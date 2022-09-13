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
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_ptvDistributionRightsVsDealptvcEndLongMedia extends ConflictCalculationTest {
	@Test
	public void ptvDistributionRightsVsDealptvcEndLongMediaTest() {
		TestDealStrand tempRightStrand = new TestDealStrand(simpleSeasonLicense);
		tempRightStrand.setRightStrandId(1386450l);
		tempRightStrand.setMTL(new MTL(ptvc, usa, Constants.ALL_LANGUAGES));
		tempRightStrand.setActualMTL(new MTL(ptvc, usa, Constants.ALL_LANGUAGES));
		tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
		tempRightStrand.setTerm(new Term(DateTimeUtil.createDate(2016, 1, 22), DateTimeUtil.createDate(2019, 9, 22)));
	
		//create Distribution Rights Strand
		TestDistributionStrand distrRights = new TestDistributionStrand(seriesDistributionRights);
		distrRights.setTerm(new Term(Constants.EPOCH, DateTimeUtil.createDate(2017, 9, 22)));
		
		List<RightStrand> primaryRightStrands = Arrays.asList(tempRightStrand);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distrRights);
		
		List<ConflictOverride> existingOverrides = new ArrayList<>();
		
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
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tempRightStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				tempRightStrand.getPMTL(),
				new Term(DateTimeUtil.createDate(2017, 9, 23), DateTimeUtil.createDate(2019, 9, 22)),
				tempRightStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
