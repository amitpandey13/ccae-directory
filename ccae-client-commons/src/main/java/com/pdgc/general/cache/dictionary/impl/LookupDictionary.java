package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Lookup;

public final class LookupDictionary extends DictionaryContainerBase<Long, Lookup> {

    public static final String CACHE_ITEM_NAME = "LookupDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/lookup.properties";
    
    private static final Object initializeLock = new Object();
    private static LookupDictionary instance;

    private LookupDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static LookupDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new LookupDictionary(
                    props.getProperty("FETCH_ALL_QUERY_LOOKUP"),
                    props.getProperty("LOOK_UP_QUERY_LOOKUP")
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
        params.put("lookupId", key);
        return params;
    }

    @Override
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("id");
    }

    @Override
    protected Lookup mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new Lookup(
            reader.getLong("id"), 
            reader.getLong("lookuptypeid"), 
            reader.getString("name"), 
            reader.getString("code")
        );
    }

}
