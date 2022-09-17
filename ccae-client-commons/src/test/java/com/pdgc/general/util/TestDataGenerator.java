package com.pdgc.general.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;

import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.maps.RightTypeCarveOutActionMap;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.lookup.maps.TerrLangMap;
import com.pdgc.general.structures.FoxProduct;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.ProductInfo;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

/**
 * Accessor to test data and structures used for unit tests.
 * 
 * This generator is a work-in-progress as more unit tests get created
 * 
 * @author Clara Hong
 *
 */
public class TestDataGenerator {

    private Product series;
    private Product season;
    private Product episode;
    private Product feature;

    private Media allMedia;
    private Media basic;

    private Territory world;
    private Territory usa;

    private Language allLanguages;
    private Language english;

    private HierarchyMapEditor<Product> productHierarchy;
    private HierarchyMapEditor<Media> mediaHierarchy;
    private HierarchyMapEditor<Territory> territoryHierarchy;
    private TwoLevelHierarchy<Language> languageHierarchy;
    private TerrLangMap terrLangMap;

    private Long productId = 1L;
    private Long mediaId = 1L;
    private Long territoryId = 1L;
    private Long languageId = 1L;

    private RightRequest exclusiveLicenseRequest;
    private RightRequest nonExclusiveLicenseRequest;
    private RightRequest holdbackRequest;

    public TestDataGenerator() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public Product getSeriesProduct() {
        if (series == null) {
            series = createSeriesProduct("Seinfeld");
        }
        return series;
    }

    public Product getSeasonProduct() {
        if (season == null) {
            season = createSeasonProduct("Seinfeld Season 01");
        }
        return season;
    }

    public Product getEpisodeProduct() {
        if (episode == null) {
            episode = createEpisodeProduct("Seinfeld Episode 01");
        }
        return episode;
    }

    public Product getFeatureProduct() {
        if (feature == null) {
            feature = createFeatureProduct("Feature Title");
        }
        return feature;
    }

    public Media getAllMedia() {
        if (allMedia == null) {
            allMedia = createMedia("All Media");
        }
        return allMedia;
    }

    public Media getBasicMedia() {
        if (basic == null) {
            basic = createMedia("Basic");
        }
        return basic;
    }
    
    public Media getPTVMedia() {
        if (basic == null) {
            basic = createMedia("PTV");
        }
        return basic;
    }


    public Territory getWorld() {
        if (world == null) {
            world = new Territory(territoryId++, "WorldWide", TerritoryLevel.OTHER);
        }
        return world;
    }

    public Territory getUSA() {
        if (usa == null) {
            usa = createTerritory("USA");
        }
        return usa;
    }

    public RightRequest getExclusiveLicenseRequest() {
        if (exclusiveLicenseRequest == null) {
            exclusiveLicenseRequest = new RightRequest(TestRightType.EXCLUSIVE_LICENSE);
        }
        return exclusiveLicenseRequest;
    }

