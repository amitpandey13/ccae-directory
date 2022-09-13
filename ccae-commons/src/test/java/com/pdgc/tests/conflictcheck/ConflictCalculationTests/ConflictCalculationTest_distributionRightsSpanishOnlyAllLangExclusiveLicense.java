package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
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
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;

public class ConflictCalculationTest_distributionRightsSpanishOnlyAllLangExclusiveLicense extends ConflictCalculationTest {
	@Test
	public void distributionRightsSpanishOnlyAllLangExclusiveLicenseTest() {
		// Set up primary licenses for Mexico Spanish Excl
		TestDealStrand tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
		tempRightStrand.setRightStrandId(tempRightStrand.getRightStrandId() + 1);
		tempRightStrand.setActualTerrLang(new TerrLang(mexico, Constants.ALL_LANGUAGES));
		tempRightStrand.setTerrLang(new TerrLang(mexico, Constants.ALL_LANGUAGES));
		tempRightStrand.setRightSource(simpleEpisodeLicense.getRightSource());
		tempRightStrand.setProduct(Seinfeld_SEASON_1);
		tempRightStrand.setActualProduct(Seinfeld_SEASON_1);
		tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
		
		// Set up distribution rights-in
		TestDistributionStrand wwSpanishDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		PMTL pmtl = new PMTL(Seinfeld_SERIES, allMedia, Constants.WORLD, spanish);
		wwSpanishDistributionRights.setActualPMTL(pmtl);
		wwSpanishDistributionRights.setPMTL(pmtl);
		
		Collection<RightStrand> primaryRightStrands = Arrays.asList(tempRightStrand);
		List<RightStrand> conflictingRightStrands = Arrays.asList(wwSpanishDistributionRights);
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
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tempRightStrand),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				new PMTL(tempRightStrand.getPMTL().getProduct(), ptv, mexico, english),
				tempRightStrand.getTerm(),
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
