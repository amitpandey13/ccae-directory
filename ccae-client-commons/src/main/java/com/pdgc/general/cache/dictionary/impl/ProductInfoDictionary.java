package com.pdgc.general.cache.dictionary.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.pdgc.ccae.dao.intermediateobjects.ProductInfoEntry;
import com.pdgc.db.structures.DataTable.DataRow;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.AbstractDependantDictionaryBase;
import com.pdgc.general.cache.dictionary.DictionaryContainerBase;
import com.pdgc.general.constants.SQLConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.ProductInfo;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.IntegerConversionUtil;
import com.pdgc.general.cache.dictionary.impl.*;

public class ProductInfoDictionary extends AbstractDependantDictionaryBase<Long, ProductInfo, ProductInfoEntry> {

    public static final String CACHE_ITEM_NAME = "ProductInfoDictionary";
    
    private static final String queryFile = "queries/cache/dictionary/productinfo.properties";
    
    private static final Object initializeLock = new Object();
    private static ProductInfoDictionary instance;
    
    //Dictionaries this one's object depends on
    private GenreDictionary genreDictionary;
    private ProductTypeDictionary productTypeDictionary;
    private TerritoryDictionary territoryDictionary;
    private LanguageDictionary languageDictionary;

    private ProductInfoDictionary(
        String fetchAllQuery, 
        String lookupQuery, 
        String productTypeFetchAllQuery, 
        String productTypeLookupQuery,
        String productGenreFetchAllQuery, 
        String productGenreLookupQuery
    ) {
        entryDictionary = new ProductInfoEntryDictionary(
            fetchAllQuery,
            lookupQuery,
            productTypeFetchAllQuery,
            productTypeLookupQuery,
            productGenreFetchAllQuery,
            productGenreLookupQuery
        );
        
        genreDictionary = GenreDictionary.getInstance();
        upstreamCaches.add(genreDictionary);
        
        productTypeDictionary = ProductTypeDictionary.getInstance();
        upstreamCaches.add(productTypeDictionary);
        
        territoryDictionary = TerritoryDictionary.getInstance();
        upstreamCaches.add(territoryDictionary);
        
        languageDictionary = LanguageDictionary.getInstance();
        upstreamCaches.add(languageDictionary);
    }

    public static ProductInfoDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                Properties props = SQLConstants.loadQueriesFromFile(queryFile);
                
