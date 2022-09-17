package com.pdgc.general.cache.hierarchy;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.TerritoryDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.extensionMethods.hierarchyMap.HierarchyMapExtensions;

public class TerritoryHierarchyManager extends LookupContainerManagerBase<InactiveTolerantHierarchyMap<Long>, InactiveTolerantHierarchyMap<Territory>> {

    public static final String CACHE_ITEM_NAME = "TerritoryHierarchy";
    
    private static final String queryFile = "queries/cache/hierarchy/territoryhierarchy.properties";
    
    private static final Object initializeLock = new Object();
    protected static TerritoryHierarchyManager instance;
    
    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private TerritoryDictionary territoryDictionary;
    
    private TerritoryHierarchyManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        territoryDictionary = TerritoryDictionary.getInstance();
        upstreamCaches.add(territoryDictionary);
    }
    
    public static TerritoryHierarchyManager getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
    
    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new TerritoryHierarchyManager(
                    props.getProperty("FETCH_FOR_HIERARCHY_TERRITORY")
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
    public InactiveTolerantHierarchyMap<Long> pullDataContainer() {
        HierarchyMapEditor<Long> hierarchy = new HierarchyMapEditor<Long>();
        Set<Long> inactiveElements = new HashSet<>();
        
        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Long territoryId = reader.getLong("childterr");
            if (territoryId != null) {
                hierarchy.addElement(territoryId);
                if (reader.getBoolean("inactive")) {
                    inactiveElements.add(territoryId);
                }
                
                Long parentId = reader.getLong("ParentTerr");
                if (parentId != null) {
                    hierarchy.addParent(territoryId, parentId);
                }
            }
        }

        HierarchyMapExtensions.addRootElement(hierarchy, Constants.WORLD.getTerritoryId());
        hierarchy.sanitizeTree();
        inactiveElements.remove(Constants.WORLD.getTerritoryId());
        
        return new InactiveTolerantHierarchyMap<>(hierarchy, inactiveElements);
    }

    @Override
    public InactiveTolerantHierarchyMap<Territory> buildObjectContainer(InactiveTolerantHierarchyMap<Long> idHierarchyMappings) {
        IReadOnlyHMap<Territory> objectHierarchy = HierarchyMapExtensions.buildObjectHierarchy(
                idHierarchyMappings.getBaseHierarchy(), territoryDictionary::get);
        Set<Territory> inactiveObjects = CollectionsUtil.select(
                idHierarchyMappings.getInactiveElements(), territoryDictionary::get, Collectors.toSet());

        return new InactiveTolerantHierarchyMap<>(objectHierarchy, inactiveObjects);
    }
}
