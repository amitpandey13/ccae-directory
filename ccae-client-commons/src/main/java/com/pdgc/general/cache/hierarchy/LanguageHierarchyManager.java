package com.pdgc.general.cache.hierarchy;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.db.DBUtil;
import com.pdgc.db.structures.DataTable;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.LanguageDictionary;
import com.pdgc.general.cache.lookupcontainer.LookupContainerManagerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;
import com.pdgc.general.util.CollectionsUtil;

public final class LanguageHierarchyManager extends LookupContainerManagerBase<InactiveTolerantTwoLevelHierarchy<Long>, InactiveTolerantTwoLevelHierarchy<Language>> {

    public static final String CACHE_ITEM_NAME = "LanguageHierarchy";
    
    private static final String queryFile = "queries/cache/hierarchy/languagehierarchy.properties";
    
    private static final Object initializeLock = new Object();
    private static LanguageHierarchyManager instance;
    
    private String fetchQuery;
    
    //Dictionaries this one's object depends on
    private LanguageDictionary languageDictionary;
    
    private LanguageHierarchyManager(String fetchQuery) {
        this.fetchQuery = fetchQuery;
        
        languageDictionary = LanguageDictionary.getInstance();
        upstreamCaches.add(languageDictionary);
    }
    
    public static LanguageHierarchyManager getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
    
    public static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new LanguageHierarchyManager(
                    props.getProperty("FETCH_FOR_HIERARCHY_LANGUAGE")
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
    public InactiveTolerantTwoLevelHierarchy<Long> pullDataContainer() {
        Set<Long> activeIds = new HashSet<>();
        Set<Long> inactiveIds = new HashSet<>();
        
        DataTable dt = DBUtil.READ_ONLY_CONNECTION.executeQuery(fetchQuery);
        for (DataRow reader : dt.getRows()) {
            Long languageId = reader.getLong("id");
            if (languageId != null && !languageId.equals(Constants.ALL_LANGUAGES.getLanguageId())) {
                if (reader.getBoolean("inactive")) {
                    inactiveIds.add(languageId);
                } else {
                    activeIds.add(languageId);
                }
            }
        }
        
        inactiveIds.remove(Constants.ALL_LANGUAGES.getLanguageId());
        
        return new InactiveTolerantTwoLevelHierarchy<>(
            Constants.ALL_LANGUAGES.getLanguageId(),
            activeIds,
            inactiveIds
        );
    }
    
    @Override
    public InactiveTolerantTwoLevelHierarchy<Language> buildObjectContainer(InactiveTolerantTwoLevelHierarchy<Long> idMappings) {
        Set<Language> activeLanguages = CollectionsUtil.select(
                idMappings.getAllChildren(), languageDictionary::get, Collectors.toSet());
        Set<Language> inactiveLanguages = CollectionsUtil.select(
                idMappings.getInactiveChildren(), languageDictionary::get, Collectors.toSet());
        
        return new InactiveTolerantTwoLevelHierarchy<>(
            Constants.ALL_LANGUAGES,
            activeLanguages,
            inactiveLanguages
        );
    }
}
