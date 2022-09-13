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

public class ConflictCalculationTest_overrideEncompassedWithinConflict extends ConflictCalculationTest {
	// Test that an override that is encompassed within a conflict will tag the
	// proper conflicts and leave the remainder conflicts
	@Category(Categories.OverrideReApply.class)
	@Test
	public void overrideEncompassedWithinConflict() {
		TestDealStrand tdrs = new TestDealStrand(simpleEpisodeLicense);
		tdrs.setTerrLang(new TerrLang(canada, english));
		tdrs.setActualTerrLang(new TerrLang(canada, english));
		tdrs.setPMTL(new PMTL(simpleEpisodeLicense.getPMTL().getProduct(), ptv, canada, english));
		tdrs.setActualPMTL(new PMTL(simpleEpisodeLicense.getPMTL().getProduct(), ptv, canada, english));
		
		ConflictOverride ep1FullCanadaEnglishPTVLOverride = new ConflictOverride(
			1L,
			1L,
			new ConflictOverrideType(-1L, "randomType"), "dummy override comment",
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(tdrs),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				tdrs.getPMTL(),
				new Term(simpleEpisodeLicense.getTerm().getStartDate().plusDays(1), simpleEpisodeLicense.getTerm().getEndDate().plusDays(-1)), 
				tdrs.getTimePeriod()
			)
		);

		List<RightStrand> primaryRightStrands = Arrays.asList(tdrs);
		List<RightStrand> conflictingRightStrands = new ArrayList<>();
		List<ConflictOverride> existingOverrides = Arrays.asList(ep1FullCanadaEnglishPTVLOverride);
		
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
			assertTrue(canadaEnglishPTVLConflict.getConflictOverrides().contains(ep1FullCanadaEnglishPTVLOverride));
			assertEquals(ConflictStatus.NEEDS_ACK, canadaEnglishPTVLConflict.getConflictStatus());
		}
	
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
