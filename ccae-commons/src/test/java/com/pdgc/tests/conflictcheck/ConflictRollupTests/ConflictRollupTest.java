/**
 * 
 */
package com.pdgc.tests.conflictcheck.ConflictRollupTests;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.junit.Before;

import com.google.common.collect.Sets;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictBuilder;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalenceFull;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.TestConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.container.impl.TerrLang;
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
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.PMTLUtil;
import com.pdgc.general.util.TestsHelper;

/**
 * @author gowtham
 *
 */
public class ConflictRollupTest {
	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
		ConflictConstants.instantiateConstants();
		
		conflictBuilder = new TestConflictBuilder();
				
		allMedia = Constants.ALL_MEDIA;
		basc = TestsHelper.createMedia("BASC");
		ppv =  TestsHelper.createMedia("PPV");
		ptv = TestsHelper.createMedia("PTV");
		ptvc = TestsHelper.createMedia("PTV: Cab");
		ptvi = TestsHelper.createMedia("PTV: Int");
		ptvm = TestsHelper.createMedia("PTV: Mob");

		usa = TestsHelper.createCountry("USA");
		chicago = TestsHelper.createMarket("Chicago");
		dallas = TestsHelper.createMarket("Dallas-Fort Worth");
		losAngeles = TestsHelper.createMarket("Los Angeles");
		newYork = TestsHelper.createMarket("New York");
		newOrleans = TestsHelper.createMarket("New Orleans");
		mexico =  TestsHelper.createCountry("Mexico");
		canada = TestsHelper.createCountry("Canada");
		france = TestsHelper.createCountry("France");

		english = TestsHelper.createLanguage("English");
		spanish =  TestsHelper.createLanguage("Spanish");
		french = TestsHelper.createLanguage("French");

		usaenglish = new TerrLang(usa,english);
		worldall = new TerrLang(Constants.WORLD, Constants.ALL_LANGUAGES);
		USEnglishPTV = new MTL(ptv,usaenglish);

		NCIS_SERIES = TestsHelper.createSeries("NCIS");
		NCIS_SEASON_1 = TestsHelper.createSeason("NCIS - SEASON 01(03 / 04)");
		NCIS_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("NCIS 001");
		NCIS_SEASON_1_EPISODE_02 = TestsHelper.createEpisode("NCIS 002");
		FEATURE = TestsHelper.createFeature("ROAD TRIP");
		GOODWIFE_SERIES = TestsHelper.createSeries("GOOD WIFE, THE");
		GOODWIFE_SEASON_1 = TestsHelper.createSeason("GOOD WIFE, THE - SEASON 01 (09/10)");
		GOODWIFE_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Good Wife 001");

