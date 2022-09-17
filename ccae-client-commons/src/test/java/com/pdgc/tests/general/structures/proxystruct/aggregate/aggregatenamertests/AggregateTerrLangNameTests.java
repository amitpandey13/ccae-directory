package com.pdgc.tests.general.structures.proxystruct.aggregate.aggregatenamertests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.TerrLangMap;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;
import com.pdgc.general.util.AggregateNamer;
import com.pdgc.general.util.AggregateNamer.HierarchicalAggregateNameParams;
import com.pdgc.general.util.AggregateNamer.TwoLevelHierarchyAggregateNameParams;
import com.pdgc.general.util.TestsHelper;

public class AggregateTerrLangNameTests {
	protected static IReadOnlyHMap<Territory> fullTerritoryHierarchy;
	protected static Set<Language> allChildLanguages;
	protected static TerrLangMap terrLangMap;
	
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
	
	static {
		Constants.instantiateConstants();
		
		usa = TestsHelper.createCountry("USA");
		mexico = TestsHelper.createCountry("Mexico");
		canada = TestsHelper.createCountry("Canada");
		chicago = TestsHelper.createMarket("Chicago");
		dallas = TestsHelper.createMarket("Dallas");
		losAngeles = TestsHelper.createMarket("Los Angeles");
		newYork = TestsHelper.createMarket("New York");
		newOrleans = TestsHelper.createMarket("New Orleans");

		english = TestsHelper.createLanguage("English");
		spanish = TestsHelper.createLanguage("Spanish");
		french = TestsHelper.createLanguage("French");
		
		{
		    HierarchyMapEditor<Territory> hierarchy = new HierarchyMapEditor<Territory>();
		    Map<Territory, Set<Language>> terrLangSet = new HashMap<>();
	        
			terrLangSet.put(Constants.WORLD, Sets.newHashSet(english));

			hierarchy.addChild(Constants.WORLD, usa);
			terrLangSet.put(usa, Sets.newHashSet(english, spanish, french));

			hierarchy.addChild(Constants.WORLD, mexico);
			terrLangSet.put(mexico, Sets.newHashSet(english, spanish));

			hierarchy.addChild(Constants.WORLD, canada);
			terrLangSet.put(canada, Sets.newHashSet(english, french));

			hierarchy.addChild(usa, chicago);
			terrLangSet.put(chicago, Sets.newHashSet(english, spanish, french));

			hierarchy.addChild(usa, dallas);
			terrLangSet.put(dallas, Sets.newHashSet(english, spanish, french));

			hierarchy.addChild(usa, losAngeles);
			terrLangSet.put(losAngeles, Sets.newHashSet(english, spanish, french));

			hierarchy.addChild(usa, newYork);
			terrLangSet.put(newYork, Sets.newHashSet(english, spanish, french));

			hierarchy.addChild(usa, newOrleans);
			terrLangSet.put(newOrleans, Sets.newHashSet(english, spanish, french));
			
			fullTerritoryHierarchy = hierarchy;
			terrLangMap = new TerrLangMap(terrLangSet);
		}
		
		allChildLanguages = Sets.newHashSet(english, spanish, french);
	}
	
	protected InactiveTolerantHierarchyMap<Territory> getRevisedTerritoryHierarchy(
	    Set<Territory> inactives
	) {
	    return new InactiveTolerantHierarchyMap<>(fullTerritoryHierarchy, inactives);
	}
	
	protected InactiveTolerantTwoLevelHierarchy<Language> getRevisedLanguageHierarchy(
	    Set<Language> inactives
	) {
	    return new InactiveTolerantTwoLevelHierarchy<>(
            Constants.ALL_LANGUAGES, 
            allChildLanguages, 
            inactives
        );
	}

	@Test
	public void usaAllLeavesTest() {
		Collection<Territory> sourceTerritories = Arrays.asList(chicago, dallas, losAngeles, newYork, newOrleans);
		Collection<Language> sourceLanguages = Arrays.asList(english, spanish, french);
		Set<Territory> inactiveTerritories = new HashSet<>();
		Set<Language> inactiveLanguages = new HashSet<>();
		
		String aggName = AggregateNamer.getAggregateTerrLangName(
			sourceTerritories, 
			sourceLanguages, 
			getRevisedTerritoryHierarchy(inactiveTerritories), 
			HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
			getRevisedLanguageHierarchy(inactiveLanguages),
			TwoLevelHierarchyAggregateNameParams.<Language>builder()
				.nameMapper(Language::getLanguageName)
				.allName("All")
				.delineator(",")
				.build(), 
			terrLangMap, 
            "; "
		);
		
		assertEquals(
			"USA/All",
			aggName
		);
	}

	@Test
	public void usaAllLanguageAllTest() {
		Collection<Territory> sourceTerritories = Arrays.asList(chicago, dallas, losAngeles, newYork, newOrleans);
		Collection<Language> sourceLanguages = Arrays.asList(Constants.ALL_LANGUAGES);
		Set<Territory> inactiveTerritories = new HashSet<>();
        Set<Language> inactiveLanguages = new HashSet<>();
		
		String aggName = AggregateNamer.getAggregateTerrLangName(
			sourceTerritories, 
			sourceLanguages, 
			getRevisedTerritoryHierarchy(inactiveTerritories), 
			HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
            getRevisedLanguageHierarchy(inactiveLanguages),
            TwoLevelHierarchyAggregateNameParams.<Language>builder()
                .nameMapper(Language::getLanguageName)
                .allName("All")
                .delineator(",")
                .build(), 
			terrLangMap,
			"; "
		);
		
		assertEquals(
			"USA/All",
			aggName
		);
	}

