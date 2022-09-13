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
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;

public class ConflictRollupTest_featureConflict extends ConflictRollupTest {
	// have a conflict on a feature
	@Test
	public void featureConflictTest() {
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		{
			TestDealStrand dummyDealStrand = new TestDealStrand(simpleEpisodeLicense);
			dummyDealStrand.setProduct(FEATURE);
			
			leafConflicts.add(new TestConflict(
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(FEATURE, basc, dallas, french),
					dummyDealStrand.getTerm(),
					dummyDealStrand.getTimePeriod()
				),
				null,
				ConflictStatus.DEFAULT,
				null
			));
		}
		

		List<RollupType> rollupOrder = Arrays.asList(RollupType.PRODUCT, RollupType.MEDIA, RollupType.TERRITORY, RollupType.LANGUAGE);
		
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

		assertEquals(1, rolledConflicts.size());
		assertTrue(sanitizedContains(
			rolledConflicts,
			leafConflicts.get(0).getConflictKey(), 
			fullConflictKeyEquivalence::equivalent,
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy
		));
	}
}
