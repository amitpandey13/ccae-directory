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

public class AvailsCalculationTest_basicRightsInRightsOutRightsInCorporateAvail extends AvailsCalculationTest {

	/**
	 * Rights In Rights Out Rights In Corporate Avail Test
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void basicRightsInRightsOutRightsInCorporateAvailTest() throws FileNotFoundException {

		TestDistributionStrand rightsIn1 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn1.setActualPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, mayotte, french));
		rightsIn1.setPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, mayotte, french));
		Term rightsIn1Term = new Term(Constants.EPOCH, Constants.PERPETUITY);
		rightsIn1.setTerm(rightsIn1Term);
		rightsIn1.setOrigTerm(rightsIn1Term);
		rightsIn1.setCalculationOrder(1);
		rightsIn1.setRightStrandId(1l);

		TestRestrictionStrand rightsOut = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut.setActualPMTL(new PMTL(rightsOut.getPMTL().getProduct(), basc, mayotte, french));
		rightsOut.setPMTL(new PMTL(rightsOut.getPMTL().getProduct(), basc, mayotte, french));
		Term restrictTerm = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2018, 12, 31));
		rightsOut.setTerm(restrictTerm);
		rightsOut.setOrigTerm(restrictTerm);
		rightsOut.setCalculationOrder(2);
		rightsOut.setRightStrandId(2l);

		TestDistributionStrand rightsIn2 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn2.setActualPMTL(new PMTL(rightsIn2.getPMTL().getProduct(), basc, mayotte, french));
		rightsIn2.setPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, mayotte, french));
		Term rightsIn2Term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
		rightsIn2.setTerm(rightsIn2Term);
		rightsIn2.setOrigTerm(rightsIn2Term);
		rightsIn2.setCalculationOrder(3);
		rightsIn2.setRightStrandId(3l);

		List<RightStrand> rightStrands = Arrays.asList(rightsIn1, rightsOut, rightsIn2);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
		{
		    int keyId = 1; 
		    Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(mayotte))
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

		pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, basc, mayotte, french);
		assertTrue(!pmtlIdSets.isEmpty());

		// Mayotte/French results
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

			// term: year 2017 - 2018 has restrictions
			{
				term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			// term: year 2018 - has rights in then rights out then rights in rights
			{
				term = new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2018, 12, 31));
				validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}

			// term: year 2019 to perp - nothing but distribution rights
			{
				term = new Term(DateTimeUtil.createDate(2019, 1, 1), Constants.PERPETUITY);
				validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}

}
