package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.calculation.AvailsCalculationResult;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_multiTerritoryGroupOverlappingTerritories extends AvailsCalculationTest {

	// multiple territory groups, each with multiple terr/langs and multiple
	// media/right scope combinations, terrlangs can overlap across groups
	@Test
	public void multiTerritoryGroupOverlappingTerritoriesTest()
			throws FileNotFoundException {
		TestDistributionStrand USEnglishPTVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USSpanishPTVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USFrenchPTVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USEnglishBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USSpanishBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USFrenchBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand MexicoEnglishPPVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand MexicoSpanishPPVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand CanadaEnglishPTVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand CanadaFrenchPTVDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand CanadaEnglishBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand CanadaFrenchBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDealStrand USEnglishPTVEpisode1License = new TestDealStrand(simpleEpisodeLicense);
		TestDealStrand MexicoSpanishPPVEpisode1Holdback = new TestDealStrand(simpleEpisodeLicense);
		TestDealStrand CanadaFrenchBASCEpisode1TetheredVOD = new TestDealStrand(simpleEpisodeLicense);

		{
			USEnglishPTVDistributionRights.setRightStrandId(1L);
			USEnglishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, english)));

			USSpanishPTVDistributionRights.setRightStrandId(2L);
			USSpanishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, spanish)));
			USSpanishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, spanish)));

			USFrenchPTVDistributionRights.setRightStrandId(3L);
			USFrenchPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, french)));
			USFrenchPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, french)));

			USEnglishBASCDistributionRights.setRightStrandId(4L);
			USEnglishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, english)));

			USSpanishBASCDistributionRights.setRightStrandId(5L);
			USSpanishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, spanish)));
			USSpanishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, spanish)));

			USFrenchBASCDistributionRights.setRightStrandId(6L);
			USFrenchBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, french)));
			USFrenchBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, french)));

			MexicoEnglishPPVDistributionRights.setRightStrandId(7L);
			MexicoEnglishPPVDistributionRights.setActualMTL(new MTL(ppv, new TerrLang(mexico, english)));
			MexicoEnglishPPVDistributionRights.setMTL(new MTL(ppv, new TerrLang(mexico, english)));

			MexicoSpanishPPVDistributionRights.setRightStrandId(8L);
			MexicoSpanishPPVDistributionRights.setActualMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
			MexicoSpanishPPVDistributionRights.setMTL(new MTL(ppv, new TerrLang(mexico, spanish)));

			CanadaEnglishPTVDistributionRights.setRightStrandId(9L);
			CanadaEnglishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(canada, english)));
			CanadaEnglishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(canada, english)));

			CanadaFrenchPTVDistributionRights.setRightStrandId(10L);
			CanadaFrenchPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(canada, french)));
			CanadaFrenchPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(canada, french)));

			CanadaEnglishBASCDistributionRights.setRightStrandId(11L);
			CanadaEnglishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(canada, english)));
			CanadaEnglishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(canada, english)));

			CanadaFrenchBASCDistributionRights.setRightStrandId(12L);
			CanadaFrenchBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(canada, french)));
			CanadaFrenchBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(canada, french)));

			USEnglishPTVEpisode1License.setRightStrandId(13L);
			USEnglishPTVEpisode1License.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPTVEpisode1License.setMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPTVEpisode1License.setRightType(TestRightType.EXCLUSIVE_LICENSE);

			MexicoSpanishPPVEpisode1Holdback.setRightStrandId(14L);
			MexicoSpanishPPVEpisode1Holdback.setActualMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
			MexicoSpanishPPVEpisode1Holdback.setMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
			MexicoSpanishPPVEpisode1Holdback.setRightType(TestRightType.HOLDBACK);

			CanadaFrenchBASCEpisode1TetheredVOD.setRightStrandId(15L);
			CanadaFrenchBASCEpisode1TetheredVOD.setActualMTL(new MTL(basc, new TerrLang(canada, french)));
			CanadaFrenchBASCEpisode1TetheredVOD.setMTL(new MTL(basc, new TerrLang(canada, french)));
			CanadaFrenchBASCEpisode1TetheredVOD.setRightType(TestRightType.TETHERED_VOD);
		}

		List<RightStrand> rightStrands = Arrays.asList(
				USEnglishPTVDistributionRights, USSpanishPTVDistributionRights, USFrenchPTVDistributionRights,
				USEnglishBASCDistributionRights, USSpanishBASCDistributionRights, USFrenchBASCDistributionRights,
				MexicoEnglishPPVDistributionRights, MexicoSpanishPPVDistributionRights,
				CanadaEnglishPTVDistributionRights, CanadaFrenchPTVDistributionRights,
				CanadaEnglishBASCDistributionRights, CanadaFrenchBASCDistributionRights, USEnglishPTVEpisode1License,
				MexicoSpanishPPVEpisode1Holdback, CanadaFrenchBASCEpisode1TetheredVOD);
		
		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1;
            Set<CriteriaSource> criteriaSources = Sets.newHashSet(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Sets.newHashSet(english, spanish, french))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(canada))
                    .languages(Sets.newHashSet(english, french))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(usa))
                    .languages(Sets.newHashSet(english, spanish, french))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.HOLDBACK),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ppv))
                    .territories(Collections.singleton(mexico))
                    .languages(Sets.newHashSet(english, spanish))
                    .primaryRequests(ImmutableSet.of(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.HOLDBACK),
                            false
                        ),
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(canada))
                    .languages(Sets.newHashSet(english, french))
                    .primaryRequests(ImmutableSet.of(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
                            false
                        ),
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.TETHERED_VOD),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(canada))
                    .languages(Sets.newHashSet(english))
                    .primaryRequests(ImmutableSet.of(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(canada))
                    .languages(Sets.newHashSet(english, french))
                    .primaryRequests(ImmutableSet.of(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.TETHERED_VOD),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build()
            );
                
            when(availsCriteria.getCriteriaSources()).thenReturn(criteriaSources);
            when(availsCriteria.getEvaluatedPrimaryTerm()).thenReturn(Constants.TERM_EPOCH_TO_PERPETUITY);
            when(availsCriteria.getCustomer()).thenReturn(null);
		}

		AvailsRunParams runParams = getDefaultRunParams(availsCriteria);

        AvailsCalculationResult availsCalcResult = runAvails(
            runParams,
            rightStrands, 
            Arrays.asList(Seinfeld_SEASON_1),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );

		Set<Set<LeafPMTLIdSet>> pmtlIdSetsSeason_1_EP_1;
		Set<Set<LeafPMTLIdSet>> pmtlIdSetsSeason_1_EP_2;
		Term term;
		TimePeriod timePeriod = TimePeriod.FULL_WEEK;
		
		RightRequest tetheredVODRequest = new RightRequest(TestRightType.TETHERED_VOD);

		// PTV/US/English results
		{
			pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, ptv, usa, english);
			pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_02, ptv, usa, english);
			assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
			assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
				// term: year 2017 - has an exclusive license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// BASC/US/English results
		{
			pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01, basc, usa, english);
			pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_02, basc, usa, english);
			assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
			assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
			    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
			    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}

		// PPV/Mexico/English results
		{
			pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01, ppv, mexico, english);
			pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_02, ppv, mexico, english);
			assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
			assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
			    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
			    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}

		// PPV/Mexico/Spanish results
		{
			pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01, ppv, mexico, spanish);
			pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_02, ppv, mexico, spanish);
			assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
			assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2017 - has an ep 1 holdback
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	
		// PTV/Canada/English results
        {
            pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_01, ptv, canada, english);
            pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_02, ptv, canada, english);
            assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
            assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());
            term = new Term(Constants.EPOCH, Constants.PERPETUITY);

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }
        }

        // PTV/Canada/French results
        {
            pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_01, ptv, canada, french);
            pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_02, ptv, canada, french);
            assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
            assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());
            term = new Term(Constants.EPOCH, Constants.PERPETUITY);

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }
        }

        // BASC/Canada/English results
        {
            pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_01, basc, canada, english);
            pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_02, basc, canada, english);
            assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
            assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());
            term = new Term(Constants.EPOCH, Constants.PERPETUITY);

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
            }
        }

        // BASC/Canada/French results
        {
            pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_01, basc, canada, french);
            pmtlIdSetsSeason_1_EP_2 = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
                    Seinfeld_SEASON_1_EPISODE_02, basc, canada, french);
            assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
            assertTrue(!pmtlIdSetsSeason_1_EP_2.isEmpty());

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
                // term: epoch to 2017 - has nothing but distribution rights
                {
                    term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
                }

                // term: year 2017 - has an exclusive tethered VOD
                {
                    term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
                }

                // term: year 2018 to perpetuity - has nothing but distribution rights
                {
                    term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
                }
            }

            for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_2) {
                // term: epoch to 2017 - has nothing but distribution rights
                {
                    term = new Term(Constants.EPOCH, Constants.PERPETUITY);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVODRequest, term, timePeriod);
                }
            }
        }
	}
}
