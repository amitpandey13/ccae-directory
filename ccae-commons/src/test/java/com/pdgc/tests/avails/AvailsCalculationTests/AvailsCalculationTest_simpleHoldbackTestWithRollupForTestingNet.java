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

import com.google.common.collect.ImmutableSet;
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
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_simpleHoldbackTestWithRollupForTestingNet extends AvailsCalculationTest {

	/**
	 * Simple holdback test.
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void simpleHoldbackTestWithRollupForTestingNet()
			throws FileNotFoundException {
		Product requestedProduct = Seinfeld_SEASON_1_EPISODE_01;

		TestDistributionStrand USEnglishBASCDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		TestDistributionStrand USEnglishPayDistributionRights = new TestDistributionStrand(seriesDistributionRights);

		TestDealStrand USEnglishBASCEpisode1License = new TestDealStrand(simpleEpisodeLicense);
		{
			// distribution rights
			USEnglishBASCDistributionRights.setRightStrandId(4L);
			USEnglishBASCDistributionRights.setActualMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBASCDistributionRights.setMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBASCDistributionRights.setProduct(requestedProduct);
			USEnglishBASCDistributionRights.setActualProduct(requestedProduct);

			USEnglishPayDistributionRights.setRightStrandId(6L);
			USEnglishPayDistributionRights.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPayDistributionRights.setMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPayDistributionRights.setProduct(requestedProduct);
			USEnglishPayDistributionRights.setActualProduct(requestedProduct);

			// licensing
			USEnglishBASCEpisode1License.setRightStrandId(5L);
			USEnglishBASCEpisode1License.setActualMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBASCEpisode1License.setMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBASCEpisode1License.setRightType(TestRightType.HOLDBACK);
		}

		List<RightStrand> rightStrands = Arrays.asList(
	        USEnglishBASCDistributionRights, 
	        USEnglishPayDistributionRights, 
	        USEnglishBASCEpisode1License
	    );

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1;
            Set<CriteriaSource> criteriaSources = Collections.singleton(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(basc))
                    .territories(Collections.singleton(usa))
                    .languages(Collections.singleton(english))
                    .primaryRequests(ImmutableSet.of(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
                            false
                        ),
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.HOLDBACK),
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

		pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), Seinfeld_SEASON_1_EPISODE_01, basc, usa, english);
		assertTrue(!pmtlIdSets.isEmpty());

		// PTV/US/English results
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

			// term: year 2017 - has an exclusive license
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
}
