package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

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
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_multiTerritory extends AvailsCalculationTest {
	// 1 territory group, multiple territories, 1 non-exclusive license in 1 territory
	@Test
	public void multiTerritoryTest() throws IOException {
		
		TestDistributionStrand MexicoSeriesDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		MexicoSeriesDistributionRights.setMTL(new MTL(ptv, mexico, spanish));
		MexicoSeriesDistributionRights.setActualMTL(new MTL(ptv, mexico, spanish));

		TestDistributionStrand CanadaSeriesDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		CanadaSeriesDistributionRights.setMTL(new MTL(ptv, canada, english));
		CanadaSeriesDistributionRights.setActualMTL(new MTL(ptv, canada, english));

		TestDealStrand episodeLicense = new TestDealStrand(simpleEpisodeLicense);
		episodeLicense.setMTL(USEnglishPTV);
		episodeLicense.setActualMTL(USEnglishPTV);
		episodeLicense.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		
		List<RightStrand> rightStrands = CollectionsUtil.toList(
			episodeLicense, 
			seriesDistributionRights,
			MexicoSeriesDistributionRights, 
			CanadaSeriesDistributionRights
		);
		
		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1;
            Set<CriteriaSource> criteriaSources = Sets.newHashSet(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Sets.newHashSet(usa, canada))
                    .languages(Collections.singleton(english))
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
                    .territories(Collections.singleton(mexico))
                    .languages(Collections.singleton(spanish))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
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
		
		// Episode 1 - only one with date cuts
		{
			// US/English
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
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
					}
				
					// term: year 2017 - has the license
					{
						term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
						validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
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
			}
			
			// Mexico/Spanish
			{
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					ptv,
					mexico,
					spanish
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
		
			// Canada/English
			{
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					ptv,
					canada,
					english
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
		}
		
		//Other episodes : epoch to perpetuity
		{
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			 
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}
				 
				// US/English
				{
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
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
					}
				}
				
				// Mexico/Spanish
				{
					pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						ptv,
						mexico,
						spanish
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
				
				// Canada/English
				{
					pmtlIdSets = getMappedPMTLIdSets(
						availsCalcResult.getCalcResults().keySet(),
						episode,
						ptv,
						canada,
						english
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
			}
		}
	}
}
