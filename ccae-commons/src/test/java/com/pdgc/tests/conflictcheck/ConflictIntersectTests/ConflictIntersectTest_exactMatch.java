package com.pdgc.tests.conflictcheck.ConflictIntersectTests;

import java.util.Collections;

import org.junit.Test;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

/**
 * Tests for an exact match between 2 Aggregate PMTLs 
 * 
 * @author CLARA HONG
 *
 */
public class ConflictIntersectTest_exactMatch extends ConflictIntersectTest {

	@Test
	public void exactMatchTest() {
		PMTL pmtl1 = new PMTL(
				NCIS_SEASON_1, 
				new AggregateMedia(allMedia), 
				new AggregateTerritory(usa), 
				new AggregateLanguage(english));
		PMTL pmtl2 = new PMTL(
				NCIS_SEASON_1, 
				new AggregateMedia(allMedia), 
				new AggregateTerritory(usa), 
				new AggregateLanguage(english));
		
		LeafPMTLIdSet intersection = getIntersection(pmtl1, pmtl2);
		
		compareLeafIdsToProducts(
	        intersection.getProductIds(), 
	        Collections.singleton(NCIS_SEASON_1)
	    );
		compareLeafIdsToMedias(
            intersection.getMediaIds(), 
            Collections.singleton(allMedia)
        );
		compareLeafIdsToTerritories(
            intersection.getTerritoryIds(), 
            Collections.singleton(usa)
        );
		compareLeafIdsToLanguages(
            intersection.getLanguageIds(), 
            Collections.singleton(english)
        );
	}
	
}
