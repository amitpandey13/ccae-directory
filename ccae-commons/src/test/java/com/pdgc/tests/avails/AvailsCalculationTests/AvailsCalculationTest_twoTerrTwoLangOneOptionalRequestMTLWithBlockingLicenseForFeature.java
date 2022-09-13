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
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsCalculationTest_twoTerrTwoLangOneOptionalRequestMTLWithBlockingLicenseForFeature
		extends AvailsCalculationTest {

	/**
	 * Expected Result: 2 psuedo tl should be created - one for Mex/Eng (optional
	 * english), one for Mex/Sp, one for US/Eng/Sp. Mexico is split into two
	 * RequestStrands because of the optional mtl on english language. Results
	 * should not be different than twoTerrTwoLangOneOptionalRequestMTLTest that has
	 * no licenses.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void twoTerrTwoLangOneOptionalRequestMTLWithBlockingLicenseForFeatureTest()
			throws FileNotFoundException {
		List<RightStrand> rightStrands = new ArrayList<>();
		TestDistributionStrand WWAllMediaAllLangDistributionRights = new TestDistributionStrand(seriesDistributionRights);
		WWAllMediaAllLangDistributionRights.setPMTL(new PMTL(FEATURE, allMedia, Constants.WORLD, Constants.ALL_LANGUAGES));
		WWAllMediaAllLangDistributionRights.setActualPMTL(new PMTL(FEATURE, allMedia, Constants.WORLD, Constants.ALL_LANGUAGES));
		rightStrands.add(WWAllMediaAllLangDistributionRights);

		TestDealStrand tempStrand;
		{
			tempStrand = new TestDealStrand(simpleEpisodeLicense);
			tempStrand.setRightStrandId(tempStrand.getRightStrandId() + 1);
			tempStrand.setMTL(new MTL(ptv, usa, english));
			tempStrand.setActualMTL(new MTL(ptv, usa, english));
			tempStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 1L, "1"));
			tempStrand.setActualProduct(FEATURE);
			tempStrand.setProduct(FEATURE);
			rightStrands.add(tempStrand);
		}

		AvailsQuery availsCriteria = mock(AvailsQuery.class);
        {
            int keyId = 1; 
            Set<CriteriaSource> criteriaSources = Sets.newHashSet(
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(mexico))
                    .languages(Sets.newHashSet(english, spanish))
                    .primaryRequests(Collections.singleton(
                        new OptionalWrapper<>(
                            new RightRequest(TestRightType.EXCLUSIVE_LICENSE),
                            false
                        )))
                    .secondaryPreRequests(new HashSet<>())
                    .secondaryPostRequests(new HashSet<>())
                    .build(),
                CriteriaSource.builder()
                    .key(keyId++)
                    .medias(Collections.singleton(ptv))
                    .territories(Collections.singleton(usa))
                    .languages(Sets.newHashSet(english, spanish, french))
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
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, usa, english);
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

				// term: year 2017 - has an exclusive license in basc and a nonexcl
				// license in ptv. basc should be unavailable and ptv available for the
				// nonexcl request, and both basc and ptv should be unavailable for
				// both Excl and HB.
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: 2018 to perp - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 01, 01), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.YES, availsCalcResult, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
                    validatePMTLTR(Availability.UNSET, availsCalcResult, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}

		// PTV/Mexico/English results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, mexico, english);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to perp - has nothing but distribution rights
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

		// PTV/Mexico/Spanish results
		{
			pmtlIdSets = getMappedPMTLIdSets(availsCalcResult.getCalcResults().keySet(), FEATURE, ptv, mexico, spanish);
			assertTrue(!pmtlIdSets.isEmpty());

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSets) {
				// term: epoch to perp - has nothing but distribution rights
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
