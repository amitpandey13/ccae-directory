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
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_corporateDateGluing extends ConflictCalculationTest {
	/**
	 * Test that the corporate conflicts have the longest dates possible
	 * even though the corporate strands would've caused date cuts during
	 * availability analysis
	 * @throws IELeftSideAvailabilityIsMissingException 
	 */
	@Test
	public void corporateDateGluingTest() {
		// Primary strands
		TestDealStrand licenseStrand = new TestDealStrand(simpleSeasonLicense);
		licenseStrand.setProduct(Seinfeld_SEASON_1_X);
		licenseStrand.setTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2022, 12, 31)));
		
		//Corporate Rights
		TestDistributionStrand distr1 = new TestDistributionStrand(seriesDistributionRights);
		distr1.setProduct(Seinfeld_SEASON_1_Y);
		distr1.setTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2018, 12, 31)));
		
		TestDistributionStrand distr2 = new TestDistributionStrand(seriesDistributionRights);
		distr2.setProduct(Seinfeld_SEASON_1_Y);
		distr2.setTerm(new Term(DateTimeUtil.createDate(2019, 1, 1), DateTimeUtil.createDate(2019, 12, 31)));
		
		TestDistributionStrand distr3 = new TestDistributionStrand(seriesDistributionRights);
		distr3.setProduct(Seinfeld_SEASON_1_Y);
		distr3.setTerm(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2020, 12, 31)));
		
		TestDistributionStrand distr4 = new TestDistributionStrand(seriesDistributionRights);
		distr4.setProduct(Seinfeld_SEASON_1_Y);
		distr4.setTerm(new Term(DateTimeUtil.createDate(2021, 1, 1), DateTimeUtil.createDate(2021, 12, 31)));
		
		TestDistributionStrand distr5 = new TestDistributionStrand(seriesDistributionRights);
		distr5.setProduct(Seinfeld_SEASON_1_Y);
		distr5.setTerm(new Term(DateTimeUtil.createDate(2022, 1, 1), Constants.PERPETUITY));
		
		TestDistributionStrand restriction = new TestDistributionStrand(seriesDistributionRights);
		restriction.setRightType(TestRightType.MUSIC_USE_FATAL_CORP_AVAIL);
		restriction.setProduct(Seinfeld_SERIES);
		restriction.setTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY));
	
	
		List<RightStrand> primaryRightStrands = Arrays.asList(licenseStrand);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distr1, distr2, distr3, distr4, distr5);
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
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(licenseStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				new PMTL(Seinfeld_SEASON_1_EPISODE_03, licenseStrand.getPMTL().getMTL()),
				licenseStrand.getTerm(),
				licenseStrand.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