                instance = new ProductInfoDictionary(
                    props.getProperty("FETCH_ALL_QUERY_PRODUCT_INFO"),
                    props.getProperty("LOOK_UP_QUERY_PRODUCT_INFO"),
                    props.getProperty("ONE_TO_MANY_PRODUCT_TYPE"),
                    props.getProperty("ONE_TO_MANY_PRODUCT_TYPE_LOOKUP"),
                    props.getProperty("ONE_TO_MANY_PRODUCT_GENRE"),
                    props.getProperty("ONE_TO_MANY_PRODUCT_GENRE_LOOKUP")
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
    protected ProductInfo mapEntryToObject(ProductInfoEntry productInfoEntry) {
        return ProductInfo.builder()
                .productId(productInfoEntry.getProductId())
                .productTypeDescription(CollectionsUtil.select(productInfoEntry.getProductTypeIds(), p -> productTypeDictionary.get(p)))
                .seasonNumber(productInfoEntry.getSeasonNumber())
                .episodeNumber(productInfoEntry.getEpisodeNumber())
                .episodePartNumber(productInfoEntry.getEpisodePartNumber())
                .releaseDate(productInfoEntry.getReleaseDate())
                .unitCount(productInfoEntry.getNumberOfUnits())
                .genres(CollectionsUtil.select(productInfoEntry.getGenreIds(), g -> genreDictionary.get(g)))
                .themes(CollectionsUtil.select(productInfoEntry.getThemeIds(), g -> genreDictionary.get(g)))
                .cast(productInfoEntry.getCastString())
                .director(productInfoEntry.getDirector())
                .synopsis(productInfoEntry.getSynopsis())
                .division(productInfoEntry.getDivisionCode())
                .titleId(productInfoEntry.getTitleId())
                .productionYear(productInfoEntry.getProductionYear())
                .releaseYear(productInfoEntry.getReleaseYear())
                .originCountry(productInfoEntry.getOriginCountryId() != null ? territoryDictionary.get(productInfoEntry.getOriginCountryId()).getTerritoryName() : "")
                .originLanguage(productInfoEntry.getOriginLanguageId() != null ? languageDictionary.get(productInfoEntry.getOriginLanguageId()).getLanguageName() : "")
                .runTime(productInfoEntry.getRunTime())
                .internationalAdmissions(productInfoEntry.getInternationalAdmissions())
                .usAdmissions(productInfoEntry.getUsAdmissions())
                .worldwideAdmissions(productInfoEntry.getWorldwideAdmissions())
                .internationalBoxOffice(productInfoEntry.getInternationalBoxOffice())
                .usBoxOffice(productInfoEntry.getUsBoxOffice())
                .worldwideBoxOffice(productInfoEntry.getWorldwideBoxOffice())
                .initialAirNetwork(productInfoEntry.getInitialAirNetwork())
                .mpaaRating(productInfoEntry.getMpaaRating())
                .globalFAD(productInfoEntry.getGlobalFAD())
                .usHeRelease(productInfoEntry.getUsHeRelease())
                .usInitialAir(productInfoEntry.getUsInitialAir())
                .usInitialRelease(productInfoEntry.getUsInitialRelease())
                .usTheatricalRelease(productInfoEntry.getUsTheatricalRelease())
                .internationalScreens(productInfoEntry.getInternationalScreens())
                .usScreens(productInfoEntry.getUsScreens())
                .worldwideScreens(productInfoEntry.getWorldwideScreens())
                .internationalEpisodeCount(productInfoEntry.getInternationalEpisodeCount())
                .domesticEpisodeCount(productInfoEntry.getDomesticEpisodeCount())
                .blackWhiteDesc(productInfoEntry.getBlackwhitedesc())
                .awardsAndNominations(productInfoEntry.getAwardsAndNominations())
                .build();
    }
    
    private static class ProductInfoEntryDictionary extends DictionaryContainerBase<Long, ProductInfoEntry> {

        private static final String PRODUCT_TYPE = "productType";
        private static final String PRODUCT_GENRE = "productGenre";
        
        protected ProductInfoEntryDictionary(
            String fetchAllQuery, 
            String lookupQuery, 
            String productTypeFetchAllQuery, 
            String productTypeLookupQuery,
            String productGenreFetchAllQuery, 
            String productGenreLookupQuery
        ) {
            super(fetchAllQuery, lookupQuery, createOneToManyQueries(
                productTypeFetchAllQuery, 
                productTypeLookupQuery,
                productGenreFetchAllQuery, 
                productGenreLookupQuery
            ));
        }
        
        private static Collection<OneToManyQuery<Long, ?>> createOneToManyQueries(
        		 String productTypeFetchAllQuery, 
                 String productTypeLookupQuery,
                 String productGenreFetchAllQuery, 
                 String productGenreLookupQuery
        ) {
            List<OneToManyQuery<Long, ?>> queries = new ArrayList<>();
            queries.add(new ProductTypeQuery(productTypeFetchAllQuery, productTypeLookupQuery));
            queries.add(new ProductGenreQuery(productGenreFetchAllQuery, productGenreLookupQuery));
            return queries;
        }
        
        @Override
        public String getCacheItemName() {
            return null;
        }
        
        @Override
        protected Map<String, Object> getLookupQueryParameters(Long key) {
            Map<String, Object> params = new HashMap<>();
            params.put("productId", key);
            
            return params;
        }

        @Override
        protected Long mapToKey(DataRow reader) {
            return reader.getLong("id");
        }

        @Override
        protected ProductInfoEntry mapToValue(DataRow reader, Map<String, Collection<Object>> oneToManyResults) {
            Collection<GenreKey> genreIds = new ArrayList<>();
            Collection<GenreKey> themeIds = new ArrayList<>();
            Collection<KeyWithBusinessUnit<String>> productTypeIds = new HashSet<>();
            
            if (oneToManyResults != null) {
                for (Entry<String, Collection<Object>> oneToManyResult : oneToManyResults.entrySet()) {
                    switch (oneToManyResult.getKey()) {
                        case PRODUCT_TYPE:
                            for (Object obj : oneToManyResult.getValue()) {
                                productTypeIds.add(castToProductTypeId(obj));
                            }
                            break;
                        case PRODUCT_GENRE:
                            for (Object obj : oneToManyResult.getValue()) {
                                GenreKey genreKey = (GenreKey)obj;
                                if (IntegerConversionUtil.longEquals(genreKey.getGenreType(), Constants.GENRE_TYPE_GENRE)) {
                                    genreIds.add(genreKey);
                                } else if (IntegerConversionUtil.longEquals(genreKey.getGenreType(), Constants.GENRE_TYPE_THEME)) {
                                    themeIds.add(genreKey);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            
            return ProductInfoEntry.builder()
                .productId(reader.getLong("id"))
                .productionYear(reader.getString("productionYear"))
                .releaseYear(reader.getInteger("releaseYear"))
                .runTime(reader.getInteger("runTime"))
                .title(reader.getString("title"))
                .titleId(reader.getString("titleId"))
                .productTypeCode(reader.getString("productTypeCode"))
                .productLevel(reader.getInteger("productLevel"))
                .seasonNumber(reader.getInteger("seasonNumber"))
                .episodeNumber(reader.getInteger("episodeNumber"))
                .episodePartNumber(reader.getInteger("episodePartNumber"))
                .releaseDate(reader.getDate("releaseDate"))
                .numberOfUnits(reader.getInteger("unitCount"))
                .castString(reader.getString("Cast"))
                .director(reader.getString("director"))
                .synopsis(reader.getString("synopsisText"))
                .divisionCode(reader.getString("divisionCode"))
                .originCountryId(reader.getLong("originCountry"))
                .originLanguageId(reader.getLong("originLanguage"))
                .internationalAdmissions(reader.getLong("international_admissions"))
                .usAdmissions(reader.getLong("us_admissions"))
                .worldwideAdmissions(reader.getLong("ww_admissions"))
                .internationalBoxOffice(reader.getLong("intl_box_office"))
                .usBoxOffice(reader.getLong("us_box_office"))
                .worldwideBoxOffice(reader.getLong("ww_box_office"))
                .initialAirNetwork(reader.getString("initialAirNetwork"))
                .mpaaRating(reader.getString("mpaaRatingCode"))
                .globalFAD(reader.getDate("global_fad"))
                .usHeRelease(reader.getDate("us_he_release"))
                .usInitialAir(reader.getDate("us_initial_air"))
                .usInitialRelease(reader.getDate("us_initial_release"))
                .usTheatricalRelease(reader.getDate("us_theatrical_release"))
                .internationalScreens(reader.getLong("international_screens"))
                .usScreens(reader.getLong("us_screens"))
                .worldwideScreens(reader.getLong("ww_screens"))
                .internationalEpisodeCount(reader.getString("international_episode_count"))
                .domesticEpisodeCount(reader.getString("domestic_episode_count"))
                .blackwhitedesc(reader.getString("blackwhitedesc"))
                .awardsAndNominations(reader.getString("awardDescCategoryWon"))
                .productType(reader.getString("tvdProductType"))
                .genreIds(Collections.unmodifiableCollection(genreIds))
                .themeIds(Collections.unmodifiableCollection(themeIds))
                .productTypeIds(Collections.unmodifiableCollection(productTypeIds))
                .build();
        }

        @SuppressWarnings("unchecked")
        private KeyWithBusinessUnit<String> castToProductTypeId(Object obj) {
            return (KeyWithBusinessUnit<String>)obj;
        }

        private static class ProductTypeQuery extends OneToManyQuery<Long, KeyWithBusinessUnit<String>> {
            
            ProductTypeQuery(String fetchAllQuery, String lookupQuery) {
                super(PRODUCT_TYPE, fetchAllQuery, lookupQuery);
            }

            @Override
            protected Map<String, Object> getLookupQueryParameters(Long key) {
                Map<String, Object> params = new HashMap<>();
                params.put("productId", key);
                return params;
            }

            @Override
            protected Long mapToKey(DataRow reader) {
                return reader.getLong("product_id");
            }

            @Override
            protected KeyWithBusinessUnit<String> mapToValue(DataRow reader) {
                return new KeyWithBusinessUnit<>(
                    reader.getString("product_type_id"),
                    reader.getLong("business_unit_id")
                );
            }
        }

        private static class ProductGenreQuery extends OneToManyQuery<Long, GenreKey> {

            protected ProductGenreQuery(String fetchAllQuery, String lookupQuery) {
                super(PRODUCT_GENRE, fetchAllQuery, lookupQuery);
            }

            @Override
            protected Map<String, Object> getLookupQueryParameters(Long key) {
                Map<String, Object> params = new HashMap<>();
                params.put("productId", key);
                return params;
            }

            @Override
            protected Long mapToKey(DataRow reader) {
                return reader.getLong("product_id");
            }

            @Override
            protected GenreKey mapToValue(DataRow reader) {
                return new GenreKey(
                    reader.getString("genre_id"), 
                    reader.getLong("business_unit_id"),
                    reader.getLong("genre_type")
                );
            }
        }
    }
}
