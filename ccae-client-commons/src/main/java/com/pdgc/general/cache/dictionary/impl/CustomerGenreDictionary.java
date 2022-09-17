package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.customer.CustomerGenre;

public final class CustomerGenreDictionary extends DictionaryContainerBase<Long, CustomerGenre> {

    public static final String CACHE_ITEM_NAME = "CustomerGenreDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/customergenre.properties";
    
    private static final Object initializeLock = new Object();
    private static CustomerGenreDictionary instance;

    private CustomerGenreDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static CustomerGenreDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new CustomerGenreDictionary(
                    props.getProperty("FETCH_ALL_QUERY_CUSTOMER_GENRE"),
                    props.getProperty("LOOK_UP_QUERY_CUSTOMER_GENRE")
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
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("Id");
    }

    @Override
    protected CustomerGenre mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new CustomerGenre(reader.getLong("Id"), reader.getString("Name"));
    }

    @Override
    protected Map<String, Object> getLookupQueryParameters(Long key) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", key);
        return params;
    }
}
