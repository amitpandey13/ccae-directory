package com.pdgc.tests.general.structures.pmtlgroup.IdSetHelper.compressIdSetsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetHelper;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;

public class CompressIdSetsTest_pmtlSet_subsetExistence {

	/**
	 * Given 2 pmtls, where one is a subset of another, the compressed result should just contain the large pmtl
	 */
	@Test
	public void pmtlSet_subsetExistenceTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3)
			),
			new PMTLIdSet(
				Sets.newHashSet(1, 2),
				Sets.newHashSet(2, 3),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(IdSet::getIdSetList, PMTLIdSet::new)
		);
		
		assertEquals(1, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//PMTL = 1, 2, 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1, 2, 3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}
}
