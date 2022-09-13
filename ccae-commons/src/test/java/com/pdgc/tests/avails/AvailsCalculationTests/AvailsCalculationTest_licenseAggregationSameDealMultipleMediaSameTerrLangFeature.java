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
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_licenseAggregationSameDealMultipleMediaSameTerrLangFeature
		extends AvailsCalculationTest {

	/**
	 * This test should fail if we are grouping by RightSource in explodeRights but
	 * pass if we are grouping by RightSource, product and media.
	 * 
	 * @throws IELeftSideAvailabilityIsMissingException
	 * @throws FileNotFoundException
	 */
	@Test
	public void licenseAggregationSameDealMultipleMediaSameTerrLangFeatureTest()
			throws FileNotFoundException {
		TestDistributionStrand USAllLangAllMediaDistributionRights = new TestDistributionStrand(
				seriesDistributionRights);

		// All license strands need to have the same right source.
		TestDealStrand USEnglishBascEpisode1License = new TestDealStrand(simpleEpisodeLicense);
		TestDealStrand USEnglishPtvEpisode1License = new TestDealStrand(simpleEpisodeLicense);
		{

			USAllLangAllMediaDistributionRights.setRightStrandId(1L);
			USAllLangAllMediaDistributionRights.setActualMTL(new MTL(allMedia, new TerrLang(usa, Constants.ALL_LANGUAGES)));
			USAllLangAllMediaDistributionRights.setMTL(new MTL(allMedia, new TerrLang(usa, Constants.ALL_LANGUAGES)));
			USAllLangAllMediaDistributionRights.setProduct(FEATURE);
			USAllLangAllMediaDistributionRights.setActualProduct(FEATURE);

			USEnglishBascEpisode1License.setRightStrandId(2L);
			USEnglishBascEpisode1License.setActualMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBascEpisode1License.setMTL(new MTL(basc, new TerrLang(usa, english)));
			USEnglishBascEpisode1License.setProduct(FEATURE);
			USEnglishBascEpisode1License.setActualProduct(FEATURE);
			USEnglishBascEpisode1License.setRightType(TestRightType.EXCLUSIVE_LICENSE);

			USEnglishPtvEpisode1License.setRightStrandId(4L);
			USEnglishPtvEpisode1License.setActualMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPtvEpisode1License.setMTL(new MTL(ptv, new TerrLang(usa, english)));
			USEnglishPtvEpisode1License.setProduct(FEATURE);
			USEnglishPtvEpisode1License.setActualProduct(FEATURE);

		}

		List<RightStrand> rightStrands = Arrays.asList(
			USAllLangAllMediaDistributionRights, 
			USEnglishPtvEpisode1License, 
			USEnglishBascEpisode1License
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
                            new RightRequest(TestRightType.NONEXCLUSIVE_LICENSE),
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
            Arrays.asList(FEATURE),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );

		Set<Set<LeafPMTLIdSet>> pmtlIdSets;
		Term term;
		TimePeriod timePeriod = TimePeriod.FULL_WEEK;

		// PTV/US/English results
		{
			// term: epoch to 2017 - has nothing but distribution rights

			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, basc, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}

			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// term: year 2017 - has an exclusive license in basc and a nonexcl
		// license in ptv. basc should be unavailable and ptv available for
		// the nonexcl request, and both basc and ptv should be unavailable for
		// both Excl and HB.
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, basc, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}

			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, usa, english);
			assertTrue(!pmtlIdSets.isEmpty());
			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
}
