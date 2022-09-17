package com.pdgc.general.cache.hierarchy;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.MediaDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.extensionMethods.hierarchyMap.HierarchyMapExtensions;

public final class MediaHierarchyManager extends LookupContainerManagerBase<InactiveTolerantHierarchyMap<Long>, InactiveTolerantHierarchyMap<Media>> {
    
    public static final String CACHE_ITEM_NAME = "MediaHierarchy";
    
    private static final String queryFile = "queries/cache/hierarchy/mediahierarchy.properties";
    
    private static final Object initializeLock = new Object();
    private static MediaHierarchyManager instance;

    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private MediaDictionary mediaDictionary;
    
    private MediaHierarchyManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        mediaDictionary = MediaDictionary.getInstance();
        upstreamCaches.add(mediaDictionary);
    }

    public static MediaHierarchyManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new MediaHierarchyManager(
                    props.getProperty("FETCH_FOR_HIERARCHY_MEDIA")
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
            Long media = reader.getLong("Id");
            if (media != null) {
                hierarchy.addElement(media);
                if (reader.getBoolean("inactive")) {
                    inactiveElements.add(media);
                }

                Long parent = reader.getLong("ParentMediaId");
                if (parent != null) {
                    hierarchy.addParent(media, parent);
                }
            }
        }
        
        HierarchyMapExtensions.addRootElement(hierarchy, Constants.ALL_MEDIA.getMediaId());
        hierarchy.sanitizeTree();
        inactiveElements.remove(Constants.ALL_MEDIA.getMediaId());
        
        return new InactiveTolerantHierarchyMap<>(hierarchy, inactiveElements);
    }

    @Override
    public InactiveTolerantHierarchyMap<Media> buildObjectContainer(InactiveTolerantHierarchyMap<Long> idHierarchyMappings) {
        IReadOnlyHMap<Media> objectHierarchy = HierarchyMapExtensions.buildObjectHierarchy(
                idHierarchyMappings.getBaseHierarchy(), mediaDictionary::get);
        Set<Media> inactiveObjects = CollectionsUtil.select(
                idHierarchyMappings.getInactiveElements(), mediaDictionary::get, Collectors.toSet());
        
        return new InactiveTolerantHierarchyMap<>(objectHierarchy, inactiveObjects);
    }
}
