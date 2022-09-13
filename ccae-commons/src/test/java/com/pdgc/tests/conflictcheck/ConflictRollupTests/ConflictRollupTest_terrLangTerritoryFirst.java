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
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictRollupTest_terrLangTerritoryFirst extends ConflictRollupTest {

	/**
	 * Given leaf conflicts that sum up to USA/Spanish, USA/French, and New York English
	 * This can rollup to the terrLangs just stated, or, if language is done first,
	 * Rolls to New York/All, other US-New York/Spanish, US-New York/French
	 * This is the territory first test, so results should be
	 * USA/English,French and New York/English
	 */
	@Test
	public void terrLangTerritoryFirstTest() {
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		
		TestDealStrand dummyDealStrand = new TestDealStrand(simpleEpisodeLicense);
		{
			for (Territory territory : territoryHierarchy.getLeaves(usa)) {
				leafConflicts.add(new TestConflict(
					new TestConflictKey(
						ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
						new PMTL(dummyDealStrand.getPMTL().getProduct(), basc, territory, spanish),
						new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
						dummyDealStrand.getTimePeriod()
					),
					null,
					ConflictStatus.DEFAULT,
					null
				));
				
				leafConflicts.add(new TestConflict(
					new TestConflictKey(
						ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
						new PMTL(dummyDealStrand.getPMTL().getProduct(), basc, territory, french),
						new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
						dummyDealStrand.getTimePeriod()
					),
					null,
					ConflictStatus.DEFAULT,
					null
				));
			}
			
			leafConflicts.add(new TestConflict(
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(dummyDealStrand.getPMTL().getProduct(), basc, newYork, english),
					new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
					dummyDealStrand.getTimePeriod()
				),
				null,
				ConflictStatus.DEFAULT,
				null
			));
		}

		List<RollupType> rollupOrder = Arrays.asList(RollupType.TERRITORY, RollupType.LANGUAGE, RollupType.PRODUCT, RollupType.MEDIA);
		
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
			sourceProducts = Collections.singleton(dummyDealStrand.getPMTL().getProduct());
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Collections.singleton(usa);
			sourceLanguages = Arrays.asList(french, spanish);
			
			assertTrue(sanitizedContains(
				rolledConflicts,
				new TestConflict(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(
						new AggregateProduct(sourceProducts),
						new AggregateMedia(sourceMedias),
						new AggregateTerritory(sourceTerritories),
						new AggregateLanguage(sourceLanguages)
					),
					new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
					dummyDealStrand.getTimePeriod(),
					null,
					ConflictStatus.DEFAULT,
					null
				).getConflictKey(), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
		}
		
		
		{
			sourceProducts = Collections.singleton(dummyDealStrand.getPMTL().getProduct());
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Collections.singleton(newYork);
			sourceLanguages = Collections.singleton(english);
			
			assertTrue(sanitizedContains(
				rolledConflicts,
				new TestConflict(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(
						new AggregateProduct(sourceProducts),
						new AggregateMedia(sourceMedias),
						new AggregateTerritory(sourceTerritories),
						new AggregateLanguage(sourceLanguages)
					),
					new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2025, 12, 31)),
					dummyDealStrand.getTimePeriod(),
					null,
					ConflictStatus.DEFAULT,
					null
				).getConflictKey(), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
		}
	}
}
