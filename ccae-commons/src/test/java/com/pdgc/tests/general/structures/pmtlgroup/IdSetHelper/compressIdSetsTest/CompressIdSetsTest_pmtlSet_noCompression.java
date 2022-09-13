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

public class CompressIdSetsTest_pmtlSet_noCompression {

	/**
	 * All pmtlIdSets are disjoint, with absolutely not shared elements
	 */
	@Test
	public void pmtlSet_noCompressionTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(3),
				Sets.newHashSet(3),
				Sets.newHashSet(3),
				Sets.newHashSet(3)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(IdSet::getIdSetList, PMTLIdSet::new)
		);
		
		assertEquals(3, compressedPMTLs.size());
		assertTrue(compressedPMTLs.containsAll(pmtls));
	}
}
