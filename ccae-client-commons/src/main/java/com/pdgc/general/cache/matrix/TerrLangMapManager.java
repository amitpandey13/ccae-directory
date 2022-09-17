package com.pdgc.general.cache.matrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.LanguageDictionary;
import com.pdgc.general.cache.dictionary.impl.TerritoryDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.maps.TerrLangMap;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.util.CollectionsUtil;

public final class TerrLangMapManager extends LookupContainerManagerBase<Map<Long, Set<Long>>, TerrLangMap> {

    public static final String CACHE_ITEM_NAME = "TerrLangMap";
    
    private static final String queryFile = "queries/cache/matrix/terrlangmap.properties";
    
    private static final Object initializeLock = new Object();
    private static TerrLangMapManager instance;

    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private TerritoryDictionary territoryDictionary;
    private LanguageDictionary languageDictionary;
    
    private TerrLangMapManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        territoryDictionary = TerritoryDictionary.getInstance();
        upstreamCaches.add(territoryDictionary);
        
        languageDictionary = LanguageDictionary.getInstance();
        upstreamCaches.add(languageDictionary);
    }

    public static TerrLangMapManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new TerrLangMapManager(
                    props.getProperty("FETCH_FOR_MAP_TERRLANG")
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
    public Map<Long, Set<Long>> pullDataContainer() {
        Map<Long, Set<Long>> terrLangMapping = new HashMap<>();

        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Long territory = reader.getLong("territoryid");
            Long language = reader.getLong("languageid");
            
            terrLangMapping.computeIfAbsent(territory, k -> new HashSet<>())
                .add(language);
        }
        
        return terrLangMapping;
    }
    
    @Override
    public TerrLangMap buildObjectContainer(Map<Long, Set<Long>> idMappings) {
        Map<Territory, Set<Language>> newMapping = new HashMap<>();
        for (Entry<Long, Set<Long>> entry : idMappings.entrySet()) {
            newMapping.put(
                territoryDictionary.get(entry.getKey()), 
                CollectionsUtil.select(entry.getValue(), languageDictionary::get, Collectors.toSet())
            );
        }
        
        return new TerrLangMap(newMapping);
    }
}