    public RightRequest getNonExclusiveLicenseRequest() {
        if (nonExclusiveLicenseRequest == null) {
            nonExclusiveLicenseRequest = new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE);
        }
        return nonExclusiveLicenseRequest;
    }

    public RightRequest getHoldBackRequest() {
        if (holdbackRequest == null) {
            holdbackRequest = new RightRequest(TestRightType.HOLDBACK);
        }
        return holdbackRequest;
    }

    public Language getAllLanguages() {
        if (allLanguages == null) {
            allLanguages = createLanguage("All Languages");
        }
        return allLanguages;
    }

    public Language getEnglish() {
        if (english == null) {
            english = createLanguage("English");
        }
        return english;
    }

    public Map<Integer, Product> getProductDictionary() {
        Map<Integer, Product> productDictionary = new HashMap<>();
        for (Product product : getProductHierarchy().getAllElements()) {
            productDictionary.put(product.getProductId().intValue(), product);
        }
        return productDictionary;
    }

    public Map<Integer, Media> getMediaDictionary() {
        Map<Integer, Media> mediaDictionary = new HashMap<>();
        for (Media media : getMediaHierarchy().getAllElements()) {
            mediaDictionary.put(media.getMediaId().intValue(), media);
        }
        return mediaDictionary;
    }

    public Map<Integer, Territory> getTerritoryDictionary() {
        Map<Integer, Territory> territoryDictionary = new HashMap<>();
        for (Territory territory : getTerritoryHierarchy().getAllElements()) {
            territoryDictionary.put(territory.getTerritoryId().intValue(), territory);
        }
        return territoryDictionary;
    }
    
    public Equivalence<? super RightStrand> getEquivalence() {
    	return Equivalence.identity();
    }

    public Map<Integer, Language> getLanguageDictionary() {
        Map<Integer, Language> languageDictionary = new HashMap<>();
        for (Language language : getLanguageHierarchy().getAllElements()) {
            languageDictionary.put(language.getLanguageId().intValue(), language);
        }
        return languageDictionary;
    }

    public HierarchyMapEditor<Product> getProductHierarchy() {
        if (productHierarchy == null) {
            productHierarchy = new HierarchyMapEditor<Product>();

            Product seinfeldSERIES = getSeriesProduct();
            Product seinfeldSEASON1 = getSeasonProduct();

            productHierarchy.addElement(seinfeldSERIES);
            productHierarchy.addChild(seinfeldSERIES, seinfeldSEASON1);
            productHierarchy.addChild(seinfeldSEASON1, getEpisodeProduct());

            productHierarchy.addElement(getFeatureProduct());
        }
        return productHierarchy;
    }

    public HierarchyMapEditor<Media> getMediaHierarchy() {
        if (mediaHierarchy == null) {
            mediaHierarchy = new HierarchyMapEditor<Media>();

            Media allMedia = getAllMedia();
            Media basc = getBasicMedia();
            Media ptv = getPTVMedia();

            mediaHierarchy.addElement(allMedia);
            mediaHierarchy.addChild(allMedia, basc);
            mediaHierarchy.addChild(allMedia, ptv);
        }
        return mediaHierarchy;
    }

    public HierarchyMapEditor<Territory> getTerritoryHierarchy() {
        if (territoryHierarchy == null) {
            territoryHierarchy = new HierarchyMapEditor<Territory>();

            Territory world = getWorld();
            Territory usa = getUSA();

            territoryHierarchy.addElement(world);
            territoryHierarchy.addChild(world, usa);
        }
        return territoryHierarchy;
    }
    
    public TwoLevelHierarchy<Language> getLanguageHierarchy() {
        if (languageHierarchy == null) {
            return new TwoLevelHierarchy<>(
                getAllLanguages(),
                Sets.newHashSet(getEnglish())               
            );
        }
        return languageHierarchy;
    }

    public TerrLangMap getTerrLangMap() {
        if (terrLangMap == null) {
            Language english = getEnglish();

            Set<Language> worldLanguages = new HashSet<>();
            worldLanguages.add(english);

            Set<Language> usaLanguages = new HashSet<>();
            usaLanguages.add(english);

            Map<Territory, Set<Language>> terrLangSet = new HashMap<>();
            terrLangSet.put(getWorld(), worldLanguages);
            terrLangSet.put(getUSA(), usaLanguages);

            terrLangMap = new TerrLangMap(terrLangSet);
        }
        return terrLangMap;
    }

    public RightTypeImpactMatrix getRightTypeImpactMatrix() {
        Map<Pair<Long, Long>, Availability> rightTypeImpactMatrixMap = new HashMap<>();
        {
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()), Availability.YES);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()), Availability.YES);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()), Availability.YES);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.RESTRICTION.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.RESTRICTION.getRightTypeId()), Availability.NO);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.HOLDBACK.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.HOLDBACK.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.HOLDBACK.getRightTypeId()), Availability.UNSET);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);

            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.SALES_PLAN_DISTR_RIGHTS.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.SALES_PLAN_DISTR_RIGHTS.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.CATCHUP_ROLLING_4.getRightTypeId(),
                    TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId(),
                    TestRightType.CATCHUP_ROLLING_4.getRightTypeId()), Availability.NO);

        }
        return new RightTypeImpactMatrix(rightTypeImpactMatrixMap);
    }

    public RightTypeCarveOutActionMap getRightTypeCarveOutActionMap() {
        return new RightTypeCarveOutActionMap();
    }

    public RightTypeCorpAvailMap getRightTypeCorpAvailMap() {
        Map<Long, RightType> rightTypeCorpMap = new HashMap<Long, RightType>();
        {
            rightTypeCorpMap.put(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.NONEXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.TETHERED_VOD.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.CATCHUP_ROLLING_4.getRightTypeId(),
                    TestRightType.NONEXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId(),
                    TestRightType.EXCLUSIVE_CORP_AVAIL);
        }
        return new RightTypeCorpAvailMap(rightTypeCorpMap);
    }

    public Product createSeriesProduct(String name) {
        return new Product(productId++, name, ProductLevel.SERIES);
    }

    public Product createSeasonProduct(String name) {
        return new Product(productId++, name, ProductLevel.SEASON);
    }

    public Product createEpisodeProduct(String name) {
        return new Product(productId++, name, ProductLevel.EPISODE);
    }

    public Product createFeatureProduct(String name) {
        return new Product(productId++, name, ProductLevel.FEATURE);
    }

    public Media createMedia(String name) {
        return new Media(mediaId++, name);
    }

    public Territory createTerritory(String name) {
        return new Territory(territoryId++, name, TerritoryLevel.COUNTRY);
    }

    public Language createLanguage(String name) {
        return new Language(languageId++, name);
    }

    public Term getTerm() {
        return new Term(LocalDate.of(2010, 1, 1), LocalDate.of(2012, 12, 31));
    }
    
    public static FoxProduct convertToFoxProduct(Product product, ProductInfo productInfo) {
        return FoxProduct.builder()
            .productId(product.getProductId())
            .productLevel(product.getProductLevel())
            .title(product.getTitle())
            .productInfo(productInfo)
            .build();
          
    }
}
