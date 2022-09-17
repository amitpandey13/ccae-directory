package com.pdgc.general.cache.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.ICacheManager;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class DictionaryContainerBase<K, V> implements IDictionaryContainer<K, V> {

	private String fetchAllQuery;
	private String lookupQuery;
	private Collection<OneToManyQuery<K, ?>> oneToManyQueries;

	private Map<K, V> dictionary;
	private Set<K> nonExistentKeys;

	private AtomicBoolean populateCalled = new AtomicBoolean(false);

	private Collection<ICacheManager> upstreamCaches;

	protected DictionaryContainerBase(String fetchAllQuery, String lookupQuery) {
		this.fetchAllQuery = fetchAllQuery;
		this.lookupQuery = lookupQuery;
		this.oneToManyQueries = new ArrayList<>();

		init();
	}

	protected DictionaryContainerBase(String fetchAllQuery, String lookupQuery,
			Collection<OneToManyQuery<K, ?>> oneToManyQueries) {
		this.fetchAllQuery = fetchAllQuery;
		this.lookupQuery = lookupQuery;
		this.oneToManyQueries = oneToManyQueries;

		init();
	}

	private void init() {
		dictionary = new ConcurrentHashMap<>();
		nonExistentKeys = Collections.synchronizedSet(new HashSet<K>());

		upstreamCaches = new ArrayList<>();
	}

	protected abstract Map<String, Object> getLookupQueryParameters(K key);

	protected abstract K mapToKey(DataRow reader);

	protected abstract V mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults);

	@Override
	public final void populateAsync() {
		if (populateCalled.getAndSet(true)) {
			return;
		}

		try {
			List<DataTable> data = new ArrayList<>();
			Map<OneToManyQuery<K, ?>, DataTable> oneToManyResults = new HashMap<>();

			CompletableFuture<?>[] oneToManyFutures = new CompletableFuture<?>[oneToManyQueries.size() + 1];
			int i = 0;
			oneToManyFutures[i++] = DBUtil.READ_ONLY_CONNECTION.executeQueryAsync(fetchAllQuery)
					.thenAccept(dt -> data.add(dt));

			for (OneToManyQuery<K, ?> query : oneToManyQueries) {
				oneToManyFutures[i++] = DBUtil.READ_ONLY_CONNECTION.executeQueryAsync(query.fetchAllQuery)
						.thenAccept(dt -> oneToManyResults.put(query, dt));
			}

			CompletableFuture.allOf(oneToManyFutures).thenRun(() -> populateDictionary(data.get(0), oneToManyResults));
		} catch (Exception e) { // NOPMD
			populateCalled.set(false);
			throw new RuntimeException(e);
		}
	}

	@Override
	public final void populate() {
		if (populateCalled.getAndSet(true)) {
			return;
		}

		try {
			Map<OneToManyQuery<K, ?>, DataTable> oneToManyResults = new HashMap<>();
			for (OneToManyQuery<K, ?> query : oneToManyQueries) {
				oneToManyResults.put(query, DBUtil.READ_ONLY_CONNECTION.executeQuery(query.fetchAllQuery));
			}

			populateDictionary(DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchAllQuery), oneToManyResults);
		} catch (Exception e) { // NOPMD
			populateCalled.set(false);
			throw e;
		}
	}

	private void populateDictionary(DataTable mainResults, Map<OneToManyQuery<K, ?>, DataTable> oneToManyResults) {
		dictionary.clear();
		nonExistentKeys.clear();

		Map<K, Map<String, Collection<Object>>> oneToManyMappings = new HashMap<>();
		for (Entry<OneToManyQuery<K, ?>, DataTable> queryResult : oneToManyResults.entrySet()) {
			for (DataRow row : queryResult.getValue().getRows()) {
				K key = queryResult.getKey().mapToKey(row);
				Object value = queryResult.getKey().mapToValue(row);
				oneToManyMappings.computeIfAbsent(key, k -> new HashMap<>())
						.computeIfAbsent(queryResult.getKey().id, k -> new ArrayList<>()).add(value);
			}
		}

		for (DataRow row : mainResults.getRows()) {
			K key = mapToKey(row);
			dictionary.put(key, mapToValue(row, oneToManyMappings.get(key)));
		}
	}

	@Override
	public final V get(K key) {
		if (key == null || nonExistentKeys.contains(key)) {
            return null;
        } 
        
        //do not use a containsKey() check, b/c it's possible for a refresh to happen between the 
    	//containsKey() check and the actual get()
    	V value = dictionary.get(key);
    	if (value != null) {
    		return dictionary.get(key);
    	}
        
    	//Since we're missing this key, see if our dictionary is even populated, and if isn't
    	//call it so future get()s will have something to return
        if (!populateCalled.get()) {
            populateAsync();
        }
        
        Map<String, Collection<Object>> oneToManyQueryResults = new HashMap<>();
		for (OneToManyQuery<K, ?> query : oneToManyQueries) {
			DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQueryWithParameters(query.lookupQuery,
					query.getLookupQueryParameters(key));
			for (DataRow row : dt.getRows()) {
				oneToManyQueryResults.computeIfAbsent(query.id, k -> new ArrayList<>()).add(query.mapToValue(row));
			}
		}

		DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQueryWithParameters(lookupQuery, getLookupQueryParameters(key));
		value = null;
		if (dt != null && dt.getRowCount() > 0) {
			value = mapToValue(dt.getRow(0), oneToManyQueryResults);
			dictionary.put(key, value);
		} else {
			nonExistentKeys.add(key);
		}
		return value;
	}

	@Override
	public final void refresh(K key) {
		if (key == null) {
			return;
		}

		dictionary.remove(key);
		nonExistentKeys.remove(key);
	}

	@Override
	public final void fullRefresh() {
		clear();

		// Clear the flag so that the populate call will actually run
		populateCalled.set(false);

		populateAsync();
	}

	@Override
	public final void clear() {
		dictionary.clear();
		nonExistentKeys.clear();
	}

	@Override
	public String toString() {
		return this.dictionary.toString();
	}

	@Override
	public Collection<ICacheManager> getUpstreamCacheItems() {
		return upstreamCaches;
	}
	
	@Override
    public boolean isPopulated() {
    	return populateCalled.get();
    }

	/**
	 * @return a list of all the objects in this dictionary.
	 */
	public final Collection<V> getAll() {
		return this.dictionary.values();
	}

	public String getFetchAllQuery() {
		return fetchAllQuery;
	}

	public String getLookupQuery() {
		return lookupQuery;
	}

	protected abstract static class OneToManyQuery<K, O> {
		final String id;
		final String fetchAllQuery;
		final String lookupQuery;

		protected OneToManyQuery(String id, String fetchAllQuery, String lookupQuery) {
			this.id = id;
			this.fetchAllQuery = fetchAllQuery;
			this.lookupQuery = lookupQuery;
		}

		protected abstract Map<String, Object> getLookupQueryParameters(K key);

		protected abstract K mapToKey(DataRow reader);

		protected abstract O mapToValue(DataRow reader);
	}
}
