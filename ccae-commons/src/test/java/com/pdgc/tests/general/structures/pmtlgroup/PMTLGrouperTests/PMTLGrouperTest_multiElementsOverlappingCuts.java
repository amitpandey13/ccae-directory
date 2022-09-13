package com.pdgc.tests.general.structures.pmtlgroup.PMTLGrouperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.PMTLSetContainer;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;

public class PMTLGrouperTest_multiElementsOverlappingCuts extends PMTLGrouperTest {

	/**
	 * Tests what happens if there are multiple elements passed in that force cuts on each other
	 */
	@Test
	public void multiElementsOverlappingCutsTest() {
		
		Collection<Product> relevantProducts = Arrays.asList(Seinfeld_SEASON_1);
		Collection<Media> relevantMedias = Arrays.asList(ptv, basc);
		Collection<Territory> relevantTerritories = Arrays.asList(usa, mexico);
		Collection<Language> relevantLanguages = Arrays.asList(english, spanish);
		PMTLSetContainer obj1PMTLSetContainer = new PMTLSetContainer(
			Arrays.asList(Seinfeld_SEASON_1_XY),
			Arrays.asList(ptv, svod),
			Arrays.asList(mexico, canada),
			Arrays.asList(Constants.ALL_LANGUAGES),
			"Object 1"
		);
		PMTLSetContainer obj2PMTLSetContainer = new PMTLSetContainer(
			Arrays.asList(Seinfeld_SEASON_1_X),
			Arrays.asList(ptvi, basc),
			Arrays.asList(Constants.WORLD),
			Arrays.asList(english),
			"Object 2"
		);
		PMTLSetContainer obj3PMTLSetContainer = new PMTLSetContainer(
			Arrays.asList(Seinfeld_SEASON_1_Y),
			Arrays.asList(basc),
			Arrays.asList(usa),
			Arrays.asList(Constants.ALL_LANGUAGES),
			"Object 3"
		);
		Collection<PMTLSetContainer> pmtlSetContainers = Arrays.asList(
			obj1PMTLSetContainer,
			obj2PMTLSetContainer,
			obj3PMTLSetContainer
		);
		

		Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = getPMTLGroups(
			relevantProducts,
			relevantMedias,
			relevantTerritories,
			relevantLanguages,
			pmtlSetContainers
		);
		
		Set<PMTL> relevantPMTLLeaves = new HashSet<>();
		for (Product product : relevantProducts) {
			for (Media media : relevantMedias) {
				for (Territory territory : relevantTerritories) {
					for (Language language : relevantLanguages) {
						relevantPMTLLeaves.addAll(getLeafPMTLs(
							new PMTL(product, media, territory, language),
							productHierarchy,
							mediaHierarchy,
							territoryHierarchy,
							languageHierarchy
						));
					}
				}
			}
		}
	
		Set<PMTL> obj1PMTLLeaves = new HashSet<>();
		for (Product product : obj1PMTLSetContainer.getProducts()) {
			for (Media media : obj1PMTLSetContainer.getMedias()) {
				for (Territory territory : obj1PMTLSetContainer.getTerritories()) {
					for (Language language : obj1PMTLSetContainer.getLanguages()) {
						obj1PMTLLeaves.addAll(getLeafPMTLs(
							new PMTL(product, media, territory, language),
							productHierarchy,
							mediaHierarchy,
							territoryHierarchy,
							languageHierarchy
						));
					}
				}
			}
		}
		
		Set<PMTL> obj2PMTLLeaves = new HashSet<>();
		for (Product product : obj2PMTLSetContainer.getProducts()) {
			for (Media media : obj2PMTLSetContainer.getMedias()) {
				for (Territory territory : obj2PMTLSetContainer.getTerritories()) {
					for (Language language : obj2PMTLSetContainer.getLanguages()) {
						obj2PMTLLeaves.addAll(getLeafPMTLs(
							new PMTL(product, media, territory, language),
							productHierarchy,
							mediaHierarchy,
							territoryHierarchy,
							languageHierarchy
						));
					}
				}
			}
		}
		
		Set<PMTL> obj3PMTLLeaves = new HashSet<>();
		for (Product product : obj3PMTLSetContainer.getProducts()) {
			for (Media media : obj3PMTLSetContainer.getMedias()) {
				for (Territory territory : obj3PMTLSetContainer.getTerritories()) {
					for (Language language : obj3PMTLSetContainer.getLanguages()) {
						obj3PMTLLeaves.addAll(getLeafPMTLs(
							new PMTL(product, media, territory, language),
							productHierarchy,
							mediaHierarchy,
							territoryHierarchy,
							languageHierarchy
						));
					}
				}
			}
		}
		
		
		Set<PMTL> leafPMTLs;
		Collection<IdSetGroup<LeafPMTLIdSet>> matchingGroups;
		
		//Intersection of Object 1, 2, and 3
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.retainAll(obj1PMTLLeaves);
			leafPMTLs.retainAll(obj2PMTLLeaves);
			leafPMTLs.retainAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(3, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 1"));
				assertTrue(group.getSourceObjects().contains("Object 2"));
				assertTrue(group.getSourceObjects().contains("Object 3"));
			}
		}
		
		//Intersection of Object 1 and 2
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.retainAll(obj1PMTLLeaves);
			leafPMTLs.retainAll(obj2PMTLLeaves);
			leafPMTLs.removeAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(2, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 1"));
				assertTrue(group.getSourceObjects().contains("Object 2"));
			}
		}

		//Intersection of Object 1 and 3
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.retainAll(obj1PMTLLeaves);
			leafPMTLs.removeAll(obj2PMTLLeaves);
			leafPMTLs.retainAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(2, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 1"));
				assertTrue(group.getSourceObjects().contains("Object 3"));
			}
		}
		
		//Intersection of Object 2 and 3
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.removeAll(obj1PMTLLeaves);
			leafPMTLs.retainAll(obj2PMTLLeaves);
			leafPMTLs.retainAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(2, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 2"));
				assertTrue(group.getSourceObjects().contains("Object 3"));
			}
		}
		
		//Object 1 only
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.retainAll(obj1PMTLLeaves);
			leafPMTLs.removeAll(obj2PMTLLeaves);
			leafPMTLs.removeAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(1, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 1"));
			}
		}
	
		//Object 2 only
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.removeAll(obj1PMTLLeaves);
			leafPMTLs.retainAll(obj2PMTLLeaves);
			leafPMTLs.removeAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(1, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 2"));
			}
		}
	
		//Object 3 only
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.removeAll(obj1PMTLLeaves);
			leafPMTLs.removeAll(obj2PMTLLeaves);
			leafPMTLs.retainAll(obj3PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertTrue(leafPMTLs.isEmpty() == matchingGroups.isEmpty());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(1, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 3"));
			}
		}
	}
}
