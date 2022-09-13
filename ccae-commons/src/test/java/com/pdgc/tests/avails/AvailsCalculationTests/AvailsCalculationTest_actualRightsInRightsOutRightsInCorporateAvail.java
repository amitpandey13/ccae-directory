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
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_actualRightsInRightsOutRightsInCorporateAvail extends AvailsCalculationTest {

	/**
	 * Rights In Rights Out Rights In Corporate Avail Test Same one as the one
	 * presented in Joshi's email
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void actualRightsInRightsOutRightsInCorporateAvailTest() throws FileNotFoundException {

		TestRestrictionStrand rightsOut1 = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut1.setRightStrandId(2L);
		rightsOut1.setActualPMTL(new PMTL(rightsOut1.getPMTL().getProduct(), basc, comoros, Constants.ALL_LANGUAGES));
		rightsOut1.setPMTL(new PMTL(rightsOut1.getPMTL().getProduct(), basc, comoros, Constants.ALL_LANGUAGES));
		Term rightsOut1Term = new Term(Constants.EPOCH, Constants.PERPETUITY);
		rightsOut1.setTerm(rightsOut1Term);
		rightsOut1.setOrigTerm(rightsOut1Term);
		rightsOut1.setCalculationOrder(1);

		TestDistributionStrand rightsIn1 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn1.setRightStrandId(1L);
		rightsIn1.setActualPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, comoros, french));
		rightsIn1.setPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, comoros, french));
		Term rightsIn1Term = new Term(DateTimeUtil.createDate(2017, 1, 1), Constants.PERPETUITY);
		rightsIn1.setTerm(rightsIn1Term);
		rightsIn1.setOrigTerm(rightsIn1Term);
		rightsIn1.setCalculationOrder(2);

		TestRestrictionStrand rightsOut2 = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut2.setRightStrandId(3L);
		rightsOut2.setActualPMTL(new PMTL(rightsOut2.getPMTL().getProduct(), basc, mayotte, Constants.ALL_LANGUAGES));
		rightsOut2.setPMTL(new PMTL(rightsOut2.getPMTL().getProduct(), basc, mayotte, Constants.ALL_LANGUAGES));
		Term rightsOut2Term = new Term(Constants.EPOCH, Constants.PERPETUITY);
		rightsOut2.setTerm(rightsOut2Term);
		rightsOut2.setOrigTerm(rightsOut2Term);
		rightsOut2.setCalculationOrder(1);

		TestDistributionStrand rightsIn2 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn2.setRightStrandId(4L);
		rightsIn2.setActualPMTL(new PMTL(rightsIn2.getPMTL().getProduct(), basc, mayotte, english));
		rightsIn2.setPMTL(new PMTL(rightsIn2.getPMTL().getProduct(), basc, mayotte, english));
		Term rightsIn2Term = new Term(DateTimeUtil.createDate(2017, 1, 1), Constants.PERPETUITY);
		rightsIn2.setTerm(rightsIn2Term);
		rightsIn2.setOrigTerm(rightsIn2Term);
		rightsIn2.setCalculationOrder(2);

		List<RightStrand> rightStrands = Arrays.asList(rightsOut1, rightsOut2, rightsIn1, rightsIn2);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
		{
		    int keyId = 1; 
			Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(comoros))
                    .languages(Collections.singleton(french))
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

		// Mayotte/French results
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(), 
				Seinfeld_SEASON_1_EPISODE_01,
				basc, 
				mayotte, 
				french,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// TODO: check results
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// Moheli/French results
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(), 
				Seinfeld_SEASON_1_EPISODE_01,
				basc, 
				moheli, 
				french,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), Constants.PERPETUITY);
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
