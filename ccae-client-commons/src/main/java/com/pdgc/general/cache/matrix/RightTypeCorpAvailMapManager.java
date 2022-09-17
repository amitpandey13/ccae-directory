package com.pdgc.general.cache.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.RightTypeDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.structures.RightType;

public final class RightTypeCorpAvailMapManager extends LookupContainerManagerBase<Map<Long, Long>, RightTypeCorpAvailMap> {

    public static final String CACHE_ITEM_NAME = "RightTypeCorpAvailMap";
    
    private static final String queryFile = "queries/cache/matrix/righttypecorpavail.properties";
    
    private static final Object initializeLock = new Object();
    private static RightTypeCorpAvailMapManager instance;

    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private RightTypeDictionary rightTypeDictionary;
    
    private RightTypeCorpAvailMapManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        rightTypeDictionary = RightTypeDictionary.getInstance();
        upstreamCaches.add(rightTypeDictionary);
    }

    public static RightTypeCorpAvailMapManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new RightTypeCorpAvailMapManager(
                    props.getProperty("FETCH_RIGHT_TYPE_CORP_AVAIL_MAP")
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
    public Map<Long, Long> pullDataContainer() {
        Map<Long, Long> newMatrix = new HashMap<Long, Long>();

        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Long key = reader.getLong("rightTypeId");
            Long value = reader.getLong("requiredCorporateAvailType");
            if (key != null && value != null) {
                newMatrix.put(key, value);
            }
        }

        return newMatrix;
    }

    @Override
    public RightTypeCorpAvailMap buildObjectContainer(Map<Long, Long> idMatrix) {
        Map<Long, RightType> newMatrix = new HashMap<Long, RightType>();
        
        for (Entry<Long, Long> entry : idMatrix.entrySet()) {
            newMatrix.put(rightTypeDictionary.get(entry.getKey()).getRightTypeId(), rightTypeDictionary.get(entry.getValue()));
        }

        return new RightTypeCorpAvailMap(newMatrix);
    }
}
