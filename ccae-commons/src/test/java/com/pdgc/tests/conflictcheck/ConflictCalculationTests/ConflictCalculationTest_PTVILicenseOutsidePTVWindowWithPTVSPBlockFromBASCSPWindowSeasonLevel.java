package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightsource.impl.TestSalesPlanSource;
import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.SalesPlanRightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.TestSalesPlanStrand;
import com.pdgc.general.util.DateTimeUtil;

public class ConflictCalculationTest_PTVILicenseOutsidePTVWindowWithPTVSPBlockFromBASCSPWindowSeasonLevel extends ConflictCalculationTest {
	/**
	 * This test should create a conflict for a PTV license that extends beyond the
	 * end of the PTV Sales Window into an SVOD window w/ a PTV Block.
	 * Period of the conflict is 1/1/2018 - 12/31/2018.
	 */
	@Test
	public void PTVILicenseOutsidePTVWindowWithPTVSPBlockFromBASCSPWindowSeasonLevelTest() {
		long dealId = 4;
		String dealRightSourceDetailId = "7741802";
		long salesWindowId = 100;
		
		DealRightStrand license; // PTV-I license
		SalesPlanRightStrand ptvSp; // Pay TV Sales Window
		SalesPlanRightStrand ptvSpBlock; // PTV generated SVOD Sales Window Blocks - no impact in this test
		SalesPlanRightStrand svodSp; // SVOD Sales Window
		SalesPlanRightStrand svodSpBlock; // SVOD generated PTV Sales Window Blocks - has impact in this test					
		{
			TestDealStrand tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(1l);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, dealId, dealRightSourceDetailId));
			tempRightStrand.setMTL(new MTL(ptvi, canada, english));
			tempRightStrand.setActualMTL(new MTL(ptvi, canada, english));
			tempRightStrand.setTerrLang(new TerrLang(canada, english));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
			tempRightStrand.setTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2018, 12, 31)));
			tempRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2018, 12, 31)));
			license = tempRightStrand;
			
			TestSalesPlanStrand tempSalesRightStrand = new TestSalesPlanStrand(seasonSalesWindowStrand);
			tempSalesRightStrand.setRightStrandId(2l);
			tempSalesRightStrand.setRightSource(new TestSalesPlanSource(TestRightSourceType.SALESPLAN, salesWindowId));
			tempSalesRightStrand.setMTL(new MTL(ptv, canada, english));
			tempSalesRightStrand.setActualMTL(new MTL(ptv, canada, english));
			tempSalesRightStrand.setTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setRightType(TestRightType.SALES_PLAN_WINDOW);
			tempSalesRightStrand.setTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			tempSalesRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			ptvSp = tempSalesRightStrand;
	
			tempSalesRightStrand = new TestSalesPlanStrand(seasonSalesWindowBlockStrand);
			tempSalesRightStrand.setRightStrandId(3l);
			tempSalesRightStrand.setRightSource(new TestSalesPlanSource(TestRightSourceType.SALESPLAN, salesWindowId));
			tempSalesRightStrand.setMTL(new MTL(svod, canada, english));
			tempSalesRightStrand.setActualMTL(new MTL(svod, canada, english));
			tempSalesRightStrand.setTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setRightType(TestRightType.SALES_PLAN_BLOCK);
			tempSalesRightStrand.setTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			tempSalesRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2017, 1, 1), DateTimeUtil.createDate(2017, 12, 31)));
			ptvSpBlock = tempSalesRightStrand;
	
			tempSalesRightStrand = new TestSalesPlanStrand(seasonSalesWindowStrand);
			tempSalesRightStrand.setRightStrandId(4l);
			tempSalesRightStrand.setRightSource(new TestSalesPlanSource(TestRightSourceType.SALESPLAN, salesWindowId));
			tempSalesRightStrand.setMTL(new MTL(svod, canada, english));
			tempSalesRightStrand.setActualMTL(new MTL(svod, canada, english));
			tempSalesRightStrand.setTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setRightType(TestRightType.SALES_PLAN_WINDOW);
			tempSalesRightStrand.setTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2019, 12, 31)));
			tempSalesRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2019, 12, 31)));
			svodSp = tempSalesRightStrand;
	
			tempSalesRightStrand = new TestSalesPlanStrand(seasonSalesWindowBlockStrand);
			tempSalesRightStrand.setRightStrandId(5l);
			tempSalesRightStrand.setRightSource(new TestSalesPlanSource(TestRightSourceType.SALESPLAN, salesWindowId));
			tempSalesRightStrand.setMTL(new MTL(ptv, canada, english));
			tempSalesRightStrand.setActualMTL(new MTL(ptv, canada, english));
			tempSalesRightStrand.setTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setActualTerrLang(new TerrLang(canada, english));
			tempSalesRightStrand.setRightType(TestRightType.SALES_PLAN_BLOCK);
			tempSalesRightStrand.setTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2019, 12, 31)));
			tempSalesRightStrand.setOrigTerm(new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2019, 12, 31)));
			svodSpBlock = tempSalesRightStrand;
		}
		TestDistributionStrand distrRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(license);
		List<RightStrand> conflictingRightStrands = Arrays.asList(svodSp, svodSpBlock, ptvSp, ptvSpBlock, distrRights);
		List<ConflictOverride> existingOverrides = new ArrayList<>();
		
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
		assertTrue(contains(
			primaryLeafConflicts,
			new TestConflictKey(
				conflictMatrix.getConflictType(license, ptvSpBlock),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(license),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(ptvSpBlock),
				license.getPMTL(),
				new Term(DateTimeUtil.createDate(2018, 1, 1), DateTimeUtil.createDate(2018, 12, 31)),
				license.getTimePeriod()
			),
			pmtlIgnorantConflictKeyEquivalence::equivalent
		));
		
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
	}
}
