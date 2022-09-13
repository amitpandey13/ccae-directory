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
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_requestLicensesWhenOnlyPreliminaryNonExlusiveLicenseExists extends AvailsCalculationTest {
	//
	/* Basic Test For preliminary Non_Exclusive License
	 * request Licenses when Only Preliminary Non Exclusive exists
	 *
	 *
	 */
	@Test
	public void requestLicensesWhenOnlyPreliminaryNonExlusiveLicenseExists() throws FileNotFoundException {
		
		TestDealStrand rightsIn = new TestDealStrand(simpleEpisodeLicense);
		rightsIn.setTerm(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31)));
		rightsIn.setOrigTerm(new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31)));
		rightsIn.setRightType(TestRightType.PRELIM_NONEXCLUSIVE_LICENSE);

		List<RightStrand> rightStrands = Arrays.asList(rightsIn);

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
                DateTimeUtil.createDate(2021, 12, 31)));
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
		TimePeriod timePeriod;

		//All episodes 
		{
			// return the pmtl sets that contain all the criteria leaf pmtl
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			term = new Term(DateTimeUtil.createDate(2020, 1, 1), DateTimeUtil.createDate(2021, 12, 31));
			timePeriod = TimePeriod.FULL_WEEK;

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                validatePMTLTR(Availability.CONDITIONAL_DEAL, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.CONDITIONAL_DEAL, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
