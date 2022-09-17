package com.pdgc.general.cache;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.cache.dictionary.impl.ConflictTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.LanguageDictionary;
import com.pdgc.general.cache.dictionary.impl.MediaDictionary;
import com.pdgc.general.cache.dictionary.impl.RightTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.TerritoryDictionary;
import com.pdgc.general.lookup.Constants;

/**
 * Manages dictionaries, hierarchies, and matrices
 *
 * @author Linda Xu
 */
public final class MasterDataCacheManager {

	private static final Object initializeLock = new Object();
    static MasterDataCacheManager instance;

    private Set<ICacheManager> cacheItems;
    
    /**
     * Maps each cache item to other cache items it will cause to require an objectsOnlyRefresh.
     * This is based on inverting the results of ICacheManager.getUpstreamCacheItems()
     * @see ICacheManager#getUpstreamCacheItems()
     */
    private Map<ICacheManager, Collection<ICacheManager>> downstreamCacheMap;

    private OffsetDateTime initialStartTime;
    
    private MasterDataCacheManager() {
        cacheItems = Collections.synchronizedSet(new HashSet<>());
        downstreamCacheMap = new ConcurrentHashMap<>();
        initialStartTime = OffsetDateTime.now();
    }

    public static MasterDataCacheManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                instance = new MasterDataCacheManager();
            }

            //Instantiate the items the Constants rely on
            instance.addCacheItem(TerritoryDictionary.getInstance());
            instance.addCacheItem(LanguageDictionary.getInstance());
            instance.addCacheItem(MediaDictionary.getInstance());
            instance.addCacheItem(RightTypeDictionary.getInstance());
        }
    }

    @SuppressWarnings("PMD.NPathComplexity")
    public void populateAllCacheItems(boolean async) {
        
        //Ordered list of initializations that have considered 
        //which cache items depend on others
        List<ICacheManager> reordedCacheItems = reorderForRefresh(cacheItems);
        
        if (async) {
            for (ICacheManager cacheItem : reordedCacheItems) {
                cacheItem.populateAsync();
            }
        } else {
            for (ICacheManager cacheItem : reordedCacheItems) {
                cacheItem.populate();
            }
        }

        //Update the constants with the actual values from the db
        Constants.WORLD = TerritoryDictionary.getInstance().get(Constants.WORLD.getTerritoryId());
        Constants.ALL_LANGUAGES = LanguageDictionary.getInstance().get(Constants.ALL_LANGUAGES.getLanguageId());
        Constants.ALL_MEDIA = MediaDictionary.getInstance().get(Constants.ALL_MEDIA.getMediaId());
        
        if (ConflictConstants.isInitialized()) {
            ConflictConstants.NO_CORP_CONFLICT = new ConflictClass(
                    ConflictTypeDictionary.getInstance().get(ConflictConstants.NO_CORP_CONFLICT.getConflictType().getConflictId()), 
                    ConflictConstants.NO_CORP_CONFLICT.getConflictSeverity());
            ConflictConstants.NO_CONFLICT = new ConflictClass(
                    ConflictTypeDictionary.getInstance().get(ConflictConstants.NO_CONFLICT.getConflictType().getConflictId()),  
                    ConflictConstants.NO_CONFLICT.getConflictSeverity());
            ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT = new ConflictClass(
                    ConflictTypeDictionary.getInstance().get(ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT.getConflictType().getConflictId()),   
                    ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT.getConflictSeverity());
            ConflictConstants.CONDITIONAL_CORP_RIGHTS_CONFLICT = new ConflictClass(
                    ConflictTypeDictionary.getInstance().get(ConflictConstants.CONDITIONAL_CORP_RIGHTS_CONFLICT.getConflictType().getConflictId()),   
                    ConflictConstants.CONDITIONAL_CORP_RIGHTS_CONFLICT.getConflictSeverity());
            ConflictConstants.PLAYOFF_PERIOD_CONFLICT = new ConflictClass(
                    ConflictTypeDictionary.getInstance().get(ConflictConstants.PLAYOFF_PERIOD_CONFLICT.getConflictType().getConflictId()),   
                    ConflictConstants.PLAYOFF_PERIOD_CONFLICT.getConflictSeverity()); 
        }
    }

    public void addCacheItem(ICacheManager cacheItem) {
        cacheItems.add(cacheItem);
        for (ICacheManager upstream : cacheItem.getUpstreamCacheItems()) {
        	downstreamCacheMap.computeIfAbsent(upstream, k -> Collections.synchronizedSet(new HashSet<>()))
        		.add(cacheItem);
        }
    }
    
    /**
     * We can't check for existing initialization using getInstance() since that will automatically 
     * insert the cache item into the cacheItems list, so do it by the cacheItemName
     * @param cacheItemName
     * @return
     */
    public boolean isActiveCache(String cacheItemName) {
        return cacheItems.stream().anyMatch(c -> c.getCacheItemName() == cacheItemName);
    }
    
    /**
     * Returns the downstream cache items that rely on the cacheItem 
     * and will require objectsOnlyRefresh if there are changes.
     * @param cacheItem
     * @return
     * @see ICacheManager#getUpstreamCacheItems()
     * @see ICacheManager#objectsOnlyRefresh()
     */
    public Collection<ICacheManager> getDownstreamCaches(ICacheManager cacheItem) {
    	Collection<ICacheManager> downstreamCaches = downstreamCacheMap.computeIfAbsent(cacheItem, k -> Collections.synchronizedSet(new HashSet<>()));
    	return Collections.unmodifiableCollection(downstreamCaches);
    }
    
    /**
     * Returns the time when the cache was first instantiated
     * @return
     */
    public OffsetDateTime getInitialStartTime() {
    	return initialStartTime;
    }
    
    /**
     * Orders the cache items according to the dependecnies returned by getUpstreamCacheItems().
     * Therefore the a cache item will never appear earlier in the list
     * than any of the cache items it depends on 
     * @param cacheItems
     * @return
     */
    public static List<ICacheManager> reorderForRefresh(Collection<ICacheManager> cacheItems) {
        Set<ICacheManager> distinctCacheItems;
        if (cacheItems instanceof Set<?>) {
            distinctCacheItems = (Set<ICacheManager>)cacheItems;
        } else {
            distinctCacheItems = new HashSet<>(cacheItems);
        }
        
        List<ICacheManager> orderedItems = new ArrayList<>(distinctCacheItems.size());
        
        Map<ICacheManager, Collection<ICacheManager>> remainingItems = new HashMap<>();
        Collection<ICacheManager> newlyInsertedItems = new ArrayList<>();
        for (ICacheManager item : distinctCacheItems) {
            Collection<ICacheManager> relevantUpstreamCacheItems = new ArrayList<>(item.getUpstreamCacheItems());
        	relevantUpstreamCacheItems.retainAll(distinctCacheItems);
            
        	if (relevantUpstreamCacheItems.isEmpty()) {
                orderedItems.add(item);
                newlyInsertedItems.add(item);
            } else {
                remainingItems.put(item, relevantUpstreamCacheItems);
            }
        }
        
        Collection<ICacheManager> itemsToInsert = new ArrayList<>();
        while (!remainingItems.isEmpty()) {
            for (ICacheManager item : newlyInsertedItems) {
                for (Entry<ICacheManager, Collection<ICacheManager>> entry : remainingItems.entrySet()) {
                    entry.getValue().remove(item);
                }
            }
            
            itemsToInsert.clear();
            for (Entry<ICacheManager, Collection<ICacheManager>> entry : remainingItems.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    itemsToInsert.add(entry.getKey());
                }
            }
            
            newlyInsertedItems.clear();
            for (ICacheManager item : itemsToInsert) {
                remainingItems.remove(item);
                newlyInsertedItems.add(item);
                orderedItems.add(item);
            }
        }
        
        return orderedItems;
    }
}
