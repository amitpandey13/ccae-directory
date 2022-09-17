package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.customer.CustomerGroup;

/**
 * Build Customer Group Dictionary with Ids and Names of Groups
 *
 * @author Angela Massey
 *
 */
public final class CustomerGroupDictionary extends DictionaryContainerBase<Long, CustomerGroup> {
    
    public static final String CACHE_ITEM_NAME = "CustomerGroupDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/customergroup.properties";
    
    private static final Object initializeLock = new Object();
    private static CustomerGroupDictionary instance;

    private CustomerGroupDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static CustomerGroupDictionary getInstance() {
        synchronized (initializeLock) {
            if (instance == null) {
                initialize();
            }
        }
        return instance;
    }

    //if we haven't initialized already, get an instance of a customerGroupDictionary and add the item to our cache.
    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new CustomerGroupDictionary(
                    props.getProperty("FETCH_ALL_QUERY_CUSTOMER_GROUP"),
                    props.getProperty("LOOK_UP_QUERY_CUSTOMER_GROUP")
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
    protected CustomerGroup mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new CustomerGroup(reader.getLong("Id"), reader.getString("Name"));
    }

    @Override
    protected Map<String, Object> getLookupQueryParameters(Long key) {
        Map<String, Object> params = new HashMap<>();
        params.put("customerGroupId", key);
        return params;
    }
}
