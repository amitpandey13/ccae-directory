package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

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
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.NonAggregateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

/**
 * Tests for availability for Catch-ups with episode limits. 
 * 
 * A catchup block must be greater than a catchup grant to be available. Otherwise, it's unavailable. 
 *  
 * @author Clara Hong
 *
 */
public class AvailsCalculationTest_catchupRollingEpisodeLimit extends AvailsCalculationTest {
	
	/**
	 * Request a catchup grant (ep limit 6), with existing block (ep limit 4). Expect NO availability. 
	 */
	@Test
	public void requestedCatchupLimitGreaterThanBlockLimit() {
		// Build Right ins
		TestDealStrand catchupBlock = new TestDealStrand(simpleEpisodeLicense);
		catchupBlock.setCustomer(dummyCustomer);
		catchupBlock.setRightType(TestRightType.CATCHUP_BLOCK_ROLLING_4);
		List<RightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlock);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1; 
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_ROLLING_6),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_ROLLING_6);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}

	/**
	 * Request a catchup grant (ep limit 4), with existing block (ep limit 4). Expect NO availability. 
	 */
	@Test
	public void requestedCatchupLimitEqualsBlockLimit() {
		// Build Right ins
		TestDealStrand catchupBlock = new TestDealStrand(simpleEpisodeLicense);
		catchupBlock.setCustomer(dummyCustomer);
		catchupBlock.setRightType(TestRightType.CATCHUP_BLOCK_ROLLING_4);
		List<NonAggregateRightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlock);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_ROLLING_4),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_ROLLING_4);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
	
	/**
	 * Request a catchup grant (ep limit 4), with existing block (ep limit 6). Expect availability. 
	 */
	@Test
	public void requestedCatchupLimitLessThanBlockLimit() {
		// Build Right ins
		TestDealStrand catchupBlock = new TestDealStrand(simpleEpisodeLicense);
		catchupBlock.setCustomer(dummyCustomer);
		catchupBlock.setRightType(TestRightType.CATCHUP_BLOCK_ROLLING_6);
		List<NonAggregateRightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlock);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_ROLLING_4),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_ROLLING_4);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
	
	/**
	 * Request a catchup block (ep limit 6), with existing grant (ep limit 4). Expect availability. 
	 */
	@Test
	public void requestedCatchupBlockLimitGreaterThanCatchupLimit() {
		// Build Right ins
		TestDealStrand catchupBlockRight = new TestDealStrand(simpleEpisodeLicense);
		catchupBlockRight.setCustomer(dummyCustomer);
		catchupBlockRight.setRightType(TestRightType.CATCHUP_ROLLING_4);
		List<NonAggregateRightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlockRight);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_6),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_6);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
	
	/**
	 * Request a catchup block (ep limit 4), with existing grant (ep limit 4). Expect NO availability. 
	 */
	@Test
	public void requestedCatchupBlockLimitEqualsCatchupLimit() {
		// Build Right ins
		TestDealStrand catchupBlockRight = new TestDealStrand(simpleEpisodeLicense);
		catchupBlockRight.setCustomer(dummyCustomer);
		catchupBlockRight.setRightType(TestRightType.CATCHUP_ROLLING_4);
		List<NonAggregateRightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlockRight);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_4),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_4);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
	
	/**
	 * Request a catchup block (ep limit 4), with existing grant (ep limit 6). Expect NO availability. 
	 */
	@Test
	public void requestedCatchupBlockLimitLessThanCatchupLimit() {
		// Build Right ins
		TestDealStrand catchupBlockRight = new TestDealStrand(simpleEpisodeLicense);
		catchupBlockRight.setCustomer(dummyCustomer);
		catchupBlockRight.setRightType(TestRightType.CATCHUP_ROLLING_6);
		List<NonAggregateRightStrand> rightStrands = Arrays.asList(seriesDistributionRights, catchupBlockRight);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_4),
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
		
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;

        RightRequest catchupRequest = new RightRequest(TestRightType.CATCHUP_BLOCK_ROLLING_4);
		// Episode 1: the ony episode with cuts
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
	
				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Other episodes: epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					ptv,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, catchupRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
}
