package com.pdgc.tests.conflictcheck.ConflictIntersectTests;

import java.util.Collections;

import org.junit.Test;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

/**
 * Test in which two PMTLs both have overlapping elements and unique elements.
 *  
 * @author CLARA HONG
 *
 */
public class ConflictIntersectTest_partialOverlap extends ConflictIntersectTest {

	/**
	 * PMTL1 and PMTL2 overlap 
	 */
	@Test
	public void partialOverlapTest() {
		PMTL pmtl1 = new PMTL(
				NCIS_SEASON_1, 
				new AggregateMedia(ptv), 
				new AggregateTerritory(chicago, losAngeles), 
				new AggregateLanguage(english, french));
		PMTL pmtl2 = new PMTL(
				NCIS_SEASON_1_EPISODE_01, 
				new AggregateMedia(ptv, basc), 
				new AggregateTerritory(chicago, newYork), 
				new AggregateLanguage(english, spanish));
		
		LeafPMTLIdSet intersection = getIntersection(pmtl1, pmtl2);
		
		compareLeafIdsToProducts(
            intersection.getProductIds(), 
            Collections.singleton(NCIS_SEASON_1_EPISODE_01)
        );
        compareLeafIdsToMedias(
            intersection.getMediaIds(), 
            Collections.singleton(ptv)
        );
        compareLeafIdsToTerritories(
            intersection.getTerritoryIds(), 
            Collections.singleton(chicago)
        );
        compareLeafIdsToLanguages(
            intersection.getLanguageIds(), 
            Collections.singleton(english)
        );
	}
	
}
