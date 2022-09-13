package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;

public class ConflictCalculationTest_ptviNewYorkEnglishDistributionRightsVsDealPtvUsaAllMedia extends ConflictCalculationTest {
	@Test
	public void ptviNewYorkEnglishDistributionRightsVsDealPtvUsaAllMediaTest() {
		TestDealStrand tempRightStrand = new TestDealStrand(simpleSeasonLicense);
		tempRightStrand.setRightStrandId(1386450l);
		tempRightStrand.setMTL(new MTL(ptv, usa, Constants.ALL_LANGUAGES));
		tempRightStrand.setActualTerrLang(new TerrLang(usa, Constants.ALL_LANGUAGES));
		tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
	
		//create Distribution Rights Strand
		TestDistributionStrand distrRights = new TestDistributionStrand(seriesDistributionRights);
		distrRights.setMedia(ptvi);
		distrRights.setTerrLang(new TerrLang(newYork, english));
		
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
		
		Collection<PMTL> leafConflictPMTLs = getLeafPMTLs(
			getPMTLs(tempRightStrand.getPMTL()),
			productHierarchy,
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		);
		leafConflictPMTLs.removeAll(getLeafPMTLs(
			getPMTLs(distrRights.getPMTL()),
			productHierarchy,
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		for (PMTL leafPMTL : leafConflictPMTLs) {
			assertTrue(containsConflict(
				primaryLeafConflicts,
				new TestConflictKey(
					ConflictConstants.NO_CORP_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tempRightStrand),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					leafPMTL,
					tempRightStrand.getTerm(),
					tempRightStrand.getTimePeriod()
				),
				productHierarchy, 
				mediaHierarchy, 
				territoryHierarchy, 
				languageHierarchy
			));	
		}
	
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
