package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.AbstractDependantDictionaryBase;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.FoxProduct;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.util.ProductToHierarchy;

public final class ProductDictionary extends AbstractDependantDictionaryBase<ProductToHierarchy, FoxProduct, Product> {

    public static final String CACHE_ITEM_NAME = "ProductDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/product.properties";
    
    private static final Object initializeLock = new Object();
    private static ProductDictionary instance;
    
    //Dictionaries this one's object depends on
    private ProductInfoDictionary productInfoDictionary;

    private ProductDictionary(String fetchAllQuery, String lookupQuery) {
        entryDictionary = new ProductEntryDictionary(
            fetchAllQuery,
            lookupQuery
        );
        
        productInfoDictionary = ProductInfoDictionary.getInstance(); 
        upstreamCaches.add(productInfoDictionary);
    }

    public static ProductDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ProductDictionary(
                    props.getProperty("FETCH_ALL_QUERY_HIERARCHY_PRODUCT"),
                    props.getProperty("LOOK_UP_QUERY_HIERARCHY_PRODUCT")
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
    protected FoxProduct mapEntryToObject(Product entry) {
        return new FoxProduct(
            entry.getProductId(),
            entry.getTitle(),
            entry.getProductLevel(),
            productInfoDictionary.get(entry.getProductId())
        );
    }

	@Override
	public boolean isPopulated() {
		return entryDictionary.isPopulated();
	}
    
    private class ProductEntryDictionary extends DictionaryContainerBase<ProductToHierarchy, Product> {

        protected ProductEntryDictionary(String fetchAllQuery, String lookupQuery) {
            super(fetchAllQuery, lookupQuery);
        }
        
        @Override
        public String getCacheItemName() {
            return null;
        }

        @Override
        protected Map<String, Object> getLookupQueryParameters(ProductToHierarchy key) {
            Map<String, Object> params = new HashMap<>();
            params.put("productId", key.getProductId());
            params.put("hierarchyId", key.getHierarchyId());
            return params;
        }

        @Override
        protected ProductToHierarchy mapToKey(DataRow reader) {
            return new ProductToHierarchy(
                reader.getLong("productId"),
                reader.getLong("hierarchyId")
            );
        }

        @Override
        protected Product mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
            return new Product(
                reader.getLong("productid"),
                reader.getString("title"),
                ProductLevel.valueOf(reader.getInteger("productlevel"))
            );
        }
    }
}
