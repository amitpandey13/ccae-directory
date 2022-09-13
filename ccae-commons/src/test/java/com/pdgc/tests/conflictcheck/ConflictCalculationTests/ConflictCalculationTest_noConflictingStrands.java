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

public class ConflictCalculationTest_noConflictingStrands extends ConflictCalculationTest {
	
	@Test
	public void noConflictingStrandsTest() {
		List<RightStrand> primaryRightStrands = Arrays.asList(
			nonExclusiveCanadaEnglishStrand,
			nonExclusiveUSEnglishStrand, 
			nonExclusiveMexicoSpanishStrand, 
			holdbackUSEnglishStrand
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
		
		assertEquals(4, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(nonExclusiveCanadaEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				nonExclusiveCanadaEnglishStrand.getPMTL(),
				nonExclusiveCanadaEnglishStrand.getTerm(),
				nonExclusiveCanadaEnglishStrand.getTimePeriod()
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
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(nonExclusiveUSEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				nonExclusiveUSEnglishStrand.getPMTL(),
				nonExclusiveUSEnglishStrand.getTerm(),
				nonExclusiveUSEnglishStrand.getTimePeriod()
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
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(nonExclusiveMexicoSpanishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				nonExclusiveMexicoSpanishStrand.getPMTL(),
				nonExclusiveMexicoSpanishStrand.getTerm(),
				nonExclusiveMexicoSpanishStrand.getTimePeriod()
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
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(holdbackUSEnglishStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				holdbackUSEnglishStrand.getPMTL(),
				holdbackUSEnglishStrand.getTerm(),
				holdbackUSEnglishStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
