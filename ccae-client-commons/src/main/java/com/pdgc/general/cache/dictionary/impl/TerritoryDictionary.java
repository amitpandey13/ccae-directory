package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;

/**
 * @author Vishal Raut
 */
public class TerritoryDictionary extends DictionaryContainerBase<Long, Territory> {

    public static final String CACHE_ITEM_NAME = "TerritoryDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/territory.properties";
    
    private static final Object initializeLock = new Object();
    private static TerritoryDictionary instance;

    private TerritoryDictionary(String fetchAllQuery, String lookupQuery) {
        super(fetchAllQuery, lookupQuery);
    }

    public static TerritoryDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new TerritoryDictionary(
                    props.getProperty("FETCH_ALL_QUERY_TERRITORY"),
                    props.getProperty("LOOK_UP_QUERY_TERRITORY")
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
        params.put("territoryId", key);
        return params;
    }

    @Override
    protected Long mapToKey(DataRow reader) {
        return reader.getLong("id");
    }

    @Override
    protected Territory mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
        Long territoryId = reader.getLong("id");
        Long parentId = reader.getLong("parentId");
        TerritoryLevel territoryLevel;
        if (parentId == null) {
            territoryLevel = TerritoryLevel.OTHER;
        } else if (Constants.WORLD.getTerritoryId().equals(parentId)) {
            territoryLevel = TerritoryLevel.COUNTRY;
        } else {
            territoryLevel = TerritoryLevel.MARKET;
        }
        return new Territory(territoryId, reader.getString("Name"), reader.getString("ShortName"), territoryLevel);
    }
}
