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
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_ignoredRestrictionsOnMultiLevelCorpOnly extends AvailsCalculationTest {

	/**
	 * Ignore restrictions if there are any exclusive distribution rights that have
	 * a different business unit For example, if there are 4 corp rights: 1.
	 * Distribution right - BusUnit: USA, Comoros 2. Distribution right - BusUnit:
	 * LatAm, Comoros 3. Restriction - BusUnit: USA, Comoros 4. Restriction -
	 * BusUnit: LatAm, Comoros Ignore the last 2 restrictions and only consider the
	 * distribution rights
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void ignoredRestrictionsOnMultiLevelCorpOnly() throws FileNotFoundException {

		TestDistributionStrand rightsIn1 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn1.setRightStrandId(1L);
		rightsIn1.setActualPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, comoros, english));
		rightsIn1.setPMTL(new PMTL(rightsIn1.getPMTL().getProduct(), basc, comoros, english));
		Term rightsIn1Term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
		rightsIn1.setTerm(rightsIn1Term);
		rightsIn1.setOrigTerm(rightsIn1Term);
		rightsIn1.setCalculationOrder(1);
		rightsIn1.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 1l));

		TestDistributionStrand rightsIn2 = new TestDistributionStrand(simpleEpisodeDistributionRights);
		rightsIn2.setRightStrandId(2L);
		rightsIn2.setActualPMTL(new PMTL(rightsIn2.getPMTL().getProduct(), basc, comoros, english));
		rightsIn2.setPMTL(new PMTL(rightsIn2.getPMTL().getProduct(), basc, comoros, english));
		Term rightsIn2Term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
		rightsIn2.setTerm(rightsIn2Term);
		rightsIn2.setOrigTerm(rightsIn2Term);
		rightsIn2.setCalculationOrder(1);
		rightsIn2.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 2l));

		TestRestrictionStrand rightsOut1 = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut1.setRightStrandId(3L);
		rightsOut1.setActualPMTL(new PMTL(rightsOut1.getPMTL().getProduct(), basc, comoros, english));
		rightsOut1.setPMTL(new PMTL(rightsOut1.getPMTL().getProduct(), basc, comoros, english));
		Term rightsOut1Term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
		rightsOut1.setTerm(rightsOut1Term);
		rightsOut1.setOrigTerm(rightsOut1Term);
		rightsOut1.setCalculationOrder(2);
		rightsOut1.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 3l));

		TestRestrictionStrand rightsOut2 = new TestRestrictionStrand(simpleEpisodeRestriction);
		rightsOut2.setRightStrandId(4L);
		rightsOut2.setActualPMTL(new PMTL(rightsOut2.getPMTL().getProduct(), basc, comoros, english));
		rightsOut2.setPMTL(new PMTL(rightsOut2.getPMTL().getProduct(), basc, comoros, english));
		Term rightsOut2Term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
		rightsOut2.setTerm(rightsOut2Term);
		rightsOut2.setOrigTerm(rightsOut2Term);
		rightsOut2.setCalculationOrder(2);
		rightsOut2.setRightSource(new TestCorpSource(TestRightSourceType.RIGHTSIN, 4l));

		List<RightStrand> rightStrands = Arrays.asList(rightsIn1, rightsIn2, rightsOut1, rightsOut2);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1; 
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(comoros))
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

		pmtlIdSets = getMappedPMTLIdSets(
			availsCalcResult.getCalcResults().keySet(), 
			Seinfeld_SEASON_1_EPISODE_01, 
			basc,
			comoros, 
			english,
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy
		);
		assertTrue(!pmtlIdSets.isEmpty());

		// Mayotte/English results
		for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			{
				term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
				validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
			{
				term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
				validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
			{
				term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
				validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
