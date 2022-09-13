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
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestSalesPlanStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_salesPlanBasicSalesBlock extends AvailsCalculationTest {
	/*
	 * Sales plan window on BASC is parent to sales block on PTV. BASC returns
	 * as available and PTV as unavailable on the same time period.
	 */
	@Test
	public void salesPlanBasicSalesBlockTest() throws FileNotFoundException {
		
		TestSalesPlanStrand salesPlanWindow = new TestSalesPlanStrand(simpleEpisodeSalesPlan);
		salesPlanWindow.setRightType(TestRightType.SALES_PLAN_WINDOW);
		salesPlanWindow.setMedia(basc);

		TestSalesPlanStrand salesPlanBlock = new TestSalesPlanStrand(simpleEpisodeSalesPlan);
		salesPlanBlock.setRightType(TestRightType.SALES_PLAN_BLOCK);
		//salesPlanBlock.setParentSource(salesWindows.getRightStrandId());

		TestDistributionStrand allMediaDistrRights = new TestDistributionStrand(seriesDistributionRights);
		allMediaDistrRights.setPMTL(new PMTL(Seinfeld_SERIES, allMedia, worldall.getTerritory(), worldall.getLanguage()));
		allMediaDistrRights.setActualPMTL(new PMTL(Seinfeld_SERIES, allMedia, worldall.getTerritory(), worldall.getLanguage()));

		List<RightStrand> rightStrands = Arrays.asList(
			allMediaDistrRights, 
			salesPlanBlock, 
			salesPlanWindow
		);

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1;
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Sets.newHashSet(ptv, basc))
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
        TimePeriod timePeriod = TimePeriod.FULL_WEEK;
		
		//Episode 1
		{
			//PTV
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
					
					// term: year 2017 - has sales block
					{
						term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
						validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
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
			
			//BASC
			{
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					Seinfeld_SEASON_1_EPISODE_01,
					basc,
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
					
					// term: year 2017 - has sales plan
					{
						term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
						validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
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
		}
		
		for (Product episode : productHierarchy.getLeaves(Seinfeld_SEASON_1)) {
			if (episode.equals(Seinfeld_SEASON_1_EPISODE_01)) {
				continue;
			}
			
			//PTV
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
					// term: epoch to 2017 - has nothing but distribution rights
					{
						term = new Term(Constants.EPOCH, Constants.PERPETUITY);
						validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
					}
				}
			}
			
			//BASC
			{
				pmtlIdSets = getMappedPMTLIdSets(
					availsCalcResult.getCalcResults().keySet(),
					episode,
					basc,
					usa,
					english
				);
				assertTrue(!pmtlIdSets.isEmpty());
				
				for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {		
					// term: epoch to perpetuity - has nothing but distribution rights
					{
						term = new Term(Constants.EPOCH, Constants.PERPETUITY);
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
