package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;

public final class GenreDictionary extends DictionaryContainerBase<GenreKey, String> {

    public static final String CACHE_ITEM_NAME = "GenreDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/genre.properties";
    
    private static final Object initializeLock = new Object();
    private static GenreDictionary instance;

    private GenreDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static GenreDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new GenreDictionary(
                    props.getProperty("FETCH_ALL_QUERY_GENRE"),
                    props.getProperty("LOOK_UP_QUERY_GENRE")
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
    protected Map<String, Object> getLookupQueryParameters(GenreKey key) {
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("genreId", key.getId());
        queryParameters.put("businessUnitId", key.getBusinessUnitId());
        queryParameters.put("genreType", key.getGenreType());
        return queryParameters;
    }

    @Override
    protected GenreKey mapToKey(DataRow reader) {
        return new GenreKey(reader.getString("id"), reader.getLong("business_unit_id"), reader.getLong("genre_type"));
    }

    @Override
    protected String mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return reader.getString("description");
    }

}
