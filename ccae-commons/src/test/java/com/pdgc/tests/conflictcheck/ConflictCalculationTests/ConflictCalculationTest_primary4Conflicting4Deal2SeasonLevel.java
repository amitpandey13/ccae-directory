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
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.rightsource.TestRightSourceType;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;

public class ConflictCalculationTest_primary4Conflicting4Deal2SeasonLevel extends ConflictCalculationTest {
	@Test
	public void primary4Conflicting4Deal2Product11Test() {
		// Primary strands
		DealRightStrand _1386450;
		DealRightStrand _801105;
		DealRightStrand _801107;
		DealRightStrand _86;
		
		//Conflicting strands
		DealRightStrand _87;
		DealRightStrand _801106;
		DealRightStrand _1386451;
		DealRightStrand _83;
		DealRightStrand _84;
		DealRightStrand _85;
		DealRightStrand _801108;
		{
			TestDealStrand tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(1386450l);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 4L, "7741802"));
			tempRightStrand.setMTL(new MTL(ptvc, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
			_1386450 = tempRightStrand;
	
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(801105);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 4L, "7741801"));
			tempRightStrand.setMTL(new MTL(basc, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
			_801105 = tempRightStrand;
	
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(801107);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 4L, "7741800"));
			tempRightStrand.setMTL(new MTL(ptv, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
			_801107 = tempRightStrand;
	
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(86);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 4L, "7741799"));
			tempRightStrand.setMTL(new MTL(svodi, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			_86 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(87);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 203L, "7741803"));
			tempRightStrand.setMTL(new MTL(svodi, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			_87 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(801106);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 203L, "7741804"));
			tempRightStrand.setMTL(new MTL(basc, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
			_801106 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(1386451);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 203L, "7741805"));
			tempRightStrand.setMTL(new MTL(ptvc, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
			_1386451 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(83);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 400L, "7741807"));
			tempRightStrand.setMTL(new MTL(basc, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			_83 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(84);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 400L, "7741806"));
			tempRightStrand.setMTL(new MTL(ptv, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			_84 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(85);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 400L, "7741808"));
			tempRightStrand.setMTL(new MTL(ppv, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.HOLDBACK);
			_85 = tempRightStrand;
			
			tempRightStrand = new TestDealStrand(simpleSeasonLicense);
			tempRightStrand.setRightStrandId(801108);
			tempRightStrand.setRightSource(new TestDealSource(TestRightSourceType.DEAL, 400L, "7741809"));
			tempRightStrand.setMTL(new MTL(svodi, canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setActualTerrLang(new TerrLang(canada, Constants.ALL_LANGUAGES));
			tempRightStrand.setRightType(TestRightType.EXCLUSIVE_LICENSE);
			_801108 = tempRightStrand;
		}
		
		List<RightStrand> primaryRightStrands = Arrays.asList(_1386450, _86, _801105, _801107);
		List<RightStrand> conflictingRightStrands = Arrays.asList(_87, _801106, _1386451, _83, _84, _85, _801108);
		List<ConflictOverride> existingOverrides = new ArrayList<>();
		
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
		
		assertEquals(8, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		{
			//_1386450 conflicts - 2 conflicts
			{
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_1386450, _84),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386450),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_84),
						new PMTL(_1386450.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_1386450, _1386451),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386450),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386451),
						new PMTL(_1386450.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
			
			//_801105 conflicts - 2 conflicts
			{
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801105, _83),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801105),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_83),
						new PMTL(_801105.getPMTL().getProduct(), basc, canada, Constants.ALL_LANGUAGES),
						_801105.getTerm(),
						_801105.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801105, _801106),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801105),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801106),
						new PMTL(_801105.getPMTL().getProduct(), basc, canada, Constants.ALL_LANGUAGES),
						_801105.getTerm(),
						_801105.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
		
			//_801107 conflicts - 2 conflicts
			{
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801107, _84),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801107),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_84),
						new PMTL(_801107.getPMTL().getProduct(), ptv, canada, Constants.ALL_LANGUAGES),
						_801107.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801107, _1386451),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801107),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386451),
						new PMTL(_801107.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
			
			//_86 conflicts - 2 conflicts
			{
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_86, _87),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_86),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_87),
						new PMTL(_86.getPMTL().getProduct(), svodi, canada, Constants.ALL_LANGUAGES),
						_86.getTerm(),
						_86.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					primaryLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_86, _801108),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_86),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801108),
						new PMTL(_86.getPMTL().getProduct(), svodi, canada, Constants.ALL_LANGUAGES),
						_86.getTerm(),
						_86.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
		}
		
		assertEquals(8, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		{
			//_1386450 conflicts - 2 conflicts
			{
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_84, _1386450),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_84),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386450),
						new PMTL(_1386450.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_1386451, _1386450),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386451),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386450),
						new PMTL(_1386450.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
			
			//_801105 conflicts - 2 conflicts
			{
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_83, _801105),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_83),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801105),
						new PMTL(_801105.getPMTL().getProduct(), basc, canada, Constants.ALL_LANGUAGES),
						_801105.getTerm(),
						_801105.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801106, _801105),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801106),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801105),
						new PMTL(_801105.getPMTL().getProduct(), basc, canada, Constants.ALL_LANGUAGES),
						_801105.getTerm(),
						_801105.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
		
			//_801107 conflicts - 2 conflicts
			{
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_84, _801107),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_84),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801107),
						new PMTL(_801107.getPMTL().getProduct(), ptv, canada, Constants.ALL_LANGUAGES),
						_801107.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_1386451, _801107),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_1386451),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801107),
						new PMTL(_801107.getPMTL().getProduct(), ptvc, canada, Constants.ALL_LANGUAGES),
						_1386450.getTerm(),
						_1386450.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
			
			//_86 conflicts - 2 conflicts
			{
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_87, _86),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_87),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_86),
						new PMTL(_86.getPMTL().getProduct(), svodi, canada, Constants.ALL_LANGUAGES),
						_86.getTerm(),
						_86.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
				
				assertTrue(contains(
					siblingLeafConflicts,
					new TestConflictKey(
						conflictMatrix.getConflictType(_801108, _86),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_801108),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(_86),
						new PMTL(_86.getPMTL().getProduct(), svodi, canada, Constants.ALL_LANGUAGES),
						_86.getTerm(),
						_86.getTimePeriod()
					),
					pmtlIgnorantConflictKeyEquivalence::equivalent
				));
			}
		}
	}
}