	@Test
	public void usaEnglishTest() {
		Collection<Territory> sourceTerritories = Arrays.asList(chicago, dallas, losAngeles, newYork, newOrleans);
		Collection<Language> sourceLanguages = Arrays.asList(english);
		Set<Territory> inactiveTerritories = new HashSet<>();
        Set<Language> inactiveLanguages = new HashSet<>();
		
		String aggName = AggregateNamer.getAggregateTerrLangName(
			sourceTerritories, 
			sourceLanguages, 
			getRevisedTerritoryHierarchy(inactiveTerritories), 
			HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
            getRevisedLanguageHierarchy(inactiveLanguages),
            TwoLevelHierarchyAggregateNameParams.<Language>builder()
                .nameMapper(Language::getLanguageName)
                .allName("All")
                .delineator(",")
                .build(), 
			terrLangMap,
			"; "
		);
		
		assertEquals(
			"USA/English",
			aggName
		);
	}

	@Test
	public void usaAllMinusChicagoAllTest() {
		Collection<Territory> sourceTerritories = Arrays.asList(dallas, losAngeles, newYork, newOrleans);
		Collection<Language> sourceLanguages = Arrays.asList(english, spanish, french);
		Set<Territory> inactiveTerritories = new HashSet<>();
        Set<Language> inactiveLanguages = new HashSet<>();
		
		String aggName = AggregateNamer.getAggregateTerrLangName(
			sourceTerritories, 
			sourceLanguages, 
			getRevisedTerritoryHierarchy(inactiveTerritories), 
			HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
            getRevisedLanguageHierarchy(inactiveLanguages),
            TwoLevelHierarchyAggregateNameParams.<Language>builder()
                .nameMapper(Language::getLanguageName)
                .allName("All")
                .delineator(",")
                .build(), 
			terrLangMap,
			"; "
		);
		
		assertEquals(
			"USA excl Chicago/All",
			aggName
		);
	}

/*
 * Undefined rules about what to do when there are more (active) languages than are defined in a territory
 * b/c of the non-cartesianed nature of pmtl sets. At Fox, all territories have the same languages, 
 * so this is a non-issue.
	@Test
	public void dallasMexicoAllTest() {
		Collection<Territory> sourceTerritories = Arrays.asList(dallas, mexico);
		Collection<Language> sourceLanguages = Arrays.asList(english, spanish, french);
		Set<Territory> inactiveTerritories = new HashSet<>();
        Set<Language> inactiveLanguages = new HashSet<>();
		
		String aggName = AggregateNamer.getAggregateTerrLangName(
			sourceTerritories, 
			sourceLanguages, 
			getRevisedTerritoryHierarchy(inactiveTerritories), 
			new HierarchicalAggregateNameParams<Territory>(
				1, 
				1, 
				Territory::getTerritoryName,
				null, 
				true,
				","
			), 
			getRevisedLanguageHierarchy(inactiveLanguages),
			new NonHierarchyAggregateNameParams<Language>(
				Language::getLanguageName,
				"All", 
				","
			), 
			terrLangMap,
			"; "
		);
		
		assertEquals(
			"Dallas,Mexico/All",
			aggName
		);
	}
*/
	
	@Test
    public void usaAllMinusInvalidChicagoTest() {
        Collection<Territory> sourceTerritories = Arrays.asList(dallas, losAngeles, newYork, newOrleans);
        Collection<Language> sourceLanguages = Arrays.asList(english, spanish, french);
        Set<Territory> inactiveTerritories = Sets.newHashSet(chicago);
        Set<Language> inactiveLanguages = new HashSet<>();
        
        String aggName = AggregateNamer.getAggregateTerrLangName(
            sourceTerritories, 
            sourceLanguages, 
            getRevisedTerritoryHierarchy(inactiveTerritories), 
            HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
            getRevisedLanguageHierarchy(inactiveLanguages),
            TwoLevelHierarchyAggregateNameParams.<Language>builder()
                .nameMapper(Language::getLanguageName)
                .allName("All")
                .delineator(",")
                .build(), 
            terrLangMap,
            "; "
        );
        
        assertEquals(
            "USA/All",
            aggName
        );
    }
	
	@Test
    public void usaAllInvalidChicagoTest() {
        Collection<Territory> sourceTerritories = Arrays.asList(usa, chicago);
        Collection<Language> sourceLanguages = Arrays.asList(english, spanish, french);
        Set<Territory> inactiveTerritories = Sets.newHashSet(chicago);
        Set<Language> inactiveLanguages = new HashSet<>();
        
        String aggName = AggregateNamer.getAggregateTerrLangName(
            sourceTerritories, 
            sourceLanguages, 
            getRevisedTerritoryHierarchy(inactiveTerritories), 
            HierarchicalAggregateNameParams.<Territory>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Territory::getTerritoryName)
                .delineator(",")
                .includeUnknowns(true)
                .build(),
            getRevisedLanguageHierarchy(inactiveLanguages),
            TwoLevelHierarchyAggregateNameParams.<Language>builder()
                .nameMapper(Language::getLanguageName)
                .allName("All")
                .delineator(",")
                .build(), 
            terrLangMap,
            "; "
        );
        
        assertEquals(
            "USA + Chicago/All",
            aggName
        );
    }
}
