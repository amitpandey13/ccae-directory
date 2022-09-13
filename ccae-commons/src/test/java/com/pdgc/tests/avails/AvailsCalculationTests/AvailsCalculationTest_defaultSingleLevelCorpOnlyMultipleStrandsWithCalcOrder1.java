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
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_defaultSingleLevelCorpOnlyMultipleStrandsWithCalcOrder1
		extends AvailsCalculationTest {

	/**
	 * Tie breaker test for conflicting rights that share the same calculation
	 * order. For example, Comoros restriction all lang & mayotte distribution
	 * rights french. Two distinct rights, but both have calc order 1. So which one
	 * wins? By default, we take NO over YES for corp availability.
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void defaultSingleLevelCorpOnlyMultipleStrandsWithCalcOrder1() throws FileNotFoundException {

		TestRestrictionStrand rightsOut = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut.setRightStrandId(1L);
		rightsOut.setActualPMTL(new PMTL(rightsOut.getPMTL().getProduct(), basc, comoros, Constants.ALL_LANGUAGES));
		rightsOut.setPMTL(new PMTL(rightsOut.getPMTL().getProduct(), basc, comoros, Constants.ALL_LANGUAGES));
		Term rightsOutTerm = new Term(Constants.EPOCH, Constants.PERPETUITY);
		rightsOut.setTerm(rightsOutTerm);
		rightsOut.setOrigTerm(rightsOutTerm);
		rightsOut.setCalculationOrder(1);
		rightsOut.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 1l));

		TestDistributionStrand rightsIn = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn.setRightStrandId(2L);
		rightsIn.setActualPMTL(new PMTL(rightsIn.getPMTL().getProduct(), basc, mayotte, english));
		rightsIn.setPMTL(new PMTL(rightsIn.getPMTL().getProduct(), basc, mayotte, english));
		Term rightsInTerm = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
		rightsIn.setTerm(rightsInTerm);
		rightsIn.setOrigTerm(rightsInTerm);
		rightsIn.setCalculationOrder(1);
		rightsIn.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 2l));

		List<RightStrand> rightStrands = Arrays.asList(rightsOut, rightsIn);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1; 
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Sets.newHashSet(mayotte, moheli))
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

		// Mayotte/English results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, basc, mayotte, english);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			    term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}

		// Moheli/English results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, basc, moheli, english);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			    term = new Term(Constants.EPOCH, Constants.PERPETUITY);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
