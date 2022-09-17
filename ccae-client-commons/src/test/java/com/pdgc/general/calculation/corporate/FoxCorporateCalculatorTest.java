package com.pdgc.general.calculation.corporate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.impl.FoxCorporateSource;
import com.pdgc.general.structures.rightstrand.impl.FoxDistributionRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.TestDataGenerator;

/**
 * Tests corporate availability results
 *
 * @author Clara Hong
 */
class FoxCorporateCalculatorTest {

    @BeforeAll
    private static void setup() {
        Constants.instantiateConstants();
    }

    @Test
    void givenNoDistributionRights_whenCalculateFinalAvailabilityForExclusivity_expectAvailable() {
        // Arrange
        TestDataGenerator data = new TestDataGenerator();

        RightType corpType = RightType.builder()
                .rightTypeId(Constants.EXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID)
                .build(); 
        
        CorporateCalculatorParams corpCalcParams = new CorporateCalculatorParams();
        corpCalcParams.requestedPMTL = new PMTL(data.getFeatureProduct(), data.getBasicMedia(), data.getUSA(), data.getEnglish());
        corpCalcParams.rightStrands = Collections.emptySet();
        CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
        calcRequest.requestedRightType = corpType;
        calcRequest.requestedTerm = data.getTerm();
        calcRequest.requestedTimePeriod = TimePeriod.FULL_WEEK;
        corpCalcParams.calcRequests = Collections.singleton(calcRequest);

        NearestStrandsCalculator nearestStrandsCalculator = new NearestStrandsCalculator(
            new HashMap<>(),
            data.getMediaHierarchy(),
            data.getTerritoryHierarchy()
        );
        
        CorporateCalculator corpAvailCalculator = new FoxCorporateCalculator(
            data.getRightTypeImpactMatrix(),
            nearestStrandsCalculator,
            false,
            false
        );
        
        //Check the avails answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForAvails(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
        
        //Check the conflict answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForConflictCheck(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
    }

    @Test
    void givenNoDistributionRights_whenCalculateFinalAvailabilityForExhibition_expectUnavailable() {
        // Arrange
        TestDataGenerator data = new TestDataGenerator();

        RightType corpType = RightType.builder()
                .rightTypeId(Constants.NONEXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID)
                .build(); 
        
        CorporateCalculatorParams corpCalcParams = new CorporateCalculatorParams();
        corpCalcParams.requestedPMTL = new PMTL(data.getFeatureProduct(), data.getBasicMedia(), data.getUSA(), data.getEnglish());
        corpCalcParams.rightStrands = Collections.emptySet();    // Distribution rights
        CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
        calcRequest.requestedRightType = corpType;
        calcRequest.requestedTerm = data.getTerm();
        calcRequest.requestedTimePeriod = TimePeriod.FULL_WEEK;
        corpCalcParams.calcRequests = Collections.singleton(calcRequest);
        
        NearestStrandsCalculator nearestStrandsCalculator = new NearestStrandsCalculator(
            new HashMap<>(),
            data.getMediaHierarchy(),
            data.getTerritoryHierarchy()
        );
        
        CorporateCalculator corpAvailCalculator = new FoxCorporateCalculator(
            data.getRightTypeImpactMatrix(),
            nearestStrandsCalculator,
            false,
            false
        );

        //Check the avails answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForAvails(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.UNSET, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
        
        //Check the conflict answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForConflictCheck(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.UNSET, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
    }

    @Test
    void givenDistributionRights_whenCalculateFinalAvailabilityForExclusivity_expectAvailable() {
        // Arrange
        TestDataGenerator data = new TestDataGenerator();

        RightType corpType = RightType.builder()
                .rightTypeId(Constants.EXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID)
                .build();
        
        CorporateCalculatorParams corpCalcParams = new CorporateCalculatorParams();
        corpCalcParams.requestedPMTL = new PMTL(data.getFeatureProduct(), data.getBasicMedia(), data.getUSA(), data.getEnglish());
        corpCalcParams.rightStrands = Sets.newHashSet(new FoxDistributionRightStrand(
            1,
            corpCalcParams.requestedPMTL,
            data.getTerm(),
            new FoxCorporateSource(
                FoxRightSourceType.RIGHTSIN,
                100L,
                new FoxBusinessUnit(1L, "", "")
            ),
            TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE,
            corpCalcParams.requestedPMTL,
            data.getTerm(),
            "",
            true,
            1L,
            1L
        ));
        CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
        calcRequest.requestedRightType = corpType;
        calcRequest.requestedTerm = data.getTerm();
        calcRequest.requestedTimePeriod = TimePeriod.FULL_WEEK;
        corpCalcParams.calcRequests = Collections.singleton(calcRequest);
        
        NearestStrandsCalculator nearestStrandsCalculator = new NearestStrandsCalculator(
            new HashMap<>(),
            data.getMediaHierarchy(),
            data.getTerritoryHierarchy()
        );
        
        CorporateCalculator corpAvailCalculator = new FoxCorporateCalculator(
            data.getRightTypeImpactMatrix(),
            nearestStrandsCalculator,
            false,
            false
        );

        //Check the avails answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForAvails(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
        
        //Check the conflict answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForConflictCheck(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
    }

    @Test
    void givenDistributionRights_whenCalculateFinalAvailabilityForExhibition_expectAvailable() {
        // Arrange
        TestDataGenerator data = new TestDataGenerator();

        RightType corpType = RightType.builder()
                .rightTypeId(Constants.NONEXCLUSIVE_CORP_AVAIL_RIGHT_TYPE_ID)
                .build();
        
        CorporateCalculatorParams corpCalcParams = new CorporateCalculatorParams();
        corpCalcParams.requestedPMTL = new PMTL(data.getFeatureProduct(), data.getBasicMedia(), data.getUSA(), data.getEnglish());
        corpCalcParams.rightStrands = Sets.newHashSet(new FoxDistributionRightStrand(
            1,
            corpCalcParams.requestedPMTL,
            data.getTerm(),
            new FoxCorporateSource(
                FoxRightSourceType.RIGHTSIN,
                100L,
                new FoxBusinessUnit(1L, "", "")
            ),
            TestRightType.DISTRIBUTION_RIGHTS_EXCLUSIVE,
            corpCalcParams.requestedPMTL,
            data.getTerm(),
            "",
            true,
            1L,
            1L
        ));
        CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
        calcRequest.requestedRightType = corpType;
        calcRequest.requestedTerm = data.getTerm();
        calcRequest.requestedTimePeriod = TimePeriod.FULL_WEEK;
        corpCalcParams.calcRequests = Collections.singleton(calcRequest);
        
        NearestStrandsCalculator nearestStrandsCalculator = new NearestStrandsCalculator(
            new HashMap<>(),
            data.getMediaHierarchy(),
            data.getTerritoryHierarchy()
        );
        
        CorporateCalculator corpAvailCalculator = new FoxCorporateCalculator(
            data.getRightTypeImpactMatrix(),
            nearestStrandsCalculator,
            false,
            false
        );

        //Check the avails answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForAvails(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
        
        //Check the conflict answer
        {
            // Act 
            CorporateCalculatorResult result = corpAvailCalculator.calculateForConflictCheck(corpCalcParams);

            // Assert 
            for (Entry<TermPeriod, ? extends CorpResult> termPeriodEntry : result.getAvailabilityResults().get(corpType).entrySet()) {
                assertEquals(Availability.YES, termPeriodEntry.getValue().getAvailabilityResult().availability);
            }
        }
    }
}
