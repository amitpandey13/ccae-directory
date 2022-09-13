package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeAll;

import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;
import com.pdgc.avails.service.AvailsCalculation;
import com.pdgc.avails.service.AvailsPMTLGrouper;
import com.pdgc.avails.service.AvailsRunnerHelper;
import com.pdgc.avails.service.TestAvailsPMTLGrouper;
import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.calculation.AvailabilityMetaData;
import com.pdgc.avails.structures.calculation.AvailsCalculationResult;
import com.pdgc.avails.structures.calculation.CondensedAvailsCalcResult;
import com.pdgc.avails.structures.calculation.EditableAvailabilityMetaData;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.calculation.TestCorporateCalculator;
import com.pdgc.general.calculation.carveout.CarveOutImpactCalculator;
import com.pdgc.general.calculation.corporate.CorporateCalculator;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.RightTypeCarveOutActionMap;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightsource.impl.TestSalesPlanSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestSalesPlanStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.TestsHelper;

public class AvailsCalculationTest {

    protected static HierarchyMapEditor<Product> productHierarchy;
    protected static HierarchyMapEditor<Media> mediaHierarchy;
    protected static HierarchyMapEditor<Territory> territoryHierarchy;
    protected static TwoLevelHierarchy<Language> languageHierarchy;
    
    protected static RightTypeImpactMatrix rightTypeImpactMatrix;
    protected static RightTypeCorpAvailMap rightTypeCorpAvailMap;
    protected static RightTypeCarveOutActionMap rightTypeCarveOutActionMap;
    protected static CorporateCalculator corpAvailabilityCalculator;
    
    protected static Map<Integer, Product> productDictionary;
    protected static Map<Integer, Media> mediaDictionary;
    protected static Map<Integer, Territory> territoryDictionary;
    protected static Map<Integer, Language> languageDictionary;
    
    protected static Media allMedia;
    protected static Media ppv;
    protected static Media ptv;
    protected static Media ftv; 
    protected static Media basc;
    
    protected static Territory usa;
    protected static Territory mexico;
    protected static Territory canada;
    protected static Territory france;
    protected static Territory comoros;
    protected static Territory mayotte;
    protected static Territory moheli;;
    
    protected static Language english;
    protected static Language spanish;
    protected static Language french;
    
    protected static TerrLang usaeng;
    protected static TerrLang worldall;
    protected static MTL USEnglishPTV;
    protected static MTL USEnglishBASC;
    
    protected static TerrLang comoroseng;
    protected static MTL ComorosEnglishBASC;
    
    protected static TerrLang usaspanish;
    protected static MTL USSpanishPTV;
    protected static TerrLang usafrench;
    protected static MTL USFrenchPTV;
    
    protected static TerrLang mayotteeng;
    protected static MTL MayotteEnglishPTV;
    protected static MTL MayotteFrenchBASC;
    protected static TerrLang mayottefrench;
    protected static TerrLang comorosfrench;
    protected static MTL ComorosFrenchBASC;
    
    
    protected static TerrLang mexicoeng;
    protected static MTL MexicoEnglishPTV;
    
    protected static TerrLang mexicospanish;
    protected static MTL MexicoSpanishPTV;
    
    protected static MTL MayotteEnglishBASC;
    
    protected static TerrLang mohelieng;
    protected static MTL MoheliEnglishBASC;
    
    public static final Territory dummyTerritory = new Territory(-1L, "dummyTerritory", TerritoryLevel.PSEUDOTERRITORY);
    protected static final Language dummyLanguage = new Language(-2L, "dummyLanguage-2");
    protected static final TerrLang dummyTerritorydummyLanguage = new TerrLang(dummyTerritory, dummyLanguage);
    protected static final MTL dummyTerritorydummyLanguagePTV = new MTL(ptv, dummyTerritorydummyLanguage);
    
    protected static  RightRequest[] defaultRightTypes = new RightRequest[] 
    { 
        new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
        new RightRequest(TestRightType.HOLDBACK),
        new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
        new RightRequest(TestRightType.NONEXCLUSIVE_CORP_AVAIL),
        new RightRequest(TestRightType.EXCLUSIVE_CORP_AVAIL)                                    
    };
    
