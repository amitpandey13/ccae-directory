package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;

/**
 * @author Vishal Raut
 */
public final class ProductTypeDictionary extends DictionaryContainerBase<KeyWithBusinessUnit<String>, String> {

    public static final String CACHE_ITEM_NAME = "ProductTypeDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/producttype.properties";
    
    private static final Object initializeLock = new Object();
    private static ProductTypeDictionary instance;

    private ProductTypeDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static ProductTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ProductTypeDictionary(
                    props.getProperty("FETCH_ALL_QUERY_PRODUCT_TYPE"),
                    props.getProperty("LOOK_UP_QUERY_PRODUCT_TYPE")
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
    protected Map<String, Object> getLookupQueryParameters(KeyWithBusinessUnit<String> key) {
        Map<String, Object> params = new HashMap<>();
        params.put("productTypeId", key.getId());
        params.put("businessUnitId", key.getBusinessUnitId());
        return params;
    }

    @Override
    protected KeyWithBusinessUnit<String> mapToKey(DataRow reader) {
        return new KeyWithBusinessUnit<String>(reader.getString("id"), reader.getLong("business_unit_id"));
    }

    @Override
    protected String mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return reader.getString("description");
    }

}
