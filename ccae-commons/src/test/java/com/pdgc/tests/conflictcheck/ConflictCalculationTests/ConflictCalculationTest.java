package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.javatuples.Pair;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.pdgc.conflictcheck.service.ConflictCalculator;
import com.pdgc.conflictcheck.service.ConflictCheckRunner;
import com.pdgc.conflictcheck.service.OverrideApplier;
import com.pdgc.conflictcheck.service.TestConflictCheckRunner;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictType;
import com.pdgc.conflictcheck.structures.builders.TestConflictBuilder;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyContainerEquivalence;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalenceFull;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLIgnorant;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.conflictcheck.structures.result.ConflictCalculationResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.calculation.TestCorporateCalculator;
import com.pdgc.general.calculation.carveout.CarveOutImpactCalculator;
import com.pdgc.general.calculation.corporate.CorporateCalculator;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.ConflictMatrix;
import com.pdgc.general.lookup.maps.RightTypeCarveOutActionMap;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.lookup.maps.TestConflictMatrix;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.structures.customer.CustomerGenre;
import com.pdgc.general.structures.customer.CustomerType;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightsource.impl.TestSalesPlanSource;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestSalesPlanStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.PMTLUtil;
import com.pdgc.general.util.TestsHelper;
import com.pdgc.general.util.equivalenceCollections.EquivalenceSet;

