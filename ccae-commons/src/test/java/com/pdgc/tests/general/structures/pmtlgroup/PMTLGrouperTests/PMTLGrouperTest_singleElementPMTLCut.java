package com.pdgc.tests.general.structures.pmtlgroup.PMTLGrouperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.PMTLSetContainer;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;

public class PMTLGrouperTest_singleElementPMTLCut extends PMTLGrouperTest {

	/**
	 * Tests what happens if there were was a single element passed in
	 * that forced cuts to product, media, territory, and language
	 */
	@Test
	public void singleElementPMTLCut() {
		
		Collection<Product> relevantProducts = Arrays.asList(Seinfeld_SEASON_1);
		Collection<Media> relevantMedias = Arrays.asList(ptv, basc);
		Collection<Territory> relevantTerritories = Arrays.asList(usa, mexico);
		Collection<Language> relevantLanguages = Arrays.asList(english, spanish);
		PMTLSetContainer obj1PMTLSetContainer = new PMTLSetContainer(
			Arrays.asList(Seinfeld_SEASON_1_EPISODE_01),
			Arrays.asList(ptvi),
			Arrays.asList(chicago),
			Arrays.asList(english),
			"Object 1"
		);
		Collection<PMTLSetContainer> pmtlSetContainers = Arrays.asList(obj1PMTLSetContainer);
		
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
		
		Set<PMTL> leafPMTLs;
		Collection<IdSetGroup<LeafPMTLIdSet>> matchingGroups;
		Set<Integer> productIds;
		Set<Integer> mediaIds;
		Set<Integer> territoryIds;
		Set<Integer> languageIds;
		IdSetGroup<LeafPMTLIdSet> pmtlGroup;
		
		assertEquals(2, pmtlGroups.size());
		Collection<LeafPMTLIdSet> leafPMTLIdSets = new HashSet<LeafPMTLIdSet>(); 
		for (IdSetGroup<LeafPMTLIdSet> idSetGroup : pmtlGroups) {
			leafPMTLIdSets.addAll(idSetGroup.getIdSets()); 
		}
		assertEquals(5, leafPMTLIdSets.size());

		
		//PMTL with the object - intersected with the relevant PMTL filters
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.retainAll(obj1PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertFalse(matchingGroups.isEmpty());
			assertEquals(1, matchingGroups.size());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertEquals(1, group.getSourceObjects().size());
				assertTrue(group.getSourceObjects().contains("Object 1"));
			}
			
			//Intersection
			{
				productIds = Sets.newHashSet(Seinfeld_SEASON_1_EPISODE_01.getProductId().intValue());
				mediaIds = Sets.newHashSet(ptvi.getMediaId().intValue());
				territoryIds = Sets.newHashSet(chicago.getTerritoryId().intValue());
				languageIds = Sets.newHashSet(english.getLanguageId().intValue());
				
				pmtlGroup = first(
					matchingGroups,
					productIds,
					mediaIds,
					territoryIds,
					languageIds
				);
				assertNotNull(pmtlGroup);
			}
		}
		
		//PMTLs that contain only the relevantPMTLs
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			leafPMTLs.removeAll(obj1PMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertFalse(matchingGroups.isEmpty());
			assertEquals(4, matchingGroups.size());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertTrue(group.getSourceObjects().isEmpty());
			}
		}
	}
}
