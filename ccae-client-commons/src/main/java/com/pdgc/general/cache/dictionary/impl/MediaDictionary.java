package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.Media;

public class MediaDictionary extends DictionaryContainerBase<Long, Media> {

    public static final String CACHE_ITEM_NAME = "MediaDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/media.properties";
    
    private static final Object initializeLock = new Object();
    private static MediaDictionary instance;

    private MediaDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static MediaDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new MediaDictionary(
                    props.getProperty("FETCH_ALL_QUERY_MEDIA"),
                    props.getProperty("LOOK_UP_QUERY_MEDIA")
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
        params.put("mediaId", key);
        return params;
    }

    @Override
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("id");
    }

    @Override
    protected Media mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        return new Media(reader.getLong("id"), reader.getString("name"), reader.getString("shortName"));
    }

}
