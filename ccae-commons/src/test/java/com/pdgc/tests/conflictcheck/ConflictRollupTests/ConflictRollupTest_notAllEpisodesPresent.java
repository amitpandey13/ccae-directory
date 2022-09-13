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
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;

public class ConflictRollupTest_notAllEpisodesPresent extends ConflictRollupTest {
	@Test
	public void notAllEpisodesPresentTest()
	{
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		{
			for (Product episode : productHierarchy.getLeaves(NCIS_SEASON_1)) {
				if (episode.equals(NCIS_SEASON_1_EPISODE_01)) {
					continue;
				}
				TestDealStrand dummyDealStrand = new TestDealStrand(simpleEpisodeLicense);
				dummyDealStrand.setProduct(episode);

				leafConflicts.add(new TestConflict(
					new TestConflictKey(
						ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
						new PMTL(episode, basc, dallas, english),
						dummyDealStrand.getTerm(),
						dummyDealStrand.getTimePeriod()
					),
					null,
					ConflictStatus.DEFAULT,
					null
				));
			}
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

		Collection<Product> sourceProducts;
		Collection<Media> sourceMedias;
		Collection<Territory> sourceTerritories;
		Collection<Language> sourceLanguages;
		
		assertEquals(1, rolledConflicts.size());
		
		{
			sourceProducts = productHierarchy.getLeaves(NCIS_SEASON_1);
			sourceProducts.remove(NCIS_SEASON_1_EPISODE_01);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Collections.singleton(dallas);
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
