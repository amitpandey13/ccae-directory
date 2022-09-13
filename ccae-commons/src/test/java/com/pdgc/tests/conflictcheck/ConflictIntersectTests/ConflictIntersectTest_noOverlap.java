package com.pdgc.tests.conflictcheck.ConflictIntersectTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

/**
 * Two PMTLs have at least one element in which they are mutually exclusive. 
 * 
 * @author CLARA HONG
 *
 */
public class ConflictIntersectTest_noOverlap extends ConflictIntersectTest {
	
	/**
	 * PMTL1's product is episode 1, PMTL2 product is episode 2 
	 * Products are mutually exclusive so there is no overlap 
	 */
	@Test
	public void noOverlapTest() {
		PMTL pmtl1 = new PMTL(
				NCIS_SEASON_1_EPISODE_01, 
				new AggregateMedia(ptv), 
				new AggregateTerritory(chicago, losAngeles), 
				new AggregateLanguage(english, french));
		PMTL pmtl2 = new PMTL(
				NCIS_SEASON_1_EPISODE_02, 
				new AggregateMedia(ptv, basc), 
				new AggregateTerritory(chicago, newYork), 
				new AggregateLanguage(english, spanish));
		
		LeafPMTLIdSet intersection = getIntersection(pmtl1, pmtl2);
		
		assertTrue(intersection == null);
		
	}
}
