package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.customer.CustomerType;

public final class CustomerTypeDictionary extends DictionaryContainerBase<Long, CustomerType> {

    public static final String CACHE_ITEM_NAME = "CustomerTypeDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/customertype.properties";
    
    private static final Object initializeLock = new Object();
    private static CustomerTypeDictionary instance;

    private CustomerTypeDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static CustomerTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new CustomerTypeDictionary(
                    props.getProperty("FETCH_ALL_QUERY_CUSTOMER_TYPE"),
                    props.getProperty("LOOK_UP_QUERY_CUSTOMER_TYPE")
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
        params.put("id", key);
        return params;
    }

    @Override
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("id");
    }

    @Override
    protected CustomerType mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new CustomerType(reader.getLong("id"), reader.getString("name"));
    }
}