public class ConflictCalculationTest {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ConflictCalculator.class);

	protected static ConflictCalculator<TestConflict> conflictCalculator;
	protected static ConflictCheckRunner<TestConflict> conflictCheckRunner;

	protected static ConflictKeyEquivalencePMTLIgnorant pmtlIgnorantConflictKeyEquivalence = new ConflictKeyEquivalencePMTLIgnorant();
	protected static ConflictKeyEquivalenceFull pmtlFullConflictKeyEquivalence = new ConflictKeyEquivalenceFull();
	
	protected static Map<Long, IReadOnlyHMap<Product>> productHierarchies;
	protected static HierarchyMapEditor<Product> productHierarchy;
	protected static HierarchyMapEditor<Media> mediaHierarchy;
	protected static HierarchyMapEditor<Territory> territoryHierarchy;
	protected static TwoLevelHierarchy<Language> languageHierarchy;

	protected static RightTypeImpactMatrix rightTypeImpactMatrix;
	protected static RightTypeCorpAvailMap rightTypeCorpAvailMap;
	protected static ConflictMatrix conflictMatrix;
	protected static RightTypeCarveOutActionMap rightTypeCarveOutActionMap;

	protected static TestConflictBuilder conflictBuilder;
	protected static CorporateCalculator corpAvailabilityCalculator;
	
	protected static Map<Integer, Product> productDictionary;
	protected static Map<Integer, Media> mediaDictionary;
	protected static Map<Integer, Territory> territoryDictionary;
	protected static Map<Integer, Language> languageDictionary;
	
	protected static Media allMedia;
	protected static Media basc;
	protected static Media ppv;
	protected static Media ptv;
	protected static Media ptvc;
	protected static Media ptvi;
	protected static Media ptvm;
	protected static Media svod;
	protected static Media svodc;
	protected static Media svodi;

	protected static Territory usa;
	protected static Territory mexico;
	protected static Territory canada;
	protected static Territory chicago;
	protected static Territory dallas;
	protected static Territory losAngeles;
	protected static Territory newYork;
	protected static Territory newOrleans;

	protected static Language english;
	protected static Language spanish;
	protected static Language french;

	protected static TerrLang usaenglish;
	protected static TerrLang worldall;
	protected static MTL USEnglishPTV;

	protected static Product Seinfeld_SERIES;
	protected static Product Seinfeld_SEASON_1;
	protected static Product Seinfeld_SEASON_1_X;
	protected static Product Seinfeld_SEASON_1_Y;
	protected static Product Seinfeld_SEASON_1_EPISODE_01;
	protected static Product Seinfeld_SEASON_1_EPISODE_02;
	protected static Product Seinfeld_SEASON_1_EPISODE_03;
	protected static Product Seinfeld_SEASON_1_EPISODE_04;
	protected static Product FEATURE;
	protected static Product GOODWIFE_SERIES;
	protected static Product GOODWIFE_SEASON_1;
	protected static Product GOODWIFE_SEASON_1_EPISODE_01;

	// Right Types
	protected static RightType rtEXDist;
	protected static RightType rtNONEXDist;
	protected static RightType rtSPWindow;
	protected static RightType rtSPBlock;

	protected static Long rightGroupId;
	protected static Long rightSourceDetailId;

	// Template structures. To test other PMTLs or whatever, create a new object
	// using the test structures' copy constructur and then set the instance
	// fields to the desired values
	protected static Customer dummyCustomer = new CustomerBuilder(1L, 1L).customerName("DummyCustomer").build();
	protected static Customer dummyCustomer2 = new CustomerBuilder(2L, 2L).customerName("DummyCustomer2").build();
	protected static Customer dummyCustomer3 = new CustomerBuilder(3L, 3L).customerName("DummyCustomer3").build();
	protected static Customer dummyCustomer4 = new CustomerBuilder(4L, 4L).customerName("DummyCustomer4").build();
	
	protected static CustomerGenre dummyCustomerGenre1 = new CustomerGenre(1L, "dummy genre 1");
	protected static CustomerGenre dummyCustomerGenre2 = new CustomerGenre(2L, "dummy genre 2");
	protected static CustomerGenre dummyCustomerGenre3 = new CustomerGenre(3L, "dummy genre 3");
	
	protected static CustomerType dummyCustomerType1 = new CustomerType(1L, "dummy type 1");
	protected static CustomerType dummyCustomerType2 = new CustomerType(2L, "dummy type 2");
	protected static CustomerType dummyCustomerType3 = new CustomerType(3L, "dummy type 3");
	
	protected static int unknownCarveOutTypeId = 13;  // This carveoutTypeId is extra spooky!
														//	 .-.
														//	 (o o) boo!
														//	 | O \
														//	  \   \
														//	   `~~~'
	
	protected static long defaultRightsGroupId;
	protected static TestCorpSource crSource;
	protected static TestDealSource drSource; // Seinfeldseason1
	protected static TestSalesPlanSource swSource;
	protected static TestCorpSource resSource; 

	protected static TestDistributionStrand seriesDistributionRights;
	protected static TestSalesPlanStrand seasonSalesWindowStrand;
	protected static TestSalesPlanStrand seasonSalesWindowBlockStrand;
	protected static TestDealStrand simpleEpisodeLicense;
	protected static TestDealStrand simpleSeasonLicense;

	// sample right strands
	static TestDealStrand sharedRightSourceNonExclusiveUSEnglishStrand;
	static TestDealStrand sharedRightSourceNonExclusiveCanadaEnglishStrand;
	static TestDealStrand sharedRightSourceNonExclusiveMexicoSpanishStrand;
	static TestDealStrand sharedRightSourceHoldbackUSEnglishStrand;

	static TestDealStrand nonExclusiveUSEnglishStrand;
	static TestDealStrand nonExclusiveCanadaEnglishStrand;
	static TestDealStrand nonExclusiveMexicoSpanishStrand;
	static TestDealStrand holdbackUSEnglishStrand;
	
	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
		ConflictConstants.instantiateConstants();
		
		allMedia = Constants.ALL_MEDIA;
		basc = TestsHelper.createMedia("BASC");
		ppv = TestsHelper.createMedia("PPV");
		ptv = TestsHelper.createMedia("PTV");
		ptvc = TestsHelper.createMedia("PTV: Cab");
		ptvi = TestsHelper.createMedia("PTV: Int");
		ptvm = TestsHelper.createMedia("PTV: Mob");
		svod = TestsHelper.createMedia("SVOD");
		svodc = TestsHelper.createMedia("SVOD: Cab");
		svodi = TestsHelper.createMedia("SVOD: Int");

		usa = TestsHelper.createCountry("USA");
		mexico = TestsHelper.createCountry("Mexico");
		canada = TestsHelper.createCountry("Canada");
		chicago = TestsHelper.createMarket("Chicago");
		dallas = TestsHelper.createMarket("Dallas-Fort Worth");
		losAngeles = TestsHelper.createMarket("Los Angeles");
		newYork = TestsHelper.createMarket("New York");
		newOrleans = TestsHelper.createMarket("New Orleans");

		english = TestsHelper.createLanguage("English");
		spanish = TestsHelper.createLanguage("Spanish");
		french = TestsHelper.createLanguage("French");

		usaenglish = new TerrLang(usa, english);
		worldall = new TerrLang(Constants.WORLD, Constants.ALL_LANGUAGES);
		USEnglishPTV = new MTL(ptv, usaenglish);

		Seinfeld_SERIES = TestsHelper.createSeries("Seinfeld");
		Seinfeld_SEASON_1 = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04)");
		Seinfeld_SEASON_1_X = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04) Ep 1-3");
		Seinfeld_SEASON_1_Y = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04) Ep 1,2,4");
		Seinfeld_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Seinfeld 001");
		Seinfeld_SEASON_1_EPISODE_02 = TestsHelper.createEpisode("Seinfeld 002");
		Seinfeld_SEASON_1_EPISODE_03 = TestsHelper.createEpisode("Seinfeld 003");
		Seinfeld_SEASON_1_EPISODE_04 = TestsHelper.createEpisode("Seinfeld 004");
		FEATURE = TestsHelper.createFeature("ROAD TRIP");
		GOODWIFE_SERIES = TestsHelper.createSeries("GOOD WIFE, THE");
		GOODWIFE_SEASON_1 = TestsHelper.createSeason("GOOD WIFE, THE - SEASON 01 (09/10)");
		GOODWIFE_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Good Wife 001");
		

		// Right Types
		rtEXDist = TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE;
		rtNONEXDist = TestRightType.DISTRIBUTION_RIGHTS_NONEXCLUSIVE;
		rtSPWindow = TestRightType.SALES_PLAN_WINDOW;
		rtSPBlock = TestRightType.SALES_PLAN_BLOCK;

		rightGroupId = 1L;
		rightSourceDetailId = 1L;

		defaultRightsGroupId = 1L;
		crSource = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12345L);
		drSource = new TestDealSource(TestRightSourceType.DEAL, 570796L, "570796");
		swSource = new TestSalesPlanSource(TestRightSourceType.SALESPLAN, 44l);
		resSource = new TestCorpSource(TestRightSourceType.RESTRICTION, 45L); 

		{
			seriesDistributionRights = new TestDistributionStrand(
				1L, 
				new PMTL(
					new AggregateProduct(Seinfeld_SERIES), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				),
				new Term(Constants.EPOCH, Constants.PERPETUITY),
				crSource,
				TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE, 
				new PMTL(
					new AggregateProduct(Seinfeld_SERIES), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				), 
				new Term(Constants.EPOCH, Constants.PERPETUITY),
				1
			);

			seasonSalesWindowStrand = new TestSalesPlanStrand(
				200L, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(ptv),
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				),
				new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2022, 12, 31)), 
				swSource, 
				rtSPWindow, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(ptv),
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				),
				new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2022, 12, 31))
			);

			seasonSalesWindowBlockStrand = new TestSalesPlanStrand(
				201L, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(basc),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				), 
				new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2022, 12, 31)), 
				swSource, 
				rtSPBlock,
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(basc),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				), 
				new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2022, 12, 31))
			);

			simpleEpisodeLicense = new TestDealStrand(
				1L, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1_EPISODE_01), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				),
				new TermPeriod(
			        new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)), 
			        TimePeriod.FULL_WEEK
			    ),
				drSource,
				TestRightType.NONEXCLUSIVE_LICENSE, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1_EPISODE_01), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				),
				new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)), 
				true,
				null,
				null
			);

			simpleSeasonLicense = new TestDealStrand(
				2L, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				),
				new TermPeriod(
    				new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)), 
    				TimePeriod.FULL_WEEK
				),
				drSource,
				TestRightType.NONEXCLUSIVE_LICENSE, 
				new PMTL(
					new AggregateProduct(Seinfeld_SEASON_1), 
					new AggregateMedia(ptv),
					new AggregateTerritory(Constants.WORLD),
					new AggregateLanguage(Constants.ALL_LANGUAGES)
				),
				new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)), 
				true,
				null,
				null
			); 

			// Populate the primary right strands
			{
				TestDealStrand tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
				tempRightStrand.setRightStrandId(tempRightStrand.getRightStrandId() + 1);
				tempRightStrand.setTerrLang(new TerrLang(
					new AggregateTerritory(canada),
					new AggregateLanguage(english)
				));
				tempRightStrand.setActualTerrLang(new TerrLang(
					new AggregateTerritory(canada),
					new AggregateLanguage(english)
				));
				sharedRightSourceNonExclusiveCanadaEnglishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
				tempRightStrand.setRightStrandId(tempRightStrand.getRightStrandId() + 3);
				tempRightStrand.setTerrLang(new TerrLang(
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				));
				tempRightStrand.setActualTerrLang(new TerrLang(
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				));
				sharedRightSourceNonExclusiveUSEnglishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
				tempRightStrand.setRightStrandId(tempRightStrand.getRightStrandId() + 2);
				tempRightStrand.setTerrLang(new TerrLang(
					new AggregateTerritory(mexico),
					new AggregateLanguage(spanish)
				));
				tempRightStrand.setActualTerrLang(new TerrLang(
					new AggregateTerritory(mexico),
					new AggregateLanguage(spanish)
				));
				sharedRightSourceNonExclusiveMexicoSpanishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
				tempRightStrand.setRightStrandId(tempRightStrand.getRightStrandId() + 4);
				tempRightStrand.setTerrLang(new TerrLang(
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				));
				tempRightStrand.setActualTerrLang(new TerrLang(
					new AggregateTerritory(usa),
					new AggregateLanguage(english)
				));
				tempRightStrand.setRightType(TestRightType.HOLDBACK);
				sharedRightSourceHoldbackUSEnglishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(sharedRightSourceNonExclusiveCanadaEnglishStrand);
				tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, drSource.getDealId(), "nonExclusiveCanadaEnglish"));
				nonExclusiveCanadaEnglishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(sharedRightSourceNonExclusiveUSEnglishStrand);
				tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, drSource.getDealId(), "nonExclusiveUSEnglish"));
				nonExclusiveUSEnglishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(sharedRightSourceNonExclusiveMexicoSpanishStrand);
				tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, drSource.getDealId(), "nonExclusiveMexicoSpanish"));
				nonExclusiveMexicoSpanishStrand = tempRightStrand;

				tempRightStrand = new TestDealStrand(sharedRightSourceHoldbackUSEnglishStrand);
				tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, drSource.getDealId(), "holdbackUSEnglish"));
				holdbackUSEnglishStrand = tempRightStrand;
			}

			productHierarchy = new HierarchyMapEditor<Product>();
			{
				productHierarchy.addElement(Seinfeld_SERIES);
				productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1);
				productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_01);
				productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_02);
				productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_03);
				productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_04);
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

				productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_X);
				productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_01);
				productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_02);
				productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_03);

				productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_Y);
				productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_01);
				productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_02);
				productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_04);

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
			//Initialize the productHierarchies map. For testing it only has a single hierarchy.
			productHierarchies = new HashMap<>();
			productHierarchies.put(1L, productHierarchy);
			
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
				mediaHierarchy.addChild(allMedia, svod);
				mediaHierarchy.addChild(ptv, ptvc);
				mediaHierarchy.addChild(ptv, ptvi);
				mediaHierarchy.addChild(ptv, ptvm);
				mediaHierarchy.addChild(svod, svodc);
				mediaHierarchy.addChild(svod, svodi);

			}
			mediaDictionary = new HashMap<>();
			for (Media media : mediaHierarchy.getAllElements()) {
				mediaDictionary.put(media.getMediaId().intValue(), media);
			}
			
			territoryHierarchy = new HierarchyMapEditor<Territory>();
			{
				territoryHierarchy.addChild(Constants.WORLD, usa);
				territoryHierarchy.addChild(Constants.WORLD, mexico);
				territoryHierarchy.addChild(Constants.WORLD, canada);
				territoryHierarchy.addChild(usa, chicago);
				territoryHierarchy.addChild(usa, dallas);
				territoryHierarchy.addChild(usa, losAngeles);
				territoryHierarchy.addChild(usa, newYork);
				territoryHierarchy.addChild(usa, newOrleans);
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

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
						TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
						TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
						TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
						TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
						TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
						TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()), Availability.NO);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.HOLDBACK.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.HOLDBACK.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(), 
						TestRightType.HOLDBACK.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
						TestRightType.HOLDBACK.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
						TestRightType.HOLDBACK.getRightTypeId()), Availability.UNSET);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
						TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
						TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
						TestRightType.SALES_PLAN_WINDOW.getRightTypeId()), Availability.NO);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(),
						TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.HOLDBACK.getRightTypeId(),
						TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.UNSET);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
						TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.NO);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
						TestRightType.SALES_PLAN_BLOCK.getRightTypeId()), Availability.UNSET);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);


				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);


				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.HOLDBACK_FATAL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.HOLDBACK_FATAL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.HOLDBACK_FATAL.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.DNL_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.DNL_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.DNL_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.EXCLUDED_RIGHTS.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.EXCLUDED_RIGHTS.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.EXCLUDED_RIGHTS.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);

				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.EXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.NFA_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.NFA_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(TestRightType.NONEXCLUSIVE_CORP_AVAIL.getRightTypeId(),
						TestRightType.NFA_RESTRICTION.getRightTypeId()), Availability.CONDITIONAL_CORPORATE);


				// Made up a right Id for exclusive tethered VODs....should probably
				// use something real later
				rightTypeImpactMatrixMap.put(new Pair<Long, Long>(25L, 25L), Availability.NO);
			}
			rightTypeImpactMatrix = new RightTypeImpactMatrix(rightTypeImpactMatrixMap);

			Map<Long, RightType> rightTypeCorpMap = new HashMap<Long, RightType>();
			{
				rightTypeCorpMap.putIfAbsent(TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
				rightTypeCorpMap.putIfAbsent(TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), TestRightType.NONEXCLUSIVE_CORP_AVAIL);
				rightTypeCorpMap.putIfAbsent(TestRightType.HOLDBACK.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
				rightTypeCorpMap.putIfAbsent(TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), TestRightType.NONEXCLUSIVE_CORP_AVAIL);
				rightTypeCorpMap.putIfAbsent(TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
				rightTypeCorpMap.putIfAbsent(TestRightType.TETHERED_VOD.getRightTypeId(), TestRightType.EXCLUSIVE_CORP_AVAIL);
			}
			rightTypeCorpAvailMap = new RightTypeCorpAvailMap(rightTypeCorpMap);

			// Pair of longs in key are source and conflicting right type ids of the right
			// strands in question.
			Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> conflictMatrixMap = new HashMap<>();
			{
				// Holdbacks: 21
				// Exclusive licenses: 22
				// NonExclusive licenses: 23
				// Sales Plan: Window: 200
				// Sales Plan: Blocking: 201
				// Preliminary Exclusive Distribution Rights: -25
				// Preliminary Non-Exclusive Distribution Rights: -40
				// DNL Restriction: 108L

				// Exclusive licenses: 22
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_DUPLICATE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_LICENSE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_LICENSE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_LICENSE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				// NonExclusive licenses: 23
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_LICENSE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_DUPLICATE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.INFO)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId()),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_LICENSE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				// Holdbacks: 21
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.HOLDBACK.getRightTypeId()),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_HOLDBACK, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.HOLDBACK.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.HOLDBACK.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					new ConflictClass(TestConflictType.SAME_DEAL_DUPLICATE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.INFO)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.HOLDBACK.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.FATAL)
				));

				// SALES PLAN BLOCK: 16L
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.SALES_PLAN_BLOCK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.SALES_PLAN_BLOCK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId()
				),
				new Pair<>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId()
				),
				new Pair<>(
					new ConflictClass(TestConflictType.SAME_DEAL_DUPLICATE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_HOLDBACK, ConflictSeverity.INFO)
				));

				// SALES PLAN WINDOW: 15L
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.SALES_PLAN_WINDOW, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(),
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId()
				),
				new Pair<>(
					new ConflictClass(TestConflictType.SAME_DEAL_DUPLICATE, ConflictSeverity.WARNING),
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.INFO)
				));

				conflictMatrixMap.put(new Pair<>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(),
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId()),
				new Pair<>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DIFF_DEAL_LICENSE, ConflictSeverity.FATAL)
				));

				//Exclusive Preliminary Distribution Rights
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_EXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_EXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_EXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_EXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_EXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));


				//NX Preliminary Distribution Rights
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_NONEXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_NONEXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_NONEXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_NONEXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.PRELIM_NONEXCLUSIVE_RIGHTS, ConflictSeverity.WARNING)
				));

				//Music Use Restriction (Warning)
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_WARNING, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_WARNING, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_WARNING, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_WARNING, ConflictSeverity.WARNING)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.MUSIC_USE_WARN_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_WARNING, ConflictSeverity.WARNING)
				));

				//Music Use Restriction (Fatal)
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_FATAL, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_FATAL, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_FATAL, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_FATAL, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.MUSIC_USE_FATAL_CORP_AVAIL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.MUSIC_USE_RESTRICTION_FATAL, ConflictSeverity.FATAL)
				));


				//Distribution Rights: Restriction = Holdback (fatal)
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.HOLDBACK_FATAL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DISTRIBUTION_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.HOLDBACK_FATAL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DISTRIBUTION_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.HOLDBACK_FATAL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DISTRIBUTION_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.HOLDBACK_FATAL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DISTRIBUTION_HOLDBACK, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.HOLDBACK_FATAL.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DISTRIBUTION_HOLDBACK, ConflictSeverity.FATAL)
				));


				//DNL Restriction (Fatal)
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.DNL_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DNL_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.DNL_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DNL_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.DNL_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DNL_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.DNL_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DNL_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.DNL_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.DNL_RESTRICTION, ConflictSeverity.FATAL)
				));


				//Excluded Rights: Restriction (Fatal
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.EXCLUDED_RIGHTS.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.EXCLUDED_RIGHTS, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.EXCLUDED_RIGHTS.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.EXCLUDED_RIGHTS, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.EXCLUDED_RIGHTS.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.EXCLUDED_RIGHTS, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.EXCLUDED_RIGHTS.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.EXCLUDED_RIGHTS, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.EXCLUDED_RIGHTS.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.EXCLUDED_RIGHTS, ConflictSeverity.FATAL)
				));


				//NFA Restriction (Fatal)
				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.EXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.NFA_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.NFA_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.NONEXCLUSIVE_LICENSE.getRightTypeId(), 
					TestRightType.NFA_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.NFA_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.HOLDBACK.getRightTypeId(), 
					TestRightType.NFA_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.NFA_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_WINDOW.getRightTypeId(), 
					TestRightType.NFA_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.NFA_RESTRICTION, ConflictSeverity.FATAL)
				));

				conflictMatrixMap.put(new Pair<Long, Long>(
					TestRightType.SALES_PLAN_BLOCK.getRightTypeId(), 
					TestRightType.NFA_RESTRICTION.getRightTypeId()
				),
				new Pair<ConflictClass, ConflictClass>(
					ConflictConstants.NO_CONFLICT,
					new ConflictClass(TestConflictType.NFA_RESTRICTION, ConflictSeverity.FATAL)
				));
			}
			conflictMatrix = new TestConflictMatrix(conflictMatrixMap);
			
			rightTypeCarveOutActionMap = new RightTypeCarveOutActionMap();
			conflictBuilder = new TestConflictBuilder();
			corpAvailabilityCalculator = new TestCorporateCalculator(rightTypeImpactMatrix);
		}

		conflictCalculator = new ConflictCalculator<>(
			rightTypeCorpAvailMap,
			conflictMatrix,
			conflictBuilder,
			corpAvailabilityCalculator,
			new CarveOutImpactCalculator(rightTypeCarveOutActionMap)
		);

		conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
	}

	protected static int getNumConflictsIgnorePMTL(
		Collection<? extends IConflictKeyContainer> list
	) {
		EquivalenceSet<IConflictKeyContainer> set = new EquivalenceSet<>(new ConflictKeyContainerEquivalence(new ConflictKeyEquivalencePMTLIgnorant()));
		for (IConflictKeyContainer conflict : list) {
			set.add(conflict);
		}
		
		return set.size();
	}
	
	protected static boolean contains(
		Collection<? extends IConflictKeyContainer> list, 
		ConflictKey value,
		BiFunction<? super ConflictKey, ? super ConflictKey, Boolean> equalityComparator
	) {
		for (IConflictKeyContainer element : list) {
			if (Boolean.TRUE.equals(equalityComparator.apply(element.getConflictKey(), value))) {
				return true;
			}
		}
		return false;
	}
	
	protected static Collection<IConflictKeyContainer> getMatches(
		Collection<? extends IConflictKeyContainer> list,
		ConflictKey value,
		BiFunction<? super ConflictKey, ? super ConflictKey, Boolean> equalityComparator
	) {
		Collection<IConflictKeyContainer> matchingConflicts = new ArrayList<>();
		
		for (IConflictKeyContainer element : list) {
			if (Boolean.TRUE.equals(equalityComparator.apply(element.getConflictKey(), value))) {
				matchingConflicts.add(element);
			}
		}
		return matchingConflicts;
	}
	
	protected static boolean containsConflict(
		Collection<? extends Conflict> list, 
		ConflictKey value,
		ILeafMap<Product> productLeafMap,
		ILeafMap<Media> mediaLeafMap,
		ILeafMap<Territory> territoryLeafMap,
		ILeafMap<Language> languageLeafMap
	) {
		Collection<IConflictKeyContainer> conflicts = getMatches(
			list,
			value,
			pmtlIgnorantConflictKeyEquivalence::equivalent
		);
		
		Set<PMTL> leafValuePMTLs = getLeafPMTLs(
			getPMTLs(value.getPMTL()),
			productLeafMap,
			mediaLeafMap,
			territoryLeafMap,
			languageLeafMap
		);
		
		for (IConflictKeyContainer conflict : conflicts) {
			Set<PMTL> leafConflictPMTLs = getLeafPMTLs(
				getPMTLs(conflict.getPMTL()),
				productLeafMap,
				mediaLeafMap,
				territoryLeafMap,
				languageLeafMap
			);
			
			if (leafConflictPMTLs.containsAll(leafValuePMTLs)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected static Set<PMTL> getPMTLs(PMTL origPMTL) {
		Collection<Product> products = PMTLUtil.extractToNonAggregateProducts(origPMTL.getProduct());
		Collection<Media> medias = PMTLUtil.extractToNonAggregateMedias(origPMTL.getMedia());
		Collection<Territory> territories = PMTLUtil.extractToNonAggregateTerritories(origPMTL.getTerritory());
		Collection<Language> languages = PMTLUtil.extractToNonAggregateLanguages(origPMTL.getLanguage());
	
		Set<PMTL> pmtls = new HashSet<>();
		for (Product product : products) {
			for (Media media : medias) {
				for (Territory territory : territories) {
					for (Language language : languages) {
						pmtls.add(new PMTL(product, media, territory, language));
					}
				}
			}
		}
		
		return pmtls;
	}
	
	protected static Set<PMTL> getLeafPMTLs(
		Collection<PMTL> pmtls,
		ILeafMap<Product> productLeafMap,
		ILeafMap<Media> mediaLeafMap,
		ILeafMap<Territory> territoryLeafMap,
		ILeafMap<Language> languageLeafMap
	) {
		Set<PMTL> leafPMTLs = new HashSet<>();
		for (PMTL pmtl : pmtls) {
			for (Product leafProduct : productLeafMap.getLeaves(pmtl.getProduct())) {
				for (Media leafMedia : mediaLeafMap.getLeaves(pmtl.getMedia())) {
					for (Territory leafTerritory : territoryLeafMap.getLeaves(pmtl.getTerritory())) {
						for (Language leafLanguage : languageLeafMap.getLeaves(pmtl.getLanguage())) {
						    leafPMTLs.add(new PMTL(leafProduct, leafMedia, leafTerritory, leafLanguage));
						}
					}
				}
			}
		}
		
		return leafPMTLs;
	}
	
	protected static <E extends Conflict> void runConflictCheck(
		ConflictCalculator<E> conflictCalculator,
		ConflictCheckRunner<E> conflictCheckRunner,
		Collection<RightStrand> primaryRightStrands,
		Collection<RightStrand> conflictingRightStrands,
		Collection<ConflictOverride> existingOverrides,
		boolean calculateCorpConflicts,
		//These are outputs
		Set<E> primaryLeafConflicts,
		Set<E> siblingLeafConflicts
	) {
		Collection<RightStrand> conflictingNonCorpStrands = new ArrayList<>();
		Collection<CorporateRightStrand> corporateStrands = new ArrayList<>();
		for (RightStrand conflictingStrand : conflictingRightStrands) {
			if (conflictingStrand instanceof CorporateRightStrand) {
				corporateStrands.add((CorporateRightStrand)conflictingStrand);
			}
			else {
				conflictingNonCorpStrands.add(conflictingStrand);
			}
		}
		
		for (RightStrand primaryRightStrand : primaryRightStrands) {
			ConflictCalculationResult<E> result = conflictCheckRunner.runConflictCheck(
				conflictCalculator,
				primaryRightStrand,
				conflictingNonCorpStrands, 
				corporateStrands, 
				existingOverrides, 
				calculateCorpConflicts, 
				null
			);
			
			primaryLeafConflicts.addAll(result.getPrimaryLeafConflicts());
			primaryLeafConflicts.addAll(result.getCorporateLeafConflicts());
			siblingLeafConflicts.addAll(result.getSiblingLeafConflicts());
		}
	}
	
	protected CustomerCountLicense createCustomerCountLicense(TestDealStrand rightStrand) {
		return new CustomerCountLicense(
				rightStrand.getCustomer(),
				rightStrand.getRightSource(),
				rightStrand.getTerm(),
				rightStrand.getTimePeriod(),
				rightStrand.getPMTL()
				);
	}
}
