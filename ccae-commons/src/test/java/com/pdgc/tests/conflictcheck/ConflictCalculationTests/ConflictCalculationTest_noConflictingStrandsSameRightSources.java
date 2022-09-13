package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class ConflictCalculationTest_noConflictingStrandsSameRightSources extends ConflictCalculationTest {
	@Test
	public void noConflictingStrandsSameRightSourcesTest() {
		List<RightStrand> primaryRightStrands = Arrays.asList(
			sharedRightSourceNonExclusiveCanadaEnglishStrand,
			sharedRightSourceNonExclusiveUSEnglishStrand, 
			sharedRightSourceNonExclusiveMexicoSpanishStrand,
			sharedRightSourceHoldbackUSEnglishStrand
		);
		List<RightStrand> conflictingRightStrands = new ArrayList<>();
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflicts = new HashSet<>();
		Set<TestConflict> siblingLeafConflicts = new HashSet<>();
		
		runConflictCheck(
			conflictCalculator,
			conflictCheckRunner,
			primaryRightStrands,
			conflictingRightStrands,
			existingOverrides,
			true,
			primaryLeafConflicts,
			siblingLeafConflicts
		);
		
		assertEquals(2, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(sharedRightSourceNonExclusiveCanadaEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				sharedRightSourceNonExclusiveCanadaEnglishStrand.getPMTL(),
				sharedRightSourceNonExclusiveCanadaEnglishStrand.getTerm(),
				sharedRightSourceNonExclusiveCanadaEnglishStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(sharedRightSourceNonExclusiveUSEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				sharedRightSourceNonExclusiveUSEnglishStrand.getPMTL(),
				sharedRightSourceNonExclusiveUSEnglishStrand.getTerm(),
				sharedRightSourceNonExclusiveUSEnglishStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(sharedRightSourceNonExclusiveMexicoSpanishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				sharedRightSourceNonExclusiveMexicoSpanishStrand.getPMTL(),
				sharedRightSourceNonExclusiveMexicoSpanishStrand.getTerm(),
				sharedRightSourceNonExclusiveMexicoSpanishStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(sharedRightSourceHoldbackUSEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				sharedRightSourceHoldbackUSEnglishStrand.getPMTL(),
				sharedRightSourceHoldbackUSEnglishStrand.getTerm(),
				sharedRightSourceHoldbackUSEnglishStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
