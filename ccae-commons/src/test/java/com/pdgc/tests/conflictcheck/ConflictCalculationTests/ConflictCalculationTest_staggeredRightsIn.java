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
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_staggeredRightsIn extends ConflictCalculationTest {
	
	@Test
	public void staggeredRightsInTest() {
		DealRightStrand _1386450;
		DealRightStrand _1386451;
		// Primary strands
		{
			TestDealStrand tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
			tempRightStrand.setRightStrandId(1386450l);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 1302L, "2211023"));
			tempRightStrand.setMTL(new MTL(basc, canada, english));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			tempRightStrand.setTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			tempRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			_1386450 = tempRightStrand;
	
			tempRightStrand = new TestDealStrand(simpleEpisodeLicense);
			tempRightStrand.setRightStrandId(1386451l);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 1303L, "2211023"));
			tempRightStrand.setMTL(new MTL(basc, canada, french));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, french));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			tempRightStrand.setTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			tempRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			_1386451 = tempRightStrand;
		}
		
		TestDistributionStrand distrRights = new TestDistributionStrand(seriesDistributionRights);
		distrRights.setMedia(allMedia);
		distrRights.setTerm(new Term(DateTimeUtil.createDate(1900, 1, 1), DateTimeUtil.createDate(2017, 1, 31)));
		distrRights.setOrigTerm(new Term(DateTimeUtil.createDate(1900, 1, 1), DateTimeUtil.createDate(2017, 1, 31)));		
		
		TestDistributionStrand distrRights2 = new TestDistributionStrand(seriesDistributionRights);
		distrRights2.setMedia(allMedia);
		distrRights2.setTerm(new Term(DateTimeUtil.createDate(2017, 2, 10), Constants.PERPETUITY));
		distrRights2.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 2, 10), Constants.PERPETUITY));
		

		List<RightStrand> primaryRightStrands = Arrays.asList(_1386450, _1386451);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distrRights, distrRights2);
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

		assertEquals(2, getNumConflictsIgnorePMTL(primaryLeafConflicts));		
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386450),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				_1386450.getPMTL(),
				new Term(DateTimeUtil.createDate(2017, 2, 1), DateTimeUtil.createDate(2017, 2, 9)),
				_1386450.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386451),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				_1386451.getPMTL(),
				new Term(DateTimeUtil.createDate(2017, 2, 1), DateTimeUtil.createDate(2017, 2, 9)),
				_1386451.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
