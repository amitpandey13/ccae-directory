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

import com.google.common.base.Equivalence;
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
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.TestsHelper;

public class AvailsCalculationTest_multiParentProductMultiSeasonLicense extends AvailsCalculationTest {

	/*
	 * Tests products with multiple parents i.e. episodes that belong to both a
	 * "broadcast season" and a "sales season". Seinfeld Broadcast Season 1 contains
	 * ep 101, 102 Seinfeld Broadcast Season 2 contains ep 201, 202 Seinfeld Sales
	 * Season 1+2 contains 101, 102, 201, 202 License is taken out on Sales Season
	 * 1+2. When searching for availability on Broadcast Season 1 and Broadcast
	 * Season 2, all are unavailable.
	 * 
	 * This unit test DOES NOT prove the Season 1 and Season 2 unavailability, as
	 * AvailsCalculationTest only provides availability at the episodic leaf level.
	 * However, AvailsRollupResult provides the ultimate unavailability for each
	 * season.
	 */
	@Test
	public void multiParentProductMultiSeasonLicenseTest()
			throws FileNotFoundException {

		Product Seinfeld_SEASON_1_AND_2 = TestsHelper.createSeason("Seinfeld - SEASON 01 AND 02");
		Product Seinfeld_SEASON_2 = TestsHelper.createSeason("Seinfeld - SEASON 02");
		Product Seinfeld_SEASON_2_EPISODE_01 = TestsHelper.createEpisode("Seinfeld 201");
		Product Seinfeld_SEASON_2_EPISODE_02 = TestsHelper.createEpisode("Seinfeld 202");

		HierarchyMapEditor<Product> productHierarchy = new HierarchyMapEditor<>();
		{
			productHierarchy.addElement(Seinfeld_SERIES);

			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_02);

			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_2);
			productHierarchy.addChild(Seinfeld_SEASON_2, Seinfeld_SEASON_2_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_2, Seinfeld_SEASON_2_EPISODE_02);

			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_AND_2);
			productHierarchy.addChild(Seinfeld_SEASON_1_AND_2, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1_AND_2, Seinfeld_SEASON_1_EPISODE_02);
			productHierarchy.addChild(Seinfeld_SEASON_1_AND_2, Seinfeld_SEASON_2_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1_AND_2, Seinfeld_SEASON_2_EPISODE_02);
		}

		TestDistributionStrand rightsIn = new TestDistributionStrand(seriesDistributionRights);
		rightsIn.setTerrLang(new TerrLang(usa, worldall.getLanguage()));

		TestDealStrand licenseSeason1And2 = new TestDealStrand(				
			2L, 
			new PMTL(Seinfeld_SEASON_1_AND_2, USEnglishPTV),
			new TermPeriod(
    			new Term(DateTimeUtil.createDate(2017, 1, 1),  DateTimeUtil.createDate(2017, 12, 31)),
    			TimePeriod.FULL_WEEK
			),
			drs570796L,
			TestRightType.EXCLUSIVE_LICENSE, 
			new PMTL(Seinfeld_SEASON_1_AND_2, USEnglishPTV),
			new Term(DateTimeUtil.createDate(2017, 1, 1),  DateTimeUtil.createDate(2017, 12, 31)),
			true,
			null,
			null
		);

		List<RightStrand> rightStrands = Arrays.asList(rightsIn, licenseSeason1And2);

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

		AvailsRunParams runParams = AvailsRunParams.baseBuilder()
            .availsCriteria(availsCriteria)
            .rightStrandEquivalence(Equivalence.identity())
            .rightTypeCorpAvailMap(rightTypeCorpAvailMap)
            .additionalRequests(requestedLicenses)
            .productHierarchy(productHierarchy)
            .mediaHierarchy(mediaHierarchy)
            .territoryHierarchy(territoryHierarchy)
            .languageHierarchy(languageHierarchy)
            .productDictionary(productDictionary::get)
            .mediaDictionary(mediaDictionary::get)
            .territoryDictionary(territoryDictionary::get)
            .languageDictionary(languageDictionary::get)
            .build();

        AvailsCalculationResult availsCalcResult = runAvails(
            runParams,
            rightStrands, 
            Arrays.asList(Seinfeld_SEASON_1),
            rightTypeImpactMatrix, 
            corpAvailabilityCalculator, 
            rightTypeCarveOutActionMap
        );

		AvailsCalculationResult availsCalcResult2 = runAvails(
		    runParams,
			rightStrands, 
			Arrays.asList(Seinfeld_SEASON_2),
			rightTypeImpactMatrix,
			corpAvailabilityCalculator,
			rightTypeCarveOutActionMap
		);

		Set<Set<LeafPMTLIdSet>> pmtlIdSetsSeason_1_EP_1;
		Set<Set<LeafPMTLIdSet>> pmtlIdSetsSeason_2_EP_1;
		Term term;
		TimePeriod timePeriod;

		// All episodes
		{
			pmtlIdSetsSeason_1_EP_1 = getMappedPMTLIdSets(
				availsCalcResult.getCalcResults().keySet(),
				Seinfeld_SEASON_1_EPISODE_01, 
				ptv, 
				usa, 
				english,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			);

			pmtlIdSetsSeason_2_EP_1 = getMappedPMTLIdSets(
				availsCalcResult2.getCalcResults().keySet(),
				Seinfeld_SEASON_2_EPISODE_01, 
				ptv, 
				usa, 
				english,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			);
			
			assertTrue(!pmtlIdSetsSeason_1_EP_1.isEmpty());
			assertTrue(!pmtlIdSetsSeason_2_EP_1.isEmpty());
			timePeriod = TimePeriod.FULL_WEEK;

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_1_EP_1) {
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
	                validatePMTLTR(Availability.NO, availsCalcResult, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
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

			for (Set<LeafPMTLIdSet> pmtlGroup : pmtlIdSetsSeason_2_EP_1) {
				// term: epoch to 2017 - has nothing but distribution rights
				{
					term = new Term(Constants.EPOCH, DateTimeUtil.createDate(2016, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, holdbackRequest, term, timePeriod);
				}
				// term: year 2017 - has the license
				{
					term = new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31));
					validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult2, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult2, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.NO, availsCalcResult2, pmtlGroup, holdbackRequest, term, timePeriod);
				}

				// term: year 2018 to perpetuity - has nothing but distribution rights
				{
					term = new Term(DateTimeUtil.createDate(2018, 1, 1), Constants.PERPETUITY);
					validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, exclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.YES, availsCalcResult2, pmtlGroup, nonExclusiveCorpAvailRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, exclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, nonExclusiveLicenseRequest, term, timePeriod);
	                validatePMTLTR(Availability.UNSET, availsCalcResult2, pmtlGroup, holdbackRequest, term, timePeriod);
				}
			}
		}
	}
}
