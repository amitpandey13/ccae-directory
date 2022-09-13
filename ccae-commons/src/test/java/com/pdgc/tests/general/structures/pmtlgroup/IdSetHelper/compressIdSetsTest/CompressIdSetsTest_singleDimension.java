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
import com.pdgc.general.structures.pmtlgroup.idSets.TestIdSet;

public class CompressIdSetsTest_singleDimension {

	/**
	 * Just a sanity check to make sure the method doesn't break given a collection of single-dimension sets
	 * All entries will compress together.
	 */
	@Test
	public void singleDimensionTest() {
		Collection<TestIdSet> idSets = Arrays.asList(
			new TestIdSet(Arrays.asList(Sets.newHashSet(1))),
			new TestIdSet(Arrays.asList(Sets.newHashSet(2))),
			new TestIdSet(Arrays.asList(Sets.newHashSet(3))),
			new TestIdSet(Arrays.asList(Sets.newHashSet(4))),
			new TestIdSet(Arrays.asList(Sets.newHashSet(1)))
		);
		
		Collection<TestIdSet> compressedSets = IdSetHelper.compressIdSets(
			idSets,
			new IdSetFactory<>(IdSet::getIdSetList, TestIdSet::new)
		);
		
		assertEquals(1, compressedSets.size());
		
		TestIdSet idSet = new TestIdSet(Arrays.asList(Sets.newHashSet(
			1, 2, 3, 4
		)));
		
		assertTrue(compressedSets.contains(idSet));
	}
}
