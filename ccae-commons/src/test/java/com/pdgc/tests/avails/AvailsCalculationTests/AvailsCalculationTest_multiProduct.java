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
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_multiProduct extends AvailsCalculationTest {

	/*
	 * Test to see if a single node can handle unrelated products in the same run.
	 * Let there be a non-exclusive license on ep 1 for Seinfeld, a holdback for the
	 * feature, and no licenses for Good Wife
	 */
	@Test
	public void multiProductTest() throws FileNotFoundException {
		TestDistributionStrand featureDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		featureDistributionRights.setActualProduct(FEATURE);
		featureDistributionRights.setProduct(FEATURE);
		PMTL distributionPMTL = new PMTL(FEATURE, featureDistributionRights.getPMTL().getMedia(),
				featureDistributionRights.getPMTL().getTerritory(), featureDistributionRights.getPMTL().getLanguage());
		featureDistributionRights.setPMTL(distributionPMTL);

		TestDealStrand featureLicense = new TestDealStrand(simpleEpisodeLicense);
		featureLicense.setActualProduct(FEATURE);
		featureLicense.setProduct(FEATURE);
		PMTL holdbackPMTL = new PMTL(FEATURE, ptv, usa, english);
		featureLicense.setPMTL(holdbackPMTL);
		featureLicense.setRightType(TestRightType.HOLDBACK);
		featureLicense.setTerm(new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2015, 12, 31)));
		featureLicense.setOrigTerm(new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2015, 12, 31)));

		TestDistributionStrand GoodWifeDistributionRights = new TestDistributionStrand(
				seriesDistributionRights);
		GoodWifeDistributionRights.setActualProduct(GOODWIFE_SEASON_1);
		PMTL gwPMTL = new PMTL(GOODWIFE_SEASON_1, seriesDistributionRights.getPMTL().getMedia(),
				seriesDistributionRights.getPMTL().getTerritory(), seriesDistributionRights.getPMTL().getLanguage());
		GoodWifeDistributionRights.setPMTL(gwPMTL);

		List<RightStrand> rightStrands = Arrays.asList(
	        seriesDistributionRights, 
	        simpleEpisodeLicense, 
	        featureDistributionRights, 
	        featureLicense,
			GoodWifeDistributionRights
		);

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
            Arrays.asList(FEATURE, Seinfeld_SEASON_1, GOODWIFE_SEASON_1),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );

		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
		Term term;
		TimePeriod timePeriod = TimePeriod.FULL_WEEK;

		// Seinfeld results
		{
			// Episode 01
			{
				pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
						Seinfeld_SEASON_1_EPISODE_01, ptv, usa, english);
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

					// term: year 2017 - has the license
					{
						term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
						validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
		                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
		                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
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

			// Episode 02
			{
				pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(),
						Seinfeld_SEASON_1_EPISODE_02, ptv, usa, english);
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
		}

		// Feature results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to 2015 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2014, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2015 - has the holdback
				{
					term = new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2015, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2016 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2016, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}

		}

		// Good Wife results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), GOODWIFE_SEASON_1_EPISODE_01,
					ptv, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to perpetuity
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
