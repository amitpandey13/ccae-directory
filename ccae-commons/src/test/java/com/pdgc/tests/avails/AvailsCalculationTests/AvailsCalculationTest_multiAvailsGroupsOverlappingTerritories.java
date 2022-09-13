package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.Assert.assertTrue;
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
import com.pdgc.general.structures.Product;
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

public class AvailsCalculationTest_multiAvailsGroupsOverlappingTerritories extends AvailsCalculationTest {
    //multiple territory groups, each with multiple terr/langs and multiple media/right scope combinations, terrlangs can overlap across groups
    @Test
    public void multiAvailsGroupsOverlappingTerritoriesTest() throws FileNotFoundException
    {
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
            USEnglishPTVDistributionRights.setRightStrandId(1);
            USEnglishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
            USEnglishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, english)));

            USSpanishPTVDistributionRights.setRightStrandId(2);
            USSpanishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, spanish)));
            USSpanishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, spanish)));

            USFrenchPTVDistributionRights.setRightStrandId(3);
            USFrenchPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, french)));
            USFrenchPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, french)));

            USEnglishBASCDistributionRights.setRightStrandId(4);
            USEnglishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, english)));
            USEnglishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, english)));

            USSpanishBASCDistributionRights.setRightStrandId(5);
            USSpanishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, spanish)));
            USSpanishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, spanish)));

            USFrenchBASCDistributionRights.setRightStrandId(6);
            USFrenchBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, french)));
            USFrenchBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, french)));

            MexicoEnglishPPVDistributionRights.setRightStrandId(7);
            MexicoEnglishPPVDistributionRights.setActualMTL(new MTL(ppv, new TerrLang(mexico, english)));
            MexicoEnglishPPVDistributionRights.setMTL(new MTL(ppv, new TerrLang(mexico, english)));

            MexicoSpanishPPVDistributionRights.setRightStrandId(8);
            MexicoSpanishPPVDistributionRights.setActualMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
            MexicoSpanishPPVDistributionRights.setMTL(new MTL(ppv, new TerrLang(mexico, spanish)));

            CanadaEnglishPTVDistributionRights.setRightStrandId(9);
            CanadaEnglishPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(canada, english)));
            CanadaEnglishPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(canada, english)));

            CanadaFrenchPTVDistributionRights.setRightStrandId(10);
            CanadaFrenchPTVDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(canada, french)));
            CanadaFrenchPTVDistributionRights.setMTL(new MTL(ptv, new TerrLang(canada, french)));

            CanadaEnglishBASCDistributionRights.setRightStrandId(11);
            CanadaEnglishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(canada, english)));
            CanadaEnglishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(canada, english)));

            CanadaFrenchBASCDistributionRights.setRightStrandId(12);
            CanadaFrenchBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(canada, french)));
            CanadaFrenchBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(canada, french)));

            USEnglishPTVEpisode1License.setRightStrandId(13);
            USEnglishPTVEpisode1License.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
            USEnglishPTVEpisode1License.setMTL(new MTL(ptv, new TerrLang(usa, english)));
            USEnglishPTVEpisode1License.setRightType(TestRightType.EXCLUSIVE_LICENSE);

            MexicoSpanishPPVEpisode1Holdback.setRightStrandId(14);
            MexicoSpanishPPVEpisode1Holdback.setActualMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
            MexicoSpanishPPVEpisode1Holdback.setMTL(new MTL(ppv, new TerrLang(mexico, spanish)));
            MexicoSpanishPPVEpisode1Holdback.setRightType(TestRightType.HOLDBACK);

            CanadaFrenchBASCEpisode1TetheredVOD.setRightStrandId(15);
            CanadaFrenchBASCEpisode1TetheredVOD.setActualMTL(new MTL(basc, new TerrLang(canada, french)));
            CanadaFrenchBASCEpisode1TetheredVOD.setMTL(new MTL(basc, new TerrLang(canada, french)));
            CanadaFrenchBASCEpisode1TetheredVOD.setRightType(TestRightType.TETHERED_VOD);
        }

        List<RightStrand> rightStrands = Arrays.asList(
            USEnglishPTVDistributionRights,
            USSpanishPTVDistributionRights,
            USFrenchPTVDistributionRights,
            USEnglishBASCDistributionRights,
            USSpanishBASCDistributionRights,
            USFrenchBASCDistributionRights,
            MexicoEnglishPPVDistributionRights,
            MexicoSpanishPPVDistributionRights,
            CanadaEnglishPTVDistributionRights,
            CanadaFrenchPTVDistributionRights,
            CanadaEnglishBASCDistributionRights,
            CanadaFrenchBASCDistributionRights,
            USEnglishPTVEpisode1License,
            MexicoSpanishPPVEpisode1Holdback,
            CanadaFrenchBASCEpisode1TetheredVOD
        );
        
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
                    .medias(Sets.newHashSet(ptv, basc))
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
        
		RightRequest tetheredVodRequest = new RightRequest(TestRightType.TETHERED_VOD);
		
        Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;
        
        //Episode 1: only one with date cuts
        {
            //PTV/US/English results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USEnglishPTVDistributionRights.getPMTL().getMedia(), 
					USEnglishPTVDistributionRights.getPMTL().getTerritory(), 
					USEnglishPTVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
                	//term: epoch to 2017 - has nothing but distribution rights
	                {
	                    term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
	
	                //term: year 2017 - has an exclusive license
	                {
	                    term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
	
	                //term: year 2018 to perpetuity - has nothing but distribution rights
	                {
	                    term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
                }
            }

            //PTV/US/Spanish results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USSpanishPTVDistributionRights.getPMTL().getMedia(), 
					USSpanishPTVDistributionRights.getPMTL().getTerritory(), 
					USSpanishPTVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
                	term = new Term(Constants.EPOCH, Constants.PERPETUITY);
                	validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                }
            }

            //PTV/US/French results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USFrenchPTVDistributionRights.getPMTL().getMedia(), 
					USFrenchPTVDistributionRights.getPMTL().getTerritory(), 
					USFrenchPTVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                }
            }

            //BASC/US/English results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USEnglishBASCDistributionRights.getPMTL().getMedia(), 
					USEnglishBASCDistributionRights.getPMTL().getTerritory(), 
					USEnglishBASCDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                }
            }

            //BASC/US/Spanish results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USSpanishBASCDistributionRights.getPMTL().getMedia(), 
					USSpanishBASCDistributionRights.getPMTL().getTerritory(), 
					USSpanishBASCDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	            }
            }

            //BASC/US/French results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					USFrenchBASCDistributionRights.getPMTL().getMedia(), 
					USFrenchBASCDistributionRights.getPMTL().getTerritory(), 
					USFrenchBASCDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	            }
            }

            //PPV/Mexico/English results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					MexicoEnglishPPVDistributionRights.getPMTL().getMedia(), 
					MexicoEnglishPPVDistributionRights.getPMTL().getTerritory(), 
					MexicoEnglishPPVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	            }
            }

            //PPV/Mexico/Spanish results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					MexicoSpanishPPVDistributionRights.getPMTL().getMedia(), 
					MexicoSpanishPPVDistributionRights.getPMTL().getTerritory(), 
					MexicoSpanishPPVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                //term: epoch to 2017 - has nothing but distribution rights
	                {
	                    term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
	
	                //term: year 2017 - has an ep 1 holdback
	                {
	                    term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
	
	                //term: year 2018 to perpetuity - has nothing but distribution rights
	                {
	                    term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
                }
            }

            //PTV/Canada/English results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					CanadaEnglishPTVDistributionRights.getPMTL().getMedia(), 
					CanadaEnglishPTVDistributionRights.getPMTL().getTerritory(), 
					CanadaEnglishPTVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	            }
            }

            //PTV/Canada/French results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					CanadaFrenchPTVDistributionRights.getPMTL().getMedia(), 
					CanadaFrenchPTVDistributionRights.getPMTL().getTerritory(), 
					CanadaFrenchPTVDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	            }
            }

            //BASC/Canada/English results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					CanadaEnglishBASCDistributionRights.getPMTL().getMedia(), 
					CanadaEnglishBASCDistributionRights.getPMTL().getTerritory(), 
					CanadaEnglishBASCDistributionRights.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                term = new Term(Constants.EPOCH, Constants.PERPETUITY);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	            }
            }

            //BASC/Canada/French results
            {
                pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					CanadaFrenchBASCEpisode1TetheredVOD.getPMTL().getMedia(), 
					CanadaFrenchBASCEpisode1TetheredVOD.getPMTL().getTerritory(), 
					CanadaFrenchBASCEpisode1TetheredVOD.getPMTL().getLanguage()
				);
                assertTrue(!pmtlIdSets.isEmpty());
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                //term: epoch to 2017 - has nothing but distribution rights
	                {
	                    term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
	
	                //term: year 2017 - has an exclusive tethered VOD
	                {
	                    term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
	
	                //term: year 2018 to perpetuity - has nothing but distribution rights
	                {
	                    term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
                }
            }
        }

	    //other episodes : epoch to perpetuity
	    {
	        term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				//PTV/US/English results
		        {
		            
		        	pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USEnglishPTVDistributionRights.getPMTL().getMedia(), 
						USEnglishPTVDistributionRights.getPMTL().getTerritory(), 
						USEnglishPTVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //PTV/US/Spanish results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USSpanishPTVDistributionRights.getPMTL().getMedia(), 
						USSpanishPTVDistributionRights.getPMTL().getTerritory(), 
						USSpanishPTVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //PTV/US/French results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USFrenchPTVDistributionRights.getPMTL().getMedia(), 
						USFrenchPTVDistributionRights.getPMTL().getTerritory(), 
						USFrenchPTVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //BASC/US/English results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USEnglishBASCDistributionRights.getPMTL().getMedia(), 
						USEnglishBASCDistributionRights.getPMTL().getTerritory(), 
						USEnglishBASCDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //BASC/US/Spanish results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USSpanishBASCDistributionRights.getPMTL().getMedia(), 
						USSpanishBASCDistributionRights.getPMTL().getTerritory(), 
						USSpanishBASCDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                        validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //BASC/US/French results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						USFrenchBASCDistributionRights.getPMTL().getMedia(), 
						USFrenchBASCDistributionRights.getPMTL().getTerritory(), 
						USFrenchBASCDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //PPV/Mexico/English results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						MexicoEnglishPPVDistributionRights.getPMTL().getMedia(), 
						MexicoEnglishPPVDistributionRights.getPMTL().getTerritory(), 
						MexicoEnglishPPVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //PPV/Mexico/Spanish results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						MexicoSpanishPPVDistributionRights.getPMTL().getMedia(), 
						MexicoSpanishPPVDistributionRights.getPMTL().getTerritory(), 
						MexicoSpanishPPVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                }
		        }
	
		        //PTV/Canada/English results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						CanadaEnglishPTVDistributionRights.getPMTL().getMedia(), 
						CanadaEnglishPTVDistributionRights.getPMTL().getTerritory(), 
						CanadaEnglishPTVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
		        }
	
		        //PTV/Canada/French results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						CanadaFrenchPTVDistributionRights.getPMTL().getMedia(), 
						CanadaFrenchPTVDistributionRights.getPMTL().getTerritory(), 
						CanadaFrenchPTVDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
		        }
	
		        //BASC/Canada/English results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						CanadaEnglishBASCDistributionRights.getPMTL().getMedia(), 
						CanadaEnglishBASCDistributionRights.getPMTL().getTerritory(), 
						CanadaEnglishBASCDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
		        }
		
		        //BASC/Canada/French results
		        {
		            pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						CanadaFrenchBASCDistributionRights.getPMTL().getMedia(), 
						CanadaFrenchBASCDistributionRights.getPMTL().getTerritory(), 
						CanadaFrenchBASCDistributionRights.getPMTL().getLanguage()
					);
	                assertTrue(!pmtlIdSets.isEmpty());
	                
	                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
	                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, tetheredVodRequest, term, timePeriod);
	                }
		        }
			}
        }
    }
}
