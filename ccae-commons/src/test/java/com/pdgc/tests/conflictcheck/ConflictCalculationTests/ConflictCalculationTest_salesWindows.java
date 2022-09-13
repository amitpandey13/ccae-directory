package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightsource.impl.TestSalesPlanSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestSalesPlanStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_salesWindows extends ConflictCalculationTest {

    private static Term windowTerm = new Term(DateTimeUtil.createDate(2015, 1, 1), DateTimeUtil.createDate(2022, 12, 31));

    private static TestSalesPlanStrand seasonBasicSalesWindowStrand;
    private static TestSalesPlanStrand seasonBasicSalesBlockStrand;
    private static TestDealStrand seasonBasicNonExclusiveStrand;

    @Before
    public void initSharedStrands() {
        PMTL seasonBasicUsaEnglish = new PMTL(
            new AggregateProduct(Seinfeld_SEASON_1),
            new AggregateMedia(basc),
            new AggregateTerritory(usa),
            new AggregateLanguage(english)
        );
        PMTL seasonBasicWorldAll = new PMTL(
            new AggregateProduct(Seinfeld_SEASON_1),
            new AggregateMedia(basc),
            new AggregateTerritory(Constants.WORLD),
            new AggregateLanguage(Constants.ALL_LANGUAGES)
        );
        seasonBasicSalesWindowStrand = new TestSalesPlanStrand(
            678L,
            seasonBasicUsaEnglish,
            windowTerm,
            new TestSalesPlanSource(TestRightSourceType.SALESPLAN, 345L),
            rtSPWindow,
            seasonBasicUsaEnglish,
            windowTerm
        );
        seasonBasicSalesBlockStrand = new TestSalesPlanStrand(
            679L,
            seasonBasicWorldAll,
            windowTerm,
            new TestSalesPlanSource(TestRightSourceType.SALESPLAN, 346L),
            rtSPBlock,
            seasonBasicWorldAll,
            windowTerm
        );
        seasonBasicNonExclusiveStrand = new TestDealStrand(
            680L,
            seasonBasicWorldAll,
            new TermPeriod(windowTerm, TimePeriod.FULL_WEEK),
            new TestDealSource(TestRightSourceType.DEAL, drSource.getDealId(), "nonExclusiveWorldAll"),
            TestRightType.NONEXCLUSIVE_LICENSE,
            seasonBasicWorldAll,
            windowTerm,
            true,
            null,
            null
        );
    }

    /**
     * Sales Windows conflict with Sales Blocks
     */
    @Test
    public void salesWindowSalesBlockTest() {
        List<RightStrand> primaryRightStrands = Arrays.asList(seasonBasicSalesWindowStrand);
        List<RightStrand> conflictingRightStrands = Arrays.asList(seasonBasicSalesBlockStrand);

        Set<ConflictOverride> existingOverrides = new HashSet<>();

        Set<TestConflict> primaryLeafConflicts = new HashSet<>();
        Set<TestConflict> siblingLeafConflicts = new HashSet<>();

        runConflictCheck(
            conflictCalculator,
            conflictCheckRunner,
            primaryRightStrands,
            conflictingRightStrands,
            existingOverrides,
            false,
            primaryLeafConflicts,
            siblingLeafConflicts
        );

        assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));

        assertTrue(contains(
                primaryLeafConflicts,
                new TestConflictKey(
                        conflictMatrix.getConflictType(seasonBasicSalesWindowStrand, seasonBasicSalesBlockStrand),
                        TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesWindowStrand),
                        TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesBlockStrand),
                        seasonBasicSalesWindowStrand.getPMTL(),
                        seasonBasicSalesWindowStrand.getTerm(),
                        seasonBasicSalesWindowStrand.getTimePeriod()
                ),
                pmtlIgnorantConflictKeyEquivalence::equivalent
        ));
    }

    /**
     * Sales Windows created for PMTL where no rights exist receive No Rights conflicts
     */
    @Test
    public void salesWindowDistributionRightsTest() {
        List<RightStrand> primaryRightStrands = Arrays.asList(seasonBasicSalesWindowStrand);
        List<RightStrand> conflictingRightStrands = new ArrayList<>();

        Set<ConflictOverride> existingOverrides = new HashSet<>();

        Set<TestConflict> primaryLeafConflicts = new HashSet<>();
        Set<TestConflict> siblingLeafConflicts = new HashSet<>();

        runConflictCheck(
            conflictCalculator,
            conflictCheckRunner,
            primaryRightStrands,
            conflictingRightStrands,
            existingOverrides,
            true,
            primaryLeafConflicts,
            siblingLeafConflicts
        );

        assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));

        assertTrue(containsConflict(
            primaryLeafConflicts,
            new TestConflictKey(
                ConflictConstants.NO_CORP_CONFLICT,
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesWindowStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
                seasonBasicSalesWindowStrand.getPMTL(),
                seasonBasicSalesWindowStrand.getTerm(),
                seasonBasicSalesWindowStrand.getTimePeriod()
            ),
            productHierarchy,
            mediaHierarchy,
            territoryHierarchy,
            languageHierarchy
        ));
    }

    /**
     * Sales Blocks for a series conflict with Sales Windows for the season
     */
    @Test
    public void salesBlockSeriesSeasonTest() {
        TestSalesPlanStrand seriesBasicSalesBlockStrand = new TestSalesPlanStrand(
            679L,
            new PMTL(
                new AggregateProduct(Seinfeld_SERIES),
                new AggregateMedia(basc),
                new AggregateTerritory(usa),
                new AggregateLanguage(english)
            ),
            windowTerm,
            new TestSalesPlanSource(TestRightSourceType.SALESPLAN, 344L),
            rtSPBlock,
            new PMTL(
                new AggregateProduct(Seinfeld_SERIES),
                new AggregateMedia(basc),
                new AggregateTerritory(usa),
                new AggregateLanguage(english)
            ),
            windowTerm
        );

        List<RightStrand> primaryRightStrands = Arrays.asList(seriesBasicSalesBlockStrand);
        List<RightStrand> conflictingRightStrands = Arrays.asList(seasonBasicSalesWindowStrand);

        Set<ConflictOverride> existingOverrides = new HashSet<>();

        Set<TestConflict> primaryLeafConflicts = new HashSet<>();
        Set<TestConflict> siblingLeafConflicts = new HashSet<>();

        runConflictCheck(
            conflictCalculator,
            conflictCheckRunner,
            primaryRightStrands,
            conflictingRightStrands,
            existingOverrides,
            false,
            primaryLeafConflicts,
            siblingLeafConflicts
        );

        assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));

        assertTrue(contains(
            primaryLeafConflicts,
            new TestConflictKey(
                conflictMatrix.getConflictType(seriesBasicSalesBlockStrand, seasonBasicSalesWindowStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seriesBasicSalesBlockStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesWindowStrand),
                seriesBasicSalesBlockStrand.getPMTL(),
                seriesBasicSalesBlockStrand.getTerm(),
                seriesBasicSalesBlockStrand.getTimePeriod()
            ),
            pmtlIgnorantConflictKeyEquivalence::equivalent
        ));
    }

    /**
     * Sales Blocks (Sales Window Exclusivity) conflict with Non-Exclusive Licenses (Exhibition)
     */
    @Test
    public void salesBlockExhibitionTest() {
        List<RightStrand> primaryRightStrands = Arrays.asList(seasonBasicSalesBlockStrand);
        List<RightStrand> conflictingRightStrands = Arrays.asList(seasonBasicNonExclusiveStrand);

        Set<ConflictOverride> existingOverrides = new HashSet<>();

        Set<TestConflict> primaryLeafConflicts = new HashSet<>();
        Set<TestConflict> siblingLeafConflicts = new HashSet<>();

        runConflictCheck(
            conflictCalculator,
            conflictCheckRunner,
            primaryRightStrands,
            conflictingRightStrands,
            existingOverrides,
            false,
            primaryLeafConflicts,
            siblingLeafConflicts
        );

        assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));

        assertTrue(contains(
            primaryLeafConflicts,
            new TestConflictKey(
                conflictMatrix.getConflictType(seasonBasicSalesBlockStrand, seasonBasicNonExclusiveStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesBlockStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicNonExclusiveStrand),
                seasonBasicSalesBlockStrand.getPMTL(),
                seasonBasicSalesBlockStrand.getTerm(),
                seasonBasicSalesBlockStrand.getTimePeriod()
            ),
            pmtlIgnorantConflictKeyEquivalence::equivalent
        ));
    }

    /**
     * Non-Exclusive Licenses (Exhibition) conflict with Sales Blocks (Sales Window Exclusivity)
     */
    @Test
    public void exhibitionSalesBlockTest() {
        List<RightStrand> primaryRightStrands = Arrays.asList(seasonBasicNonExclusiveStrand);
        List<RightStrand> conflictingRightStrands = Arrays.asList(seasonBasicSalesBlockStrand);

        Set<ConflictOverride> existingOverrides = new HashSet<>();

        Set<TestConflict> primaryLeafConflicts = new HashSet<>();
        Set<TestConflict> siblingLeafConflicts = new HashSet<>();

        runConflictCheck(
            conflictCalculator,
            conflictCheckRunner,
            primaryRightStrands,
            conflictingRightStrands,
            existingOverrides,
            false,
            primaryLeafConflicts,
            siblingLeafConflicts
        );

        assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));

        assertTrue(contains(
            primaryLeafConflicts,
            new TestConflictKey(
                conflictMatrix.getConflictType(seasonBasicNonExclusiveStrand, seasonBasicSalesBlockStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicNonExclusiveStrand),
                TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(seasonBasicSalesBlockStrand),
                seasonBasicNonExclusiveStrand.getPMTL(),
                seasonBasicNonExclusiveStrand.getTerm(),
                seasonBasicNonExclusiveStrand.getTimePeriod()
            ),
            pmtlIgnorantConflictKeyEquivalence::equivalent
        ));
    }

}
