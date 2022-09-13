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

public class CompressIdSetsTest_pmtlSet_mpCompression {

	/**
	 * Should compress to 1 group. 
	 * Initial product grouping would not have made any compression.
	 * Media grouping should have compressed the medias
	 * then, only after compressing the medias, can it be seen that the products can also be compressed
	 */
	@Test
	public void pmtlSet_mpCompressionTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(3, 4),
				Sets.newHashSet(3, 4)
			),
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(2),
				Sets.newHashSet(3, 4),
				Sets.newHashSet(3, 4)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1, 2),
				Sets.newHashSet(3, 4),
				Sets.newHashSet(3, 4)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(IdSet::getIdSetList, PMTLIdSet::new)
		);
		
		assertEquals(1, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//Product, Media = 1,2, TerrLang = 3,4
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1, 2),
				Sets.newHashSet(1, 2),
				Sets.newHashSet(3, 4),
				Sets.newHashSet(3, 4)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}
}
