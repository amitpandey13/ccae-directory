package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;

public class ConflictCalculationTest_episodicDistributionRights extends ConflictCalculationTest {
	
	/**
	 * Single season with different distribution rights for each episode
	 * All distribution rights are from 1/1/1900 to 12/31/9999 and all media world wide but have different ids
	 * No licensing
	 * Criteria at season level for single territory single media single language
	 * Confirm that episode tab shows All in episode column as there is no variance in availability of episodes.
	 */
	@Test
	public void episodicDistributionRightsTest() {
		// Primary strands
		TestDealStrand tempRightStrand = new TestDealStrand(simpleSeasonLicense);
		tempRightStrand.setProduct(Seinfeld_SEASON_1_X);

		//Episode 1
		TestDistributionStrand distrRights01 = new TestDistributionStrand(seriesDistributionRights);
		distrRights01.setProduct(Seinfeld_SEASON_1_EPISODE_01);
		distrRights01.setActualProduct(Seinfeld_SEASON_1_EPISODE_01);
		TestCorpSource crSource01 = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12345L);
		distrRights01.setRightSource(crSource01);
		
		//Episode 2
		TestDistributionStrand distrRights02 = new TestDistributionStrand(seriesDistributionRights);
		distrRights02.setRightStrandId(10L);
		distrRights02.setProduct(Seinfeld_SEASON_1_EPISODE_02);
		distrRights02.setActualProduct(Seinfeld_SEASON_1_EPISODE_02);
		TestCorpSource crSource02 = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12346L);
		distrRights02.setRightSource(crSource02);
		
		//Episode 3
		TestDistributionStrand distrRights03 = new TestDistributionStrand(seriesDistributionRights);
		distrRights03.setRightStrandId(11L);
		distrRights03.setProduct(Seinfeld_SEASON_1_EPISODE_03);
		distrRights03.setActualProduct(Seinfeld_SEASON_1_EPISODE_03);
		TestCorpSource crSource03 = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12347L);
		distrRights03.setRightSource(crSource03);
		
		//Episode 4
		TestDistributionStrand distrRights04 = new TestDistributionStrand(seriesDistributionRights);
		distrRights04.setRightStrandId(12L);
		distrRights04.setProduct(Seinfeld_SEASON_1_EPISODE_04);
		distrRights04.setActualProduct(Seinfeld_SEASON_1_EPISODE_04);
		TestCorpSource crSource04 = new TestCorpSource(TestRightSourceType.RIGHTSIN, 12348L);
		distrRights04.setRightSource(crSource04);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(tempRightStrand);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distrRights01, distrRights02, distrRights03, distrRights04);
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
