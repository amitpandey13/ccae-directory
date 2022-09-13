package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverrideType;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.test.category.Categories;

public class ConflictCalculationTest_overrideOverlapsWithConflictSeasonLevel extends ConflictCalculationTest {
	@Category(Categories.OverrideReApply.class)
	@Test
	public void overrideOverlapsWithConflictSeasonLevelTest() {
		TestDealStrand tdrs = new TestDealStrand(simpleSeasonLicense);
		tdrs.setTerrLang(new TerrLang(canada, english));
		tdrs.setActualTerrLang(new TerrLang(canada, english));
		tdrs.setPMTL(new PMTL(simpleSeasonLicense.getPMTL().getProduct(), ptv, canada, english));
		tdrs.setActualPMTL(new PMTL(simpleSeasonLicense.getPMTL().getProduct(), ptv, canada, english));
		
		ConflictOverride season1FullCanadaEnglishPTVLOverride = new ConflictOverride(
			1L,
			1L,
			new ConflictOverrideType(-1L, "randomType"), "dummy override comment",
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tdrs),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				tdrs.getPMTL(),
				new Term(simpleSeasonLicense.getTerm().getStartDate().plusDays(1), simpleSeasonLicense.getTerm().getEndDate().plusDays(1)), 
				tdrs.getTimePeriod()
			)
		);

		List<RightStrand> primaryRightStrands = Arrays.asList(tdrs);
		List<RightStrand> conflictingRightStrands = new ArrayList<>();
		List<ConflictOverride> existingOverrides = Arrays.asList(season1FullCanadaEnglishPTVLOverride);
		
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
		{
			assertTrue(containsConflict(
				primaryLeafConflicts,
				new TestConflictKey(
					ConflictConstants.NO_CORP_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tdrs),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					tdrs.getPMTL(),
					tdrs.getTerm(),
					tdrs.getTimePeriod()
				),
				productHierarchy, 
				mediaHierarchy, 
				territoryHierarchy, 
				languageHierarchy
			));
			
			Conflict canadaEnglishPTVLConflict = CollectionsUtil.findFirst(primaryLeafConflicts);
			
			assertEquals(1, canadaEnglishPTVLConflict.getConflictOverrides().size());
			assertTrue(canadaEnglishPTVLConflict.getConflictOverrides().contains(season1FullCanadaEnglishPTVLOverride));
			assertEquals(ConflictStatus.NEEDS_ACK, canadaEnglishPTVLConflict.getConflictStatus());
		}
	
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