		defaultRightsGroupId = 1L;
		crSource = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12345L);
		drSource = new TestDealSource(TestRightSourceType.DEAL, 570796L, "570796L"); //NCISseason1
				
		simpleEpisodeLicense = new TestDealStrand(
			2L, 
			new PMTL(NCIS_SEASON_1_EPISODE_01, ptv, worldall.getTerritory(), worldall.getLanguage()),
			new TermPeriod(
    			new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)),
    			TimePeriod.FULL_WEEK
			),
			drSource,
			TestRightType.NONEXCLUSIVE_LICENSE, 
			new PMTL(NCIS_SEASON_1_EPISODE_01, ptv, worldall.getTerritory(), worldall.getLanguage()),
			new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)),
			true, 
			null,
			null
		);

		productHierarchy = new HierarchyMapEditor<Product>();
		{
			productHierarchy.addElement(NCIS_SERIES);
			productHierarchy.addChild(NCIS_SERIES,   NCIS_SEASON_1);
			productHierarchy.addChild(NCIS_SEASON_1, NCIS_SEASON_1_EPISODE_01);
			productHierarchy.addChild(NCIS_SEASON_1, NCIS_SEASON_1_EPISODE_02);
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 003"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 004"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 005"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 006"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 007"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 008"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 009"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 010"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 011"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 012"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 013"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 014"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 015"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 016"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 017"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 018"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 019"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 020"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 021"));
			productHierarchy.addChild(NCIS_SEASON_1, TestsHelper.createEpisode("NCIS 022"));

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
			mediaHierarchy.addChild(ptv, ptvc);
			mediaHierarchy.addChild(ptv, ptvi);
			mediaHierarchy.addChild(ptv, ptvm);
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
	}
	
	protected static ConflictKeyEquivalenceFull fullConflictKeyEquivalence = new ConflictKeyEquivalenceFull();
	
	protected static HierarchyMapEditor<Product> productHierarchy;
	protected static HierarchyMapEditor<Media> mediaHierarchy;
	protected static HierarchyMapEditor<Territory> territoryHierarchy;
	protected static TwoLevelHierarchy<Language> languageHierarchy;

	protected static Map<Integer, Product> productDictionary;
	protected static Map<Integer, Media> mediaDictionary;
	protected static Map<Integer, Territory> territoryDictionary;
	protected static Map<Integer, Language> languageDictionary;
	
	protected static TestConflictBuilder conflictBuilder;
			
	protected static Media allMedia;
	protected static Media basc;
	protected static Media ppv;
	protected static Media ptv;
	protected static Media ptvc;
	protected static Media ptvi;
	protected static Media ptvm;

	protected static Territory usa;
	protected static Territory chicago;
	protected static Territory dallas;
	protected static Territory losAngeles;
	protected static Territory newYork;
	protected static Territory newOrleans;
	protected static Territory mexico;
	protected static Territory canada;
	protected static Territory france;

	protected static Language english;
	protected static Language spanish;
	protected static Language french;

	protected static TerrLang usaenglish;
	protected static TerrLang worldall;
	protected static MTL USEnglishPTV;

	protected static Product NCIS_SERIES;
	protected static Product NCIS_SEASON_1;
	protected static Product NCIS_SEASON_1_EPISODE_01;
	protected static Product NCIS_SEASON_1_EPISODE_02;
	protected static Product FEATURE;
	protected static Product GOODWIFE_SERIES;
	protected static Product GOODWIFE_SEASON_1;
	protected static Product GOODWIFE_SEASON_1_EPISODE_01;

	// Template structures. To test other PMTLs or whatever, create a new object
	// using the test structures' copy constructure and then set the instance
	// fields to the desired values

	protected static long defaultRightsGroupId;
	protected static TestCorpSource crSource;
	protected static TestDealSource drSource;

	protected static TestDealStrand simpleEpisodeLicense;

	public static boolean containsLeaf(Collection<Conflict> list, Conflict value) {
		if(list.contains(value))
			return true;
		return false;
	}
	
	//Modifies all PMTLs to be leaf-level aggregates 
	public static ConflictKey sanitizePMTLs(
	    ConflictKey conflictKey,
		IReadOnlyHMap<Product> productHierarchy,
		IReadOnlyHMap<Media> mediaHierarchy,
		IReadOnlyHMap<Territory> territoryHierarchy,
		IReadOnlyHMap<Language> languageHierarchy
	) {
		Set<Product> sourceProducts = productHierarchy.convertToLeaves(
		        PMTLUtil.extractToNonAggregateProducts(conflictKey.getPMTL().getProduct()));
		Set<Media> sourceMedias = mediaHierarchy.convertToLeaves(
		        PMTLUtil.extractToNonAggregateMedias(conflictKey.getPMTL().getMedia()));
		Set<Territory> sourceTerritories = territoryHierarchy.convertToLeaves(
		        PMTLUtil.extractToNonAggregateTerritories(conflictKey.getPMTL().getTerritory()));
		Set<Language> sourceLanguages = languageHierarchy.convertToLeaves(
		        PMTLUtil.extractToNonAggregateLanguages(conflictKey.getPMTL().getLanguage()));
		
		return new TestConflictKey(
			conflictKey.getConflictClass(),
			(TestConflictSourceGroupKey)conflictKey.getPrimaryConflictSourceGroupKey(),
			(TestConflictSourceGroupKey)conflictKey.getConflictingConflictSourceGroupKey(),
			new PMTL(
				new AggregateProduct(sourceProducts),
				new AggregateMedia(sourceMedias),
                new AggregateTerritory(sourceTerritories),
                new AggregateLanguage(sourceLanguages)
			),
			conflictKey.getTerm(),
			conflictKey.getTimePeriod()
		);
	}

	public static boolean sanitizedContains(
		Collection<? extends IConflictKeyContainer> list, 
		ConflictKey value,
		BiFunction<? super ConflictKey, ? super ConflictKey, Boolean> equalityComparator, 
		IReadOnlyHMap<Product> productHierarchy,
		IReadOnlyHMap<Media> mediaHierarchy,
		IReadOnlyHMap<Territory> territoryHierarchy,
		IReadOnlyHMap<Language> languageHierarchy
	) {
		for (IConflictKeyContainer element : list) {
			ConflictKey sanitizedElement = sanitizePMTLs(
				element.getConflictKey(),
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			); 
			
			ConflictKey sanitizedValue = sanitizePMTLs(
				value,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			); 
			
			if (Boolean.TRUE.equals(equalityComparator.apply(sanitizedElement, sanitizedValue))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains(
		Collection<? extends IConflictKeyContainer> list, 
		IConflictKeyContainer value,
		BiFunction<? super IConflictKeyContainer, ? super IConflictKeyContainer, Boolean> equalityComparator
	) {
		for (IConflictKeyContainer element : list) {
			if (Boolean.TRUE.equals(equalityComparator.apply(element, value))) {
				return true;
			}
		}
		return false;
	}

	static public void main(String[] args) {
		try {
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
