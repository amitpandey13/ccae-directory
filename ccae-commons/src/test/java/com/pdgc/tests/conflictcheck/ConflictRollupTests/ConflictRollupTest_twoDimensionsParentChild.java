package com.pdgc.tests.conflictcheck.ConflictRollupTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

public class ConflictRollupTest_twoDimensionsParentChild extends ConflictRollupTest {

	/**
	 * Tests what happens given child conflicts that are different in two dimensions
	 * but are still related, b/c one of the differences is just parent/child
	 * 
	 * Ep1/BASC/USA/English and Ep2/BASC/New York/English
	 * 
	 * could roll up to Ep1+Ep2/BASC/New York/English and Ep1/BASC/USA-New York/English
	 * 
	 * 
	 */
	@Test
	public void twoDimensionsParentChildTest()
	{
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		{
			leafConflicts.add(new TestConflict(
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(NCIS_SEASON_1_EPISODE_01, basc, usa, english),
					simpleEpisodeLicense.getTerm(),
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
					new PMTL(NCIS_SEASON_1_EPISODE_02, basc, newYork, english),
					simpleEpisodeLicense.getTerm(),
					simpleEpisodeLicense.getTimePeriod()
				),
				null,
				ConflictStatus.DEFAULT,
				null
			));
		}

		List<RollupType> rollupOrder = Arrays.asList();
		
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
		
		Collection<Product> sourceProducts;
		Collection<Media> sourceMedias;
		Collection<Territory> sourceTerritories;
		Collection<Language> sourceLanguages;
		
		assertEquals(2, rolledConflicts.size());
		{
			sourceProducts = Arrays.asList(NCIS_SEASON_1_EPISODE_01, NCIS_SEASON_1_EPISODE_02);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Collections.singleton(newYork);
			sourceLanguages = Collections.singleton(english);
			
			assertTrue(sanitizedContains(
				rolledConflicts, 
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(
						new AggregateProduct(sourceProducts),
						new AggregateMedia(sourceMedias),
						new AggregateTerritory(sourceTerritories),
						new AggregateLanguage(sourceLanguages)
					),
					simpleEpisodeLicense.getTerm(),
					simpleEpisodeLicense.getTimePeriod()
				), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
		}
		
		{
			sourceProducts = Collections.singleton(NCIS_SEASON_1_EPISODE_01);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = territoryHierarchy.getLeaves(usa);
			sourceTerritories.remove(newYork);
			sourceLanguages = Collections.singleton(english);
			
			assertTrue(sanitizedContains(
				rolledConflicts, 
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(
						new AggregateProduct(sourceProducts),
						new AggregateMedia(sourceMedias),
						new AggregateTerritory(sourceTerritories),
						new AggregateLanguage(sourceLanguages)
					),
					simpleEpisodeLicense.getTerm(),
					simpleEpisodeLicense.getTimePeriod()
				), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
		}
	}
}
