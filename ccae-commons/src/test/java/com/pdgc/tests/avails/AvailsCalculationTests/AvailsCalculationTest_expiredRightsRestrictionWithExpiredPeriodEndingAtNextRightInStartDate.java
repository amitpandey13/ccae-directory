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

import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.calculation.AvailsCalculationResult;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_expiredRightsRestrictionWithExpiredPeriodEndingAtNextRightInStartDate extends AvailsCalculationTest {
	//
	/*
	 * Scenario 2 with expired rights expired rights ending in start date of
	 * next rights
	 *
	 *
	 */
	@Test
	public void expiredRightsRestrictionWithExpiredPeriodEndingAtNextRightInStartDate() throws FileNotFoundException {
		
		TestDistributionStrand rightsIn = new TestDistributionStrand(seriesDistributionRights);
		rightsIn.setProduct(Seinfeld_SEASON_1_EPISODE_01);
		rightsIn.setActualProduct(Seinfeld_SEASON_1_EPISODE_01);
		rightsIn.setTerm(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31)));
		rightsIn.setOrigTerm(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31)));

		TestDistributionStrand expiredRights = new TestDistributionStrand(seriesDistributionRights);
		expiredRights.setProduct(Seinfeld_SEASON_1_EPISODE_01);
		expiredRights.setActualProduct(Seinfeld_SEASON_1_EPISODE_01);
		expiredRights.setTerm(new Term(DateTimeUtil.createDate(2022, 1, 1), DateTimeUtil.createDate(2023, 12, 31)));
		expiredRights.setTerm(new Term(DateTimeUtil.createDate(2022, 1, 1), DateTimeUtil.createDate(2023, 12, 31)));
		expiredRights.setRightSource(crs12347L);
		expiredRights.setRightType(TestRightType.EXPIRED_RIGHTS_RESTRICTION);

		TestDistributionStrand rightsIn2 = new TestDistributionStrand(seriesDistributionRights);
		rightsIn2.setProduct(Seinfeld_SEASON_1_EPISODE_01);
		rightsIn2.setActualProduct(Seinfeld_SEASON_1_EPISODE_01);
		rightsIn2.setTerm(new Term(DateTimeUtil.createDate(2024, 1, 1), DateTimeUtil.createDate(2026, 12, 31)));
		rightsIn2.setTerm(new Term(DateTimeUtil.createDate(2024, 1, 1), DateTimeUtil.createDate(2026, 12, 31)));

		List<RightStrand> rightStrands = Arrays.asList(rightsIn, expiredRights, rightsIn2);
		
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
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build()
            );
                
            when(availsCriteria.getCriteriaSources()).thenReturn(criteriaSources);
            when(availsCriteria.getEvaluatedPrimaryTerm()).thenReturn(new Term(
                DateTimeUtil.createDate(2020, 1, 1), 
                DateTimeUtil.createDate(2026, 12, 31)));
            when(availsCriteria.getCustomer()).thenReturn(null);
		}
		
		AvailsRunParams runParams = getDefaultRunParams(availsCriteria);

        AvailsCalculationResult availsCalcResult = runAvails(
            runParams,
            rightStrands, 
            Arrays.asList(Seinfeld_SEASON_1_EPISODE_01),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );
       
		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
        Term term;
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;
		
		pmtlIdSets = getMappedPMTLIdSets(
			availsCalcResult.getCalcResults().keySet(),
			Seinfeld_SEASON_1_EPISODE_01,
			ptv,
			usa,
			english
		);
		assertTrue(!pmtlIdSets.isEmpty());
		
		for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			
			// term: year 2020-2022 has exclusive right in
			{
				term = new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31));
				validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
			
			// term: year 2022-2024 rights have expired
			{
				term = new Term(DateTimeUtil.createDate(2022, 1, 1), DateTimeUtil.createDate(2023, 12, 31));
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
			
			// term: year 2024-perpetuity rights have expired
			{
				term = new Term(DateTimeUtil.createDate(2024, 1, 1), DateTimeUtil.createDate(2026, 12, 31));
				validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
