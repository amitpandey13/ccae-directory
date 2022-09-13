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

public class CompressIdSetsTest_pmtlSet_pmtlCompression {

	/**
	 * Should compress to 1 group. 
	 * First grouping should have compressed the products,
	 * then, only after compressing the products, can it be seen that the medias can also be compressed,
	 * then, after the products/medias have been compressed, the territories can be compressed,
	 * then, after the products/medias/territories have been compressed, the languages can be compressed
	 */
	@Test
	public void pmtlSet_pmtCompressionTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(1)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2),
				Sets.newHashSet(2)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(IdSet::getIdSetList, PMTLIdSet::new)
		);
		
		assertEquals(1, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//PMTL = 1,2
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1, 2),
				Sets.newHashSet(1, 2),
				Sets.newHashSet(1, 2),
				Sets.newHashSet(1, 2)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}
}
