package com.pdgc.tests.conflictcheck.ConflictIntersectTests;

import java.util.Collections;

import org.junit.Test;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

/**
 * Test in which one Aggregate PMTL is a superset of the other. 
 * 
 * @author CLARA HONG
 *
 */
public class ConflictIntersectTest_subsetOverlap extends ConflictIntersectTest {

	/**
	 * PMTL1 is a complete superset of PMTL2
	 */
	@Test
	public void subsetOverlapTest() {
		PMTL pmtl1 = new PMTL(
				NCIS_SEASON_1, 
				new AggregateMedia(allMedia), 
				new AggregateTerritory(usa), 
				new AggregateLanguage(english));
		PMTL pmtl2 = new PMTL(
				NCIS_SEASON_1_EPISODE_01, 
				new AggregateMedia(basc), 
				new AggregateTerritory(chicago), 
				new AggregateLanguage(english));
		
		LeafPMTLIdSet intersection = getIntersection(pmtl1, pmtl2);
		
		compareLeafIdsToProducts(
            intersection.getProductIds(), 
            Collections.singleton(pmtl2.getProduct())
        );
        compareLeafIdsToMedias(
            intersection.getMediaIds(), 
            Collections.singleton(pmtl2.getMedia())
        );
        compareLeafIdsToTerritories(
            intersection.getTerritoryIds(), 
            Collections.singleton(pmtl2.getTerritory())
        );
        compareLeafIdsToLanguages(
            intersection.getLanguageIds(), 
            Collections.singleton(pmtl2.getLanguage())
        );
	}
	
	/**
	 * Switching the order of PMTLs to confirm the method is not order-specific 
	 */
	@Test
	public void supersetOverlapTest() {
		PMTL pmtl2 = new PMTL(
				NCIS_SEASON_1, 
				new AggregateMedia(allMedia), 
				new AggregateTerritory(usa), 
				new AggregateLanguage(english));
		PMTL pmtl1 = new PMTL(
				NCIS_SEASON_1_EPISODE_01, 
				new AggregateMedia(basc), 
				new AggregateTerritory(chicago), 
				new AggregateLanguage(english));
		
		LeafPMTLIdSet intersection = getIntersection(pmtl1, pmtl2);
		
		compareLeafIdsToProducts(
            intersection.getProductIds(), 
            Collections.singleton(pmtl1.getProduct())
        );
        compareLeafIdsToMedias(
            intersection.getMediaIds(), 
            Collections.singleton(pmtl1.getMedia())
        );
        compareLeafIdsToTerritories(
            intersection.getTerritoryIds(), 
            Collections.singleton(pmtl1.getTerritory())
        );
        compareLeafIdsToLanguages(
            intersection.getLanguageIds(), 
            Collections.singleton(pmtl1.getLanguage())
        );
	}
}
