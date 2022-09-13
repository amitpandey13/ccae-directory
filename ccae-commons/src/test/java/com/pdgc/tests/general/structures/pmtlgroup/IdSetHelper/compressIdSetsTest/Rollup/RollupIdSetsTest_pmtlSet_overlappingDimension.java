package com.pdgc.tests.general.structures.pmtlgroup.IdSetHelper.compressIdSetsTest.Rollup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.PMTLSetHelper;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;

public class RollupIdSetsTest_pmtlSet_overlappingDimension {
	
	/**
	 * Should compress to 3 groups. 
	 * MTLs of one group are a subset of the other, while products are merely siblings.
	 * In this scenario, the product is the first dimension to compress, 
	 * which should produce a different answer than if rollup order was different 
	 */
	@Test
	public void pmtlSet_pmtlRollupTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(3)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(IdSet::getIdSetList, PMTLIdSet::new),
			true
		);
		
		assertEquals(3, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//Product = 1,2, Media, Territory = 1, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1, 2),
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
		
		//Product = 1, Media = 1, Territory = 2,3, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(2,3),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
		
		//Product = 2, Media = 2,3, Territory = 1, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(2, 3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}

	@Test
	public void pmtlSet_mtlpRollupTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(3)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(
				PMTLSetHelper.getReorderedIdSetListBuilder(
					3,
					0,
					1,
					2
				),
				new Function<List<Set<Integer>>, PMTLIdSet>() {
					@Override
					public PMTLIdSet apply(List<Set<Integer>> idSets) {
						return new PMTLIdSet(
							idSets.get(3),
							idSets.get(0),
							idSets.get(1),
							idSets.get(2)
						);
					}
				}
			),
			true
		);
		
		assertEquals(2, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//Product, Media, Territory = 1,2,3, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
		
		//Product = 2, Media = 1,2,3, Territory = 1, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}
	
	@Test
	public void pmtlSet_tlpmRollupTest() {
		Collection<PMTLIdSet> pmtls = Arrays.asList(
			new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(3)
			),
			new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			)
		);
		
		Collection<PMTLIdSet> compressedPMTLs = IdSetHelper.compressIdSets(
			pmtls,
			new IdSetFactory<>(
				PMTLSetHelper.getReorderedIdSetListBuilder(
					3,
					0,
					1,
					2
				),
				new Function<List<Set<Integer>>, PMTLIdSet>() {
					@Override
					public PMTLIdSet apply(List<Set<Integer>> idSets) {
						return new PMTLIdSet(
							idSets.get(3),
							idSets.get(0),
							idSets.get(1),
							idSets.get(2)
						);
					}
				}
			),
			true
		);
		
		assertEquals(2, compressedPMTLs.size());
		
		PMTLIdSet pmtl;
		//Product, Media, Territory = 1,2,3, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(1),
				Sets.newHashSet(1),
				Sets.newHashSet(1,2,3),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
		
		//Product = 2, Media = 1,2,3, Territory = 1, Language = 3
		{
			pmtl = new PMTLIdSet(
				Sets.newHashSet(2),
				Sets.newHashSet(1, 2, 3),
				Sets.newHashSet(1),
				Sets.newHashSet(3)
			);
			
			assertTrue(compressedPMTLs.contains(pmtl));
		}
	}
}
