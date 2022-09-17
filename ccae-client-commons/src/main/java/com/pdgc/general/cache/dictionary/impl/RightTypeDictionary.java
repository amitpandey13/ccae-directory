package com.pdgc.general.cache.dictionary.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.ICacheManager;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.cache.dictionary.IDictionaryContainer;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.structures.FoxRestriction;
import com.pdgc.general.structures.RestrictionCode;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.classificationEnums.RightTypeType;

public class RightTypeDictionary implements IDictionaryContainer<Long, RightType> {

    public static final String CACHE_ITEM_NAME = "RightTypeDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/righttype.properties";
    
    private static final Object initializeLock = new Object();
    private static RightTypeDictionary instance;

    private RightTypeTemplateDictionary templateDictionary;

    private RightTypeDictionary(String fetchAllQuery, String lookupQuery) {
        templateDictionary = new RightTypeTemplateDictionary(
            fetchAllQuery,
            lookupQuery
        );
    }

    public static RightTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new RightTypeDictionary(
                    props.getProperty("FETCH_ALL_QUERY_RIGHT_TYPE"),
                    props.getProperty("LOOK_UP_QUERY_RIGHT_TYPE")
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
    public void populateAsync() {
        templateDictionary.populateAsync();
    }

    @Override
    public void populate() {
        templateDictionary.populate();
    }

    @Override
    public RightType get(Long key) {
        return templateDictionary.get(key);
    }

    public RightType get(Long key, Integer episodeLimit) {
        RightType rightType = templateDictionary.get(key);

        if (rightType.allowsEpisodeLimit()) {
            return rightType.toBuilder().episodeLimit(episodeLimit).build();
        }

        return rightType;
    }

    @Override
    public void refresh(Long key) {
        templateDictionary.refresh(key);
    }

    @Override
    public void fullRefresh() {
        clear();
    }

    @Override
    public void clear() {
        templateDictionary.clear();
    }
    
	@Override
	public boolean isPopulated() {
		return templateDictionary.isPopulated();
	}

    @Override
    public Collection<ICacheManager> getUpstreamCacheItems() {
        return templateDictionary.getUpstreamCacheItems();
    }

    class RightTypeTemplateDictionary extends DictionaryContainerBase<Long, RightType> {

        private RightTypeTemplateDictionary(String fetchAllQuery, String lookupQuery) {
            super(fetchAllQuery, lookupQuery);
        }
        
        @Override
        public String getCacheItemName() {
            return null;
        }

        @Override
        protected Map<String, Object> getLookupQueryParameters(Long key) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", key);
            return params;
        }

        @Override
        protected Long mapToKey(DataRow reader) {
            return reader.getLong("Id");
        }

        @Override
        protected RightType mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
            //TODO: db probably needs to be updated to stop holding these corpAvail/holdback/license flags
            //and instead hold a value that better corresponds with rightTypeType
            return new FoxRestriction(
                reader.getLong("Id"),
                reader.getString("Name"),
                reader.getString("shortcode"),
                RightTypeType.getRightTypeType(reader.getInteger("righttypetype")),
                reader.getBoolean("allowsEpisodeLimit"),
                0,
                RestrictionCode.byValue(reader.getInteger("restrictionCode"))
            );
        }
    }
}
