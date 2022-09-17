package com.pdgc.general.cache.hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.ProductDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.util.ProductToHierarchy;
import com.pdgc.general.util.extensionMethods.hierarchyMap.HierarchyMapExtensions;

public final class ProductHierarchyManager extends LookupContainerManagerBase<Map<Long, IReadOnlyHMap<Long>>, Map<Long, IReadOnlyHMap<Product>>> {

    public static final String CACHE_ITEM_NAME = "ProductHierarchy";
    
    private static final String queryFile = "queries/cache/hierarchy/producthierarchy.properties";
    
    private static final Object initializeLock = new Object();
    private static ProductHierarchyManager instance;

    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private ProductDictionary productDictionary;
    
    private ProductHierarchyManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        productDictionary = ProductDictionary.getInstance();
        upstreamCaches.add(productDictionary);
    }

    public static ProductHierarchyManager getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ProductHierarchyManager(
                    props.getProperty("FETCH_FOR_HIERARCHY_PRODUCT")
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
    public Map<Long, IReadOnlyHMap<Long>> pullDataContainer() {
        // hierarchyId as key in this map
        HashMap<Long, HierarchyMapEditor<Long>> hierarchies = new HashMap<>();
        
        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Long product = reader.getLong("productid");
            Long hierarchyId = reader.getLong("hierarchyid");
            if (product != null) {
                // if new hierarchyId, initialize HierarchyMapEditor
                if (!hierarchies.containsKey(hierarchyId)) {
                    hierarchies.put(hierarchyId, new HierarchyMapEditor<Long>());
                }

                hierarchies.get(hierarchyId).addElement(product);

                Long parent = reader.getLong("parentId");
                if (parent != null) {
                    hierarchies.get(hierarchyId).addParent(product, parent);
                }
            }
        }
        
        return Collections.unmodifiableMap(hierarchies);
    }

    public Map<Long, IReadOnlyHMap<Product>> buildObjectContainer(Map<Long, IReadOnlyHMap<Long>> idHierarchies) {
        Map<Long, IReadOnlyHMap<Product>> newProductHierarchies = new HashMap<>();
        
        for (Long hierarchyId : idHierarchies.keySet()) {
            newProductHierarchies.put(hierarchyId, HierarchyMapExtensions.buildObjectHierarchy(
                idHierarchies.get(hierarchyId), 
                p -> productDictionary.get(new ProductToHierarchy(p, hierarchyId)))
            );
        }
        
        return newProductHierarchies;
    }
}
