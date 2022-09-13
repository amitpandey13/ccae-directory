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
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;

public class ConflictCalculationTest_allLangPrimaryVsSingleTerrSingleLangConflictingLeafbreakOut extends ConflictCalculationTest {
	
	@Test
	public void allLangPrimaryVsSingleTerrSingleLangConflictingLeafbreakOut() {
		// Get primary Deal
		long dealId = simpleEpisodeLicense.getDealId();

		// Set up primary licenses for Mexico Spanish Excl
		TestDealStrand tdrs = new TestDealStrand(simpleEpisodeLicense);
		tdrs.setRightStrandId(tdrs.getRightStrandId() + 1);
		tdrs.setActualTerrLang(new TerrLang(Constants.WORLD, Constants.ALL_LANGUAGES));
		tdrs.setTerrLang(new TerrLang(Constants.WORLD, Constants.ALL_LANGUAGES));
		tdrs.setRightSource(simpleEpisodeLicense.getRightSource());
		tdrs.setProduct(Seinfeld_SEASON_1_X);
		tdrs.setActualProduct(Seinfeld_SEASON_1_X);

		// Create conflicting deal strands and source.
		TestDealStrand diffDealStrand = new TestDealStrand(tdrs);
		diffDealStrand.setRightStrandId(-diffDealStrand.getRightStrandId());
		diffDealStrand.setActualTerrLang(new TerrLang(usa, english));
		diffDealStrand.setTerrLang(new TerrLang(usa, english));
		diffDealStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, dealId + 1, tdrs.getRightSource().getSourceDetailId() + 1));
		diffDealStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(tdrs);
		List<RightStrand> conflictingRightStrands = Arrays.asList(diffDealStrand);
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflicts = new HashSet<>();
		Set<TestConflict> siblingLeafConflicts = new HashSet<>();
		
		runConflictCheck(
			conflictCalculator,
			conflictCheckRunner,
			primaryRightStrands,
			conflictingRightStrands,
			existingOverrides,
			false,
			primaryLeafConflicts,
			siblingLeafConflicts
		);
		
		PMTL conflictPMTL = new PMTL (
			new AggregateProduct(Seinfeld_SEASON_1_EPISODE_01, Seinfeld_SEASON_1_EPISODE_02, Seinfeld_SEASON_1_EPISODE_03), 
			new AggregateMedia(ptvc, ptvi, ptvm), 
			new AggregateTerritory(dallas, chicago, newYork, losAngeles, newOrleans), 
			new AggregateLanguage(english)
		);
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
			primaryLeafConflicts,
			new TestConflictKey(
				conflictMatrix.getConflictType(tdrs, diffDealStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tdrs),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(diffDealStrand),
				conflictPMTL,
				tdrs.getTerm(),
				tdrs.getTimePeriod()
			),
			pmtlFullConflictKeyEquivalence::equivalent
		));
		
		assertEquals(1, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
			siblingLeafConflicts,
			new TestConflictKey(
				conflictMatrix.getConflictType(diffDealStrand, tdrs),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(diffDealStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tdrs),
				conflictPMTL,
				tdrs.getTerm(),
				tdrs.getTimePeriod()
			),
			pmtlFullConflictKeyEquivalence::equivalent
		));
	}
}