    protected RightRequest exclusiveCorpAvailRequest = new RightRequest(TestRightType.EXCLUSIVE_CORP_AVAIL);
    protected RightRequest exclusiveLicenseRequest = new RightRequest(TestRightType.EXCLUSIVE_LICENSE);
    protected RightRequest nonExclusiveCorpAvailRequest = new RightRequest(TestRightType.NONEXCLUSIVE_CORP_AVAIL);
    protected RightRequest nonExclusiveLicenseRequest = new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE);
    protected RightRequest holdbackRequest = new RightRequest(TestRightType.HOLDBACK);
    
    protected static Set<RightRequest> requestedLicenses = Sets.newHashSet(
        new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
        new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
        new RightRequest(TestRightType.HOLDBACK)
    );
    
    protected static Product Seinfeld_SERIES = TestsHelper.createSeries("Seinfeld");
    protected static Product Seinfeld_SEASON_1 = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04)");
    protected static Product Seinfeld_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Seinfeld 001");
    protected static Product Seinfeld_SEASON_1_EPISODE_02 = TestsHelper.createEpisode("Seinfeld 002");
    protected static Product FEATURE = TestsHelper.createFeature("ROAD TRIP");
    protected static Product GOODWIFE_SERIES = TestsHelper.createSeries("GOOD WIFE, THE");
    protected static Product GOODWIFE_SEASON_1 = TestsHelper.createSeason("GOOD WIFE, THE - SEASON 01 (09/10)");
    protected static Product GOODWIFE_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Good Wife 001");
    
    protected static Long rightGroupId = 1L;
    protected static Long rightSourceDetailId = 1L;
    
    // Template structures. To test other PMTLs or whatever, create a new object
    // using the test structures' copy constructor and then set the instance
    // fields to the desired values
    protected static Customer dummyCustomer = new CustomerBuilder(1L, 1L).customerName("DummyCustomer").build();
    protected static Customer dummyCustomer2 = new CustomerBuilder(2L, 2L).customerName("DummyCustomer2").build();
    protected static Customer dummyCustomer3 = new CustomerBuilder(3L, 3L).customerName("DummyCustomer3").build();
    protected static Customer dummyCustomer4 = new CustomerBuilder(4L, 4L).customerName("DummyCustomer4").build();
    
    protected static TestCorpSource crs12345L;
    protected static TestCorpSource crs12346L;
    protected static TestCorpSource crs12347L;
    protected static TestCorpSource crs12348L;
    protected static TestDealSource drs570796L;
    protected static TestSalesPlanSource sprs101010L;
    
    
    protected static RightType xclDistRights = TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE;
    protected static TestDistributionStrand seriesDistributionRights;
    protected static TestDistributionStrand simpleEpisodeDistributionRights;
    protected static TestRestrictionStrand simpleEpisodeRestriction;
    protected static TestDealStrand simpleEpisodeLicense;
    protected static TestSalesPlanStrand simpleEpisodeSalesPlan;
    
    protected RightType originalExclusiveLicense;
    protected RightType originalNonExclusiveLicense;
    protected RightType originalHoldBackLicense;
    
    @BeforeAll
    public static void setUp() throws IOException {
        Constants.instantiateConstants();
        
        //Media
        allMedia = Constants.ALL_MEDIA;
        ppv = TestsHelper.createMedia("PPV");
        ptv = TestsHelper.createMedia("PTV");
        ftv = TestsHelper.createMedia("FTV");
        basc = TestsHelper.createMedia("BASC");
        
        //Country
        usa = TestsHelper.createCountry("USA");
        mexico = TestsHelper.createCountry("Mexico");
        canada = TestsHelper.createCountry("Canada");
        france = TestsHelper.createCountry("France");
        comoros = TestsHelper.createCountry("Comoros");
        mayotte = TestsHelper.createCountry("Mayotte");
        moheli = TestsHelper.createCountry("Moheli");
        
        //Language
        english = TestsHelper.createLanguage("English");
        spanish = TestsHelper.createLanguage("Spanish");
        french = TestsHelper.createLanguage("French");
        
        //TerrLang
        comoroseng = new TerrLang(comoros, english);
        ComorosEnglishBASC = new MTL(basc, comoroseng);
        
        
        mayotteeng = new TerrLang(mayotte, english);
        MayotteEnglishPTV = new MTL(ptv, mayotteeng);
        MayotteEnglishBASC = new MTL(basc, mayotteeng);
        mohelieng = new TerrLang(moheli, english);
        MoheliEnglishBASC = new MTL(basc, mohelieng);
        TerrLang mayottefrench = new TerrLang(mayotte, french);
        MayotteFrenchBASC = new MTL(basc, mayottefrench);
        
        
        mexicoeng = new TerrLang(mexico, english);
        MexicoEnglishPTV = new MTL(ptv, mexicoeng);
        
        mexicospanish = new TerrLang(mexico, spanish);
        MexicoSpanishPTV = new MTL(ptv, mexicospanish);
        
        comorosfrench = new TerrLang(comoros, french);
        ComorosFrenchBASC = new MTL(basc, comorosfrench);
        
        
        usaeng = new TerrLang(usa, english);
        worldall = new TerrLang(Constants.WORLD, Constants.ALL_LANGUAGES);
        USEnglishPTV = new MTL(ptv, usaeng);
        usaspanish = new TerrLang(usa, spanish);
        USSpanishPTV = new MTL(ptv, usaspanish);
        usafrench = new TerrLang(usa, french);
        USFrenchPTV = new MTL(ptv, usafrench);
        
        USEnglishBASC = new MTL(basc, usa, english);
        
        //Right Source
        crs12345L = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12345L);
        crs12346L = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12346L);
        crs12347L = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12347L);
        crs12348L = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12348L);
        drs570796L = new TestDealSource(TestRightSourceType.DEAL, 570796L, rightSourceDetailId.toString());
        sprs101010L = new TestSalesPlanSource(TestRightSourceType.SALESPLAN, 101010L);      
        
        //RightStrands
        seriesDistributionRights = new TestDistributionStrand(              
            1L, 
            new PMTL(Seinfeld_SERIES, USEnglishPTV),
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            crs12345L, 
            xclDistRights, 
            new PMTL(Seinfeld_SERIES, USEnglishPTV),
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            1
        );

        simpleEpisodeDistributionRights = new TestDistributionStrand(               
            1L, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            crs12345L, 
            xclDistRights, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            1
        );

        simpleEpisodeRestriction = new TestRestrictionStrand(               
            1L, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            crs12347L, 
            TestRightType.RESTRICTION, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(Constants.EPOCH, Constants.PERPETUITY),
            1,
            null //not attached to any specific rights in strand.
        );
        
        simpleEpisodeLicense = new TestDealStrand(              
            2L, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV),
            new TermPeriod(
                new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)),
                TimePeriod.FULL_WEEK
            ),
            drs570796L,
            TestRightType.NONEXCLUSIVE_LICENSE, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV),
            new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)),
            true,
            null,
            null
        );
            
        simpleEpisodeSalesPlan = new TestSalesPlanStrand(
            3L, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)),
            sprs101010L,
            TestRightType.SALES_PLAN_WINDOW, 
            new PMTL(Seinfeld_SEASON_1_EPISODE_01, USEnglishPTV), 
            new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31))
        );
        
        productHierarchy = new HierarchyMapEditor<Product>();
        {
            productHierarchy.addElement(Seinfeld_SERIES);
            productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1);
            productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_01);
            productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_02);
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 003"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 004"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 005"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 006"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 007"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 008"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 009"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 010"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 011"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 012"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 013"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 014"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 015"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 016"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 017"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 018"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 019"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 020"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 021"));
            productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 022"));

            productHierarchy.addElement(FEATURE);

            productHierarchy.addElement(GOODWIFE_SERIES);
            productHierarchy.addChild(GOODWIFE_SERIES, GOODWIFE_SEASON_1);
            productHierarchy.addChild(GOODWIFE_SEASON_1, GOODWIFE_SEASON_1_EPISODE_01);
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 002"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 003"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 004"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 005"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 006"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 007"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 008"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 009"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 010"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 011"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 012"));
            productHierarchy.addChild(GOODWIFE_SEASON_1, TestsHelper.createEpisode("Good Wife 013"));
        }
        productDictionary = new HashMap<>();
        for (Product product : productHierarchy.getAllElements()) {
            productDictionary.put(product.getProductId().intValue(), product);
        }
            
        mediaHierarchy = new HierarchyMapEditor<Media>();
        {
            mediaHierarchy.addElement(allMedia);
            
            mediaHierarchy.addChild(allMedia, basc);
            
            mediaHierarchy.addChild(allMedia, ppv);
            mediaHierarchy.addChild(allMedia, ptv);
            mediaHierarchy.addChild(allMedia, ftv);
        }
        mediaDictionary = new HashMap<>();
        for (Media media : mediaHierarchy.getAllElements()) {
            mediaDictionary.put(media.getMediaId().intValue(), media);
        }

        territoryHierarchy = new HierarchyMapEditor<Territory>();
        {
            territoryHierarchy.addElement(Constants.WORLD);
            territoryHierarchy.addChild(Constants.WORLD, mexico);
            territoryHierarchy.addChild(Constants.WORLD, usa);
            territoryHierarchy.addChild(Constants.WORLD, canada);
            territoryHierarchy.addChild(Constants.WORLD, france);
            territoryHierarchy.addChild(Constants.WORLD, comoros);
            territoryHierarchy.addChild(comoros, mayotte);
            territoryHierarchy.addChild(comoros, moheli);
        }
        territoryDictionary = new HashMap<>();
        for (Territory territory : territoryHierarchy.getAllElements()) {
            territoryDictionary.put(territory.getTerritoryId().intValue(), territory);
        }
        
        languageHierarchy = new TwoLevelHierarchy<>(
            Constants.ALL_LANGUAGES,
            Sets.newHashSet(english, spanish, french)
        );
        languageDictionary = new HashMap<>();
        for (Language language : languageHierarchy.getAllElements()) {
            languageDictionary.put(language.getLanguageId().intValue(), language);
        }

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
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.HOLDBACK.getRightTypeId()),
                    Availability.UNSET);
            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_EXCLUSIVE_LICENSE.getRightTypeId()), Availability.CONDITIONAL_DEAL);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_EXCLUSIVE_LICENSE.getRightTypeId()), Availability.CONDITIONAL_DEAL);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.PRELIM_EXCLUSIVE_LICENSE.getRightTypeId()),
                    Availability.CONDITIONAL_DEAL);
            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.CONDITIONAL_DEAL);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.PRELIM_NONEXCLUSIVE_LICENSE.getRightTypeId()),
                    Availability.CONDITIONAL_DEAL);
            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_HOLDBACK.getRightTypeId()), Availability.CONDITIONAL_DEAL);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.PRELIM_HOLDBACK.getRightTypeId()), Availability.CONDITIONAL_DEAL);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.PRELIM_HOLDBACK.getRightTypeId()),
                    Availability.UNSET);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.EXPIRED_RIGHTS_RESTRICTION.getRightTypeId()), Availability.NO);

            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);

            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(
                    new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                            TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()),
                    Availability.CONDITIONAL_CORPORATE);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);

            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.UNSET);
            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK_INFO.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK_INFO.getRightTypeId()), Availability.UNSET);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
                    TestRightType.SALES_PLAN_BLOCK_INFO.getRightTypeId()), Availability.UNSET);

            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.SALES_PLAN_DISTR_RIGHTS.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
                    TestRightType.SALES_PLAN_DISTR_RIGHTS.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);           
            
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.CATCHUP_ROLLING_4.getRightTypeId(),
                    TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId()), Availability.NO);
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId(),
                    TestRightType.CATCHUP_ROLLING_4.getRightTypeId()), Availability.NO);            
            
            
            // Made up a right Id for exclusive tethered VODs....should probably
            rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.TETHERED_VOD.getRightTypeId(), TestRightType.TETHERED_VOD.getRightTypeId()), Availability.NO);
        }
        rightTypeImpactMatrix = new RightTypeImpactMatrix(rightTypeImpactMatrixMap);
        rightTypeCarveOutActionMap = new RightTypeCarveOutActionMap();
        

        Map<Long, RightType> rightTypeCorpMap = new HashMap<Long, RightType>();
        {
            rightTypeCorpMap.put(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), TestRightType.NONEXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.HOLDBACK.getRightTypeId(),TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.TETHERED_VOD.getRightTypeId(),TestRightType.EXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.CATCHUP_ROLLING_4.getRightTypeId(),TestRightType.NONEXCLUSIVE_CORP_AVAIL);
            rightTypeCorpMap.put(TestRightType.CATCHUP_BLOCK_ROLLING_4.getRightTypeId(),TestRightType.EXCLUSIVE_CORP_AVAIL);
        }
        rightTypeCorpAvailMap = new RightTypeCorpAvailMap(rightTypeCorpMap);
        
        corpAvailabilityCalculator = new TestCorporateCalculator(rightTypeImpactMatrix);
    }
    
    protected static AvailsRunParams getDefaultRunParams(
        AvailsQuery availsCriteria
    ) {
        return AvailsRunParams.baseBuilder()
            .availsCriteria(availsCriteria)
            .rightStrandEquivalence(Equivalence.identity())
            .rightTypeCorpAvailMap(rightTypeCorpAvailMap)
            .additionalRequests(requestedLicenses)
            .productHierarchy(productHierarchy)
            .mediaHierarchy(mediaHierarchy)
            .territoryHierarchy(territoryHierarchy)
            .languageHierarchy(languageHierarchy)
            .productDictionary(productDictionary::get)
            .mediaDictionary(mediaDictionary::get)
            .territoryDictionary(territoryDictionary::get)
            .languageDictionary(languageDictionary::get)
            .build();
    }

    protected static AvailsCalculationResult runAvails(
        AvailsRunParams runParams,
        Collection<? extends RightStrand> allRightStrands,
        Collection<Product> allRequestProducts,
        RightTypeImpactMatrix rightTypeImpactMatrix, 
        CorporateCalculator corpAvailabilityCalculator, 
        RightTypeCarveOutActionMap rightTypeCarveOutActionMap
    ) {
        AvailsCalculation availsCalculation = new AvailsCalculation(
            runParams,
            rightTypeImpactMatrix,
            corpAvailabilityCalculator,
            new CarveOutImpactCalculator(rightTypeCarveOutActionMap)
        );
        
        AvailsPMTLGrouper pmtlGrouper = new TestAvailsPMTLGrouper(runParams);
        
        Map<Set<LeafPMTLIdSet>, Map<String, Collection<Object>>> pmtlGroups = pmtlGrouper.createPMTLGroupMappings(
            runParams.getAvailsCriteria(),
            allRequestProducts, 
            allRightStrands,
            new HashMap<>()
        );
        
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Term>> criteriaRequestMap = new HashMap<>();
        
        for (Entry<Set<LeafPMTLIdSet>, Map<String, Collection<Object>>> pmtlGroup : pmtlGroups.entrySet()) {
            Set<CriteriaSource> criteriaSources = pmtlGrouper.getCriteriaSources(pmtlGroup.getValue());
            Collection<RightStrand> rightStrands = pmtlGrouper.getRightStrands(pmtlGroup.getValue());
            
            Map<RightRequest, Term> requestTermMap = AvailsRunnerHelper.getRequestTermMap(
                criteriaSources, 
                runParams.getAvailsCriteria()
            );
            
            criteriaRequestMap.put(pmtlGroup.getKey(), requestTermMap);
            
            availsCalculation.calculateAvailability(
                pmtlGroup.getKey(), 
                requestTermMap, 
                rightStrands
            );
        }
                
        return getFilledResults(availsCalculation, criteriaRequestMap);
    }

    protected static AvailsCalculationResult getFilledResults(
        AvailsCalculation availsCalculation,
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Term>> criteriaRequestMap
    ) {
        
        AvailsCalculationResult origResult = availsCalculation.getAvailsCalcResult();
        AvailsQuery availsCriteria = availsCalculation.getRunParams().getAvailsCriteria();
        RightTypeCorpAvailMap rightTypeCorpAvailMap = availsCalculation.getRunParams().getRightTypeCorpAvailMap();
        
        AvailabilityMetaData omissiveResult = new EditableAvailabilityMetaData(availsCalculation.getRunParams().getRightStrandEquivalence());
        
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>>> filledCalcResults = new HashMap<>();
        for (Entry<Set<LeafPMTLIdSet>, Map<RightRequest, Term>> pmtlEntry : criteriaRequestMap.entrySet()) {
            
            Map<RightRequest, Term> allRequestTerms = new HashMap<>();
            {
                Map<RightType, Term> corpRequestTerms = new HashMap<>();
    
                for (Entry<RightRequest, Term> requestEntry : pmtlEntry.getValue().entrySet()) {
                    allRequestTerms.merge(
                        requestEntry.getKey(), 
                        requestEntry.getValue(),
                        Term::getUnion
                    );
                    corpRequestTerms.merge(
                        rightTypeCorpAvailMap.getRequiredCorpAvailRightType(requestEntry.getKey().getRightType()),
                        requestEntry.getValue(),
                        Term::getUnion
                    );
                }
    
                for (RightRequest licenseRequest : requestedLicenses) {
                    allRequestTerms.merge(
                        licenseRequest,
                        availsCriteria.getEvaluatedPrimaryTerm(),
                        Term::getUnion
                    );
                    corpRequestTerms.merge(
                        rightTypeCorpAvailMap.getRequiredCorpAvailRightType(licenseRequest.getRightType()),
                        availsCriteria.getEvaluatedPrimaryTerm(),
                        Term::getUnion
                    );
                }
                
                for (Entry<RightType, Term> corpTypeEntry : corpRequestTerms.entrySet()) {
                    allRequestTerms.put(
                        AvailsCalculation.getCorpRequest(corpTypeEntry.getKey()),
                        corpTypeEntry.getValue()
                    );
                }
            }
            
            Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>> oldRequestMap = origResult.getCalcResults().get(pmtlEntry.getKey());
            if (oldRequestMap == null) {
                oldRequestMap = new HashMap<>();
            }
            
            for (Entry<RightRequest, Term> requestEntry : allRequestTerms.entrySet()) {
                Map<Term, Map<TimePeriod, AvailabilityMetaData>> oldTermMap = oldRequestMap.get(requestEntry.getKey());
                if (oldTermMap == null) {
                    oldTermMap = new HashMap<>();
                }
                
                Set<Term> allTerms = new HashSet<>(oldTermMap.keySet());
                allTerms.addAll(DateTimeUtil.findGapTerms(
                    oldTermMap.keySet(), 
                    requestEntry.getValue()
                ));
                
                for (Term term : allTerms) {
                    Map<TimePeriod, AvailabilityMetaData> oldPeriodMap = oldTermMap.get(term);
                    if (oldPeriodMap == null) {
                        oldPeriodMap = new HashMap<>();
                    }
                    
                    TimePeriod gapPeriod = TimePeriod.subtractPeriods(
                        requestEntry.getKey().getTimePeriod(), 
                        TimePeriod.unionPeriods(oldPeriodMap.keySet())
                    );
                    
                    if (!gapPeriod.isEmpty()) {
                        filledCalcResults.computeIfAbsent(pmtlEntry.getKey(), k -> new HashMap<>())
                            .computeIfAbsent(requestEntry.getKey(), k -> new HashMap<>())
                            .computeIfAbsent(term, k -> new HashMap<>())
                            .put(gapPeriod, omissiveResult);
                    }
                    
                    for (Entry<TimePeriod, AvailabilityMetaData> periodEntry : oldPeriodMap.entrySet()) {
                        filledCalcResults.computeIfAbsent(pmtlEntry.getKey(), k -> new HashMap<>())
                        .computeIfAbsent(requestEntry.getKey(), k -> new HashMap<>())
                        .computeIfAbsent(term, k -> new HashMap<>())
                        .put(periodEntry.getKey(), periodEntry.getValue());
                    }
                }
            }
        }
        
        return new CondensedAvailsCalcResult(new AvailsCalculationResult(
            filledCalcResults, 
            origResult.getInfoOnlyStrands(),
            origResult.getCorpResults()
        )).getFullAvailsCalcResult();        
    }
    
    //uses the default hierarchies
    protected static Set<Set<LeafPMTLIdSet>> getMappedPMTLIdSets(
        Collection<Set<LeafPMTLIdSet>> existingPMTLIdSets,
        Product product, 
        Media media, 
        Territory territory, 
        Language language
    ) {
        return getMappedPMTLIdSets(
            existingPMTLIdSets,
            product,
            media,
            territory,
            language,
            productHierarchy,
            mediaHierarchy,
            territoryHierarchy,
            languageHierarchy
        );
    }
    
    /**
     * Gets the pmtlIdSets in the list that encompass the given pmtl
     * @param existingPMTLIdSets
     * @param product
     * @param media
     * @param territory
     * @param language
     * @param productHierarchy
     * @param mediaHierarchy
     * @param territoryHierarchy
     * @param terrLangMap
     * @return
     */
    protected static Set<Set<LeafPMTLIdSet>> getMappedPMTLIdSets(
        Collection<Set<LeafPMTLIdSet>> existingPMTLIdSets,
        Product product, 
        Media media, 
        Territory territory, 
        Language language,
        IReadOnlyHMap<Product> productHierarchy,
        IReadOnlyHMap<Media> mediaHierarchy,
        IReadOnlyHMap<Territory> territoryHierarchy,
        IReadOnlyHMap<Language> languageHierarchy
    ) {
        Set<Integer> products = new HashSet<>();
        Set<Integer> medias = new HashSet<>();
        Set<Integer> territories = new HashSet<>();
        Set<Integer> languages = new HashSet<>();
        
        for (Product leafProduct : productHierarchy.getLeaves(product)) {
            products.add(leafProduct.getProductId().intValue());
        }
        for (Media leafMedia : mediaHierarchy.getLeaves(media)) {
            medias.add(leafMedia.getMediaId().intValue());
        }
        for (Territory leafTerritory : territoryHierarchy.getLeaves(territory)) {
            territories.add(leafTerritory.getTerritoryId().intValue());
        }
        for (Language leafLanguage : languageHierarchy.getLeaves(language)) {
            languages.add(leafLanguage.getLanguageId().intValue());
        }
        
        Set<Set<LeafPMTLIdSet>> matchingSets = new HashSet<>();
        for (Set<LeafPMTLIdSet> existingSet : existingPMTLIdSets) {
            for (LeafPMTLIdSet pmtl : existingSet) {
                if (pmtl.getProductIds().containsAll(products)
                    && pmtl.getMediaIds().containsAll(medias)
                    && pmtl.getTerritoryIds().containsAll(territories)
                    && pmtl.getLanguageIds().containsAll(languages)
                ) {
                    matchingSets.add(existingSet);
                    break;
                }
            }
        }
        
        return matchingSets;
    }
    
    protected static Collection<Term> getMappedTerms(
        Collection<Term> existingTerms,
        Term relevantTerm
    ) {
        return CollectionsUtil.where(
            existingTerms, 
            tp -> Term.hasIntersection(tp, relevantTerm)
        );
    }

    protected static Collection<TimePeriod> getMappedTimePeriods(
        Collection<TimePeriod> existingTimePeriods,
        TimePeriod relevantTimePeriod
    ) {
        return CollectionsUtil.where(
            existingTimePeriods, 
            tp -> TimePeriod.hasIntersection(tp, relevantTimePeriod)
        );
    }
    
    protected void validatePMTLTR(
        Availability targetAvailability,
        AvailsCalculationResult calcResult,
        Set<LeafPMTLIdSet> pmtlGroup,
        RightRequest request,
        Term relevantTerm,
        TimePeriod relevantPeriod
    ) {
        Collection<Term> mappedTerms = getMappedTerms(
            calcResult.getCalcResults().get(pmtlGroup).get(request).keySet(),
            relevantTerm
        );
        
        for (Term term : mappedTerms) {
            Collection<TimePeriod> mappedPeriods = getMappedTimePeriods(
                calcResult.getCalcResults().get(pmtlGroup).get(request).get(term).keySet(),
                relevantPeriod
            );
            
            for (TimePeriod timePeriod : mappedPeriods) {
                AvailabilityMetaData result = calcResult.getCalcResults().get(pmtlGroup).get(request).get(term).get(timePeriod);
                assertEquals(targetAvailability, result.getAvailabilityResult().availability);
            }
        }
    }
}
