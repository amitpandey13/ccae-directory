package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.time.Duration;
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
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.structures.timeperiod.TimePeriodType;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_basicTimePeriod extends AvailsCalculationTest {

	// Single right strand with a time period on it
	@Test
	public void basicTimePeriodTest() throws FileNotFoundException {
		TimePeriod licenseTimePeriod = new TimePeriodPart(Duration.ofHours(17), Duration.ofHours(22).plusMinutes(30),
				TimePeriodType.DAILY, true, true, true, true, true, false, false).convertToTimePeriod();
		
		TestDealStrand ep2HoldbackWithTimePeriod = new TestDealStrand(simpleEpisodeLicense);
		ep2HoldbackWithTimePeriod.setActualProduct(Seinfeld_SEASON_1_EPISODE_01);
		ep2HoldbackWithTimePeriod.setProduct(Seinfeld_SEASON_1_EPISODE_01);
		ep2HoldbackWithTimePeriod.setRightType(TestRightType.HOLDBACK);
		ep2HoldbackWithTimePeriod.setTimePeriod(licenseTimePeriod);

		List<RightStrand> rightStrands = Arrays.asList(seriesDistributionRights, ep2HoldbackWithTimePeriod);

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
		TimePeriod timePeriod;
		
		TimePeriod timePeriodWithCarveOut = new TimePeriod(licenseTimePeriod);
        TimePeriod timePeriodWithoutCarveOut = TimePeriod.subtractPeriods(TimePeriod.FULL_WEEK, timePeriodWithCarveOut);
		
		// Episode 01
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, ptv, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					timePeriod = TimePeriod.FULL_WEEK;
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2017 - has the time period-limited holdback against licenses
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));					
					
					timePeriod = timePeriodWithCarveOut;
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);

					timePeriod = timePeriodWithoutCarveOut;
			        validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					timePeriod = TimePeriod.FULL_WEEK;
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// other episodes
		{
			for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
				if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
					continue;
				}

				pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), episode, ptv, usa, english);
				assertTrue(!pmtlIdSets.isEmpty());
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
					// term: epoch to 2017 - has nothing but distribution rights
					{
						term = new Term(Constants.EPOCH, Constants.PERPETUITY);
						timePeriod = TimePeriod.FULL_WEEK;
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
