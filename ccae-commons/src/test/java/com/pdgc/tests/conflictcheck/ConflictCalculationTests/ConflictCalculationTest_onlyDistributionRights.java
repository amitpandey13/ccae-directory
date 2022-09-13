package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class ConflictCalculationTest_onlyDistributionRights extends ConflictCalculationTest {
	
	@Test
	public void noConflictingRightStrandsTest() {
		List<RightStrand> primaryRightStrands = Arrays.asList(
			nonExclusiveCanadaEnglishStrand,
			nonExclusiveUSEnglishStrand, 
			nonExclusiveMexicoSpanishStrand, 
			holdbackUSEnglishStrand
		);

		List<RightStrand> conflictingRightStrands = Arrays.asList(seriesDistributionRights);
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
		
		assertEquals(0, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
