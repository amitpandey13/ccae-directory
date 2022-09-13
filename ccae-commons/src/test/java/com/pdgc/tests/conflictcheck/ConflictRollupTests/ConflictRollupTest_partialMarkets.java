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

public class ConflictRollupTest_partialMarkets extends ConflictRollupTest {
	/**
	 * Chicago has all languages, dallas is nonexistent, and the other usa markets just have english
	 */
	@Test
	public void partialMarketsTest()
	{
		List<TestConflict> leafConflicts = new ArrayList<>();
		List<ConflictOverride> overrides = new ArrayList<>();
		{
			TestDealStrand dummyDealStrand = new TestDealStrand(simpleEpisodeLicense);
			dummyDealStrand.setProduct(NCIS_SEASON_1);

			for (Territory market : territoryHierarchy.getLeaves(usa)) {
				if (market.equals(chicago)) {
					leafConflicts.add(new TestConflict(
						new TestConflictKey(
							ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
							TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
							TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
							new PMTL(NCIS_SEASON_1, basc, market, english),
							dummyDealStrand.getTerm(),
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
							new PMTL(NCIS_SEASON_1, basc, market, french),
							dummyDealStrand.getTerm(),
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
							new PMTL(NCIS_SEASON_1, basc, market, spanish),
							dummyDealStrand.getTerm(),
							dummyDealStrand.getTimePeriod()
						),
						null,
						ConflictStatus.DEFAULT,
						null
					));
				}
				else if (market.equals(dallas)) {
					continue;
				}
				else {
					leafConflicts.add(new TestConflict(
						new TestConflictKey(
							ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
							TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(dummyDealStrand),
							TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
							new PMTL(NCIS_SEASON_1, basc, market, english),
							dummyDealStrand.getTerm(),
							dummyDealStrand.getTimePeriod()
						),
						null,
						ConflictStatus.DEFAULT,
						null
					));
				}
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
		
		assertEquals(2, rolledConflicts.size());
		
		//all-territories/english
		{
			sourceProducts = Collections.singleton(NCIS_SEASON_1);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = territoryHierarchy.getLeaves(usa);
			sourceTerritories.remove(dallas);
			sourceLanguages = Arrays.asList(english);
			
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
		
		//chicago/all languages minus english
		{
			sourceProducts = Collections.singleton(NCIS_SEASON_1);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Arrays.asList(chicago);
			sourceLanguages = Arrays.asList(french, spanish);
			
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
