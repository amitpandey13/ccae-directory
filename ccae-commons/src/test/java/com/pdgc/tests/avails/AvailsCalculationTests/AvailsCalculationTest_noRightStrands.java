package com.pdgc.tests.avails.AvailsCalculationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class AvailsCalculationTest_noRightStrands extends AvailsCalculationTest {
	// test for when no right strands exist for the PTML
	@Test
	public void noRightStrandsTest() throws FileNotFoundException {
			
		List<RightStrand> rightStrands = new ArrayList<>();
        
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
        
        //All episodes 
		{
			pmtlIdSets = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1,
				ptv,
				usa,
				english
			);
			assertTrue(!pmtlIdSets.isEmpty());
			
			term = new Term(Constants.EPOCH, Constants.PERPETUITY);
			timePeriod = TimePeriod.FULL_WEEK;

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
			    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
			    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
			    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
 			    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
			}
		}
	}
}
