package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_ExclExhibitionAndHBSameDeal extends ConflictCalculationTest {
	@Test
	public void exhibitionExclusivityLicenseTest() {
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.EXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);

		TestDealStrand holdback1 = new TestDealStrand(simpleEpisodeLicense);
		holdback1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		holdback1.setRightType(TestRightType.HOLDBACK);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 1); //same sourceId
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2); //different sourcedetail
		holdback1.setRightSource(tdrs2);

		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(holdback1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1);
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

		//Warning on primary side.
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
			primaryLeafConflicts,
			new TestConflictKey(
				conflictMatrix.getConflictType(holdback1, exhibitionLicense1),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(holdback1),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
				holdback1.getPMTL(),
				new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)),
				TimePeriod.FULL_WEEK
			),
			pmtlIgnorantConflictKeyEquivalence::equivalent
		));
		
		//Warning on conflicting side
		assertEquals(1, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
			siblingLeafConflicts,
			new TestConflictKey(
				conflictMatrix.getConflictType(exhibitionLicense1, holdback1),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(holdback1),
				exhibitionLicense1.getPMTL(),
				new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)),
				TimePeriod.FULL_WEEK
			),
			pmtlIgnorantConflictKeyEquivalence::equivalent
		));
	}
}
