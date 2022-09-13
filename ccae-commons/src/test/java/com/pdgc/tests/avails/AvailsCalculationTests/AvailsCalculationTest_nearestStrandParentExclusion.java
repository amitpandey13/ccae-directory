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
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_nearestStrandParentExclusion extends AvailsCalculationTest {
	@Test
	public void nearestStrandParentExclusionTest() throws FileNotFoundException {

		TestDistributionStrand rightsIn1 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn1.setRightStrandId(1L);
		rightsIn1.setActualPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), ptv, Constants.WORLD, Constants.ALL_LANGUAGES));
		rightsIn1.setPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), ptv, Constants.WORLD, Constants.ALL_LANGUAGES));
		rightsIn1.setCalculationOrder(1);
		rightsIn1.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 1l));

		TestDealStrand rightsOut = new TestDealStrand(simpleEpisodeLicense);
		rightsOut.setRightStrandId(2L);
		rightsOut.setActualPMTL(new PMTL(rightsOut.getPMTL().getProduct(), ptv, mayotte, english));
		rightsOut.setPMTL(new PMTL(rightsOut.getPMTL().getProduct(), ptv, mayotte, english));
		Term rightsOutTerm = new Term(DateTimeUtil.createDate(2016, 1, 1), DateTimeUtil.createDate(2018, 1, 1));
		rightsOut.setTerm(rightsOutTerm);
		rightsOut.setOrigTerm(rightsOutTerm);
		rightsOut.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 2l, "2"));
		rightsOut.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);

		TestRestrictionStrand rightsOut3 = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut3.setRightStrandId(3L);
		rightsOut3.setActualPMTL(new PMTL(rightsOut3.getPMTL().getProduct(), ptv, comoros, english));
		rightsOut3.setPMTL(new PMTL(rightsOut3.getPMTL().getProduct(), ptv, comoros, english));
		Term rightsOut3Term = new Term(Constants.EPOCH, Constants.PERPETUITY);
		rightsOut3.setTerm(rightsOut3Term);
		rightsOut3.setOrigTerm(rightsOut3Term);
		rightsOut3.setCalculationOrder(1);
		rightsOut3.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 3l));

		List<RightStrand> rightStrands = Arrays.asList(rightsIn1, rightsOut, rightsOut3);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1;
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(mayotte))
                    .languages(Collections.singleton(english))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
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
            Arrays.asList(Seinfeld_SEASON_1_EPISODE_01),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );

		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
		Term term;
		TimePeriod timePeriod = TimePeriod.FULL_WEEK;

		pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, ptv, mayotte, english);
		assertTrue(!pmtlIdSets.isEmpty());

		// Mayotte/English results
		for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			{
				term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2015, 12, 31));
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			{
				term = new Term(DateTimeUtil.createDate(2016, 1, 1), DateTimeUtil.createDate(2018, 1, 1));
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			{
				term = new Term(DateTimeUtil.createDate(2018, 1, 2), Constants.PERPETUITY);
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
