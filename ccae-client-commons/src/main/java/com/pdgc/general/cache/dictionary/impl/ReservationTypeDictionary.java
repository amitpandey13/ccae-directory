package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.reservationtype.ReservationType;

public final class ReservationTypeDictionary extends DictionaryContainerBase<String, ReservationType> {

    public static final String CACHE_ITEM_NAME = "ReservationTypeDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/reservationtype.properties";
    
    private static final Object initializeLock = new Object();
    private static ReservationTypeDictionary instance;

    private ReservationTypeDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static ReservationTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ReservationTypeDictionary(
                    props.getProperty("FETCH_ALL_QUERY_RESERVATION_TYPE"),
                    props.getProperty("LOOK_UP_QUERY_RESERVATION_TYPE")
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
    protected Map<String, Object> getLookupQueryParameters(String key) {
        Map<String, Object> params = new HashMap<>();
        params.put("reservationTypeId", key);
        return params;
    }

    @Override
    protected String mapToKey(DataRow reader) {
        return reader.getString("id");
    }

    @Override
    protected ReservationType mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new ReservationType(reader.getString("id"), reader.getString("name"));
    }
}
