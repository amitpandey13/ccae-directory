package com.pdgc.tests.general.structures.pmtlgroup.PMTLGrouperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.PMTLSetContainer;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;

public class PMTLGrouperTest_noElements extends PMTLGrouperTest {

	/**
	 * Tests what happens if there were was nothing passed in except the relevant PMTLs
	 * Should return a single group equal to the relevant PMTLs with no containers 
	 */
	@Test
	public void noElementsTest() {
		
		Collection<Product> relevantProducts = Arrays.asList(Seinfeld_SEASON_1);
		Collection<Media> relevantMedias = Arrays.asList(ptv, basc);
		Collection<Territory> relevantTerritories = Arrays.asList(usa, mexico);
		Collection<Language> relevantLanguages = Arrays.asList(english, spanish);
		Collection<PMTLSetContainer> pmtlSetContainers = new ArrayList<>();
		
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
	
		Set<PMTL> leafPMTLs;
		Collection<IdSetGroup<LeafPMTLIdSet>> matchingGroups;
		Set<Integer> productIds;
		Set<Integer> mediaIds;
		Set<Integer> territoryIds;
		Set<Integer> languageIds;
		IdSetGroup<LeafPMTLIdSet> pmtlGroup;
		
		assertEquals(1, pmtlGroups.size());
		
		//PMTLs that contain only the relevantPMTLs
		{
			leafPMTLs = new HashSet<>(relevantPMTLLeaves);
			
			matchingGroups = getMatches(
				pmtlGroups,
				leafPMTLs
			);
			
			assertFalse(matchingGroups.isEmpty());
			assertEquals(1, matchingGroups.size());
			
			for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
				assertTrue(group.getSourceObjects().isEmpty());
			}
			
			//Check that the only group is equal to the relevantPMTLs
			{
				productIds = new HashSet<>();
				for (Product leafProduct : productHierarchy.convertToLeaves(relevantProducts)) {
					productIds.add(leafProduct.getProductId().intValue());
				}
				
				mediaIds = new HashSet<>();
				for (Media leafMedia : mediaHierarchy.convertToLeaves(relevantMedias)) {
					mediaIds.add(leafMedia.getMediaId().intValue());
				}
				
				territoryIds = new HashSet<>();
				for (Territory leafTerritory : territoryHierarchy.convertToLeaves(relevantTerritories)) {
					territoryIds.add(leafTerritory.getTerritoryId().intValue());
				}
				
				languageIds = new HashSet<>();
				for (Language language : relevantLanguages) {
					languageIds.add(language.getLanguageId().intValue());
				}
				
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
	}
}
