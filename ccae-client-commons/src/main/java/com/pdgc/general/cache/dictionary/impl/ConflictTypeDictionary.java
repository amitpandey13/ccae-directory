package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.conflictcheck.structures.component.ConflictType;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;

public final class ConflictTypeDictionary extends DictionaryContainerBase<Long, ConflictType> {

    public static final String CACHE_ITEM_NAME = "ConflictTypeDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/conflicttype.properties";
    
    private static final Object initializeLock = new Object();
    private static ConflictTypeDictionary instance;

    private ConflictTypeDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static ConflictTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ConflictTypeDictionary(
                    props.getProperty("FETCH_ALL_QUERY_CONFLICT_TYPE"),
                    props.getProperty("LOOK_UP_QUERY_CONFLICT_TYPE")
                );
                MasterDataCacheManager.getInstance().addCacheItem(instance);
            }
        }
    }
    
    @Override
    public String getCacheItemName() {
        return CACHE_ITEM_NAME;
    }

    @Override
    protected Map<String, Object> getLookupQueryParameters(Long key) {
        Map<String, Object> params = new HashMap<>();
        params.put("conflictTypeId", key);
        return params;
    }

    @Override
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("id");
    }

    @Override
    protected ConflictType mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new ConflictType(reader.getLong("id"), reader.getString("Description"));
    }
}
