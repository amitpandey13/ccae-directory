package com.pdgc.tests.conflictcheck.ConflictRollupTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.pdgc.conflictcheck.service.ConflictCheckRunner;
import com.pdgc.conflictcheck.service.ConflictRollup.RollupType;
import com.pdgc.conflictcheck.service.OverrideApplier;
import com.pdgc.conflictcheck.service.TestConflictCheckRunner;
import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictRollupTest_unrelatedConflicts extends ConflictRollupTest {
	// Test that conflicts with completely unrelated PMTLs don't attempt to roll up
	@Test
	public void unrelatedConflictsTest() {
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		{
			leafConflicts.add(new TestConflict(
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(NCIS_SEASON_1_EPISODE_01, basc, dallas, french),
					new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
					simpleEpisodeLicense.getTimePeriod()
				),
				null,
				ConflictStatus.DEFAULT,
				null
			));
			
			leafConflicts.add(new TestConflict(
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(NCIS_SEASON_1_EPISODE_02, ptvc, mexico, english),
					new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
					simpleEpisodeLicense.getTimePeriod()
				),
				null,
				ConflictStatus.DEFAULT,
				null
			));
		}

		List<RollupType> rollupOrder = Arrays.asList( RollupType.PRODUCT, RollupType.MEDIA, RollupType.TERRITORY, RollupType.LANGUAGE);
		
		ConflictCheckRunner<TestConflict> conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
		
		List<TestConflict> rolledConflicts = conflictCheckRunner.generateRolledConflicts(
			leafConflicts, 
			overrides,
			conflictBuilder,
			rollupOrder,
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get
		);

		assertEquals(2, rolledConflicts.size());
		assertTrue(sanitizedContains(
			rolledConflicts,
			leafConflicts.get(0).getConflictKey(), 
			fullConflictKeyEquivalence::equivalent,
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy
		));
		assertTrue(sanitizedContains(
			rolledConflicts,
			leafConflicts.get(1).getConflictKey(), 
			fullConflictKeyEquivalence::equivalent,
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy
		));
	}
}
