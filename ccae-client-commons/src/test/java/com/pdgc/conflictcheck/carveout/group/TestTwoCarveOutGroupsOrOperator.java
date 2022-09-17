package com.pdgc.conflictcheck.carveout.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.pdgc.conflictcheck.service.ConflictCalculator;
import com.pdgc.conflictcheck.service.ConflictCheckRunner;
import com.pdgc.conflictcheck.service.OverrideApplier;
import com.pdgc.conflictcheck.service.TestConflictCheckRunner;
import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.calculation.carveout.CarveOutImpactCalculator;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.TestRightType;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.carveout.grouping.CarveOutGroup;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.carveout.impl.CustomerCarveOut;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.tests.conflictcheck.ConflictCalculationTests.ConflictCalculationTest;

/**
 * This unit test class tests the AND operator functions properly between groups.
 * 
 * @author thomas
 *
 */
public class TestTwoCarveOutGroupsOrOperator extends ConflictCalculationTest {
	
	@Test
	public void failGroup1OrFailGroup2_shouldFail() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		Set<CarveOut> carveOuts2 = new HashSet<>();
		carveOuts.add(CustomerCarveOut.builder()
				.carveOutId(1L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer3)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(1)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(1)
            	.build()
            );
		carveOuts2.add(CustomerCarveOut.builder()
				.carveOutId(2L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer3)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(2)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(2)
            	.build()
            );		
		
		CarveOutGroup carveOutGroup1 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		1,
        		1,
        		carveOuts
    	);
		CarveOutGroup carveOutGroup2 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		2,
        		2,
        		carveOuts2
    	);
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2)
				));
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Arrays.asList(carveOutGroup1, carveOutGroup2)
	    ));
		
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2);
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		ConflictCalculator<TestConflict> conflictCalculator = new ConflictCalculator<>(
			rightTypeCorpAvailMap,
			conflictMatrix, 
			conflictBuilder,
			corpAvailabilityCalculator,
			new CarveOutImpactCalculator(rightTypeCarveOutActionMap)
		);
		
		ConflictCheckRunner<TestConflict> conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
		
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
		
		assertEquals(2, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertEquals(2, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense2, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		Collection<RightStrand> primaryRightStrandsReverse = Arrays.asList(exhibitionLicense1, exhibitionLicense2);
		Collection<RightStrand> conflictingRightStrandsReverse = Arrays.asList(exclusivityLicense1, distributionRights);
		
		Set<ConflictOverride> existingOverridesReverse = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflictsReverse = new HashSet<>();
		Set<TestConflict> siblingLeafConflictsReverse = new HashSet<>();
		
		runConflictCheck(
				conflictCalculator,
				conflictCheckRunner,
				primaryRightStrandsReverse,
				conflictingRightStrandsReverse,
				existingOverridesReverse,
				true,
				primaryLeafConflictsReverse,
				siblingLeafConflictsReverse
			);
		
		assertEquals(2, getNumConflictsIgnorePMTL(primaryLeafConflictsReverse));
		assertTrue(contains(
				primaryLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				primaryLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense2, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertEquals(2, getNumConflictsIgnorePMTL(siblingLeafConflictsReverse));
		assertTrue(contains(
				siblingLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				siblingLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
	
	@Test
	public void passGroup1OrFailGroup2_shouldPass() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		Set<CarveOut> carveOuts2 = new HashSet<>();
		carveOuts.add(CustomerCarveOut.builder()
				.carveOutId(1L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(1)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(1)
            	.build()
            );
		carveOuts2.add(CustomerCarveOut.builder()
				.carveOutId(2L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer2)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(2)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(2)
            	.build()
            );		
		
		CarveOutGroup carveOutGroup1 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		1,
        		1,
        		carveOuts
    	);
		CarveOutGroup carveOutGroup2 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		2,
        		2,
        		carveOuts2
    	);
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2)
				));
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Arrays.asList(carveOutGroup1, carveOutGroup2)
	    ));
		
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2);
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		ConflictCalculator<TestConflict> conflictCalculator = new ConflictCalculator<>(
			rightTypeCorpAvailMap,
			conflictMatrix, 
			conflictBuilder,
			corpAvailabilityCalculator,
			new CarveOutImpactCalculator(rightTypeCarveOutActionMap)
		);
		
		ConflictCheckRunner<TestConflict> conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
		
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
		
		assertEquals(0, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		
		Collection<RightStrand> primaryRightStrandsReverse = Arrays.asList(exhibitionLicense1, exhibitionLicense2);
		Collection<RightStrand> conflictingRightStrandsReverse = Arrays.asList(exclusivityLicense1, distributionRights);
		
		Set<ConflictOverride> existingOverridesReverse = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflictsReverse = new HashSet<>();
		Set<TestConflict> siblingLeafConflictsReverse = new HashSet<>();
		
		runConflictCheck(
				conflictCalculator,
				conflictCheckRunner,
				primaryRightStrandsReverse,
				conflictingRightStrandsReverse,
				existingOverridesReverse,
				true,
				primaryLeafConflictsReverse,
				siblingLeafConflictsReverse
			);
		
		assertEquals(0, getNumConflictsIgnorePMTL(primaryLeafConflictsReverse));
		assertEquals(0, getNumConflictsIgnorePMTL(siblingLeafConflictsReverse));
	}
	
	@Test
	public void partialPassGroup1OrFailGroup2_shouldPartialPass() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		Set<CarveOut> carveOuts2 = new HashSet<>();
		carveOuts.add(CustomerCarveOut.builder()
				.carveOutId(1L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer2)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(1)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(1)
            	.build()
            );
		carveOuts2.add(CustomerCarveOut.builder()
				.carveOutId(2L)
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomers(Arrays.asList(dummyCustomer3)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1)
            	.carveOutGroupId(2)
            	.carveOutGroupCombineRule(CarveOutCombineRule.OR)
            	.carveOutGroupOrder(2)
            	.build()
            );		
		
		CarveOutGroup carveOutGroup1 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		1,
        		1,
        		carveOuts
    	);
		CarveOutGroup carveOutGroup2 = new CarveOutGroup(
        		CarveOutCombineRule.OR,
        		2,
        		2,
        		carveOuts2
    	);
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2)
				));
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Arrays.asList(carveOutGroup1, carveOutGroup2)
	    ));
		
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2);
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		ConflictCalculator<TestConflict> conflictCalculator = new ConflictCalculator<>(
			rightTypeCorpAvailMap,
			conflictMatrix, 
			conflictBuilder,
			corpAvailabilityCalculator,
			new CarveOutImpactCalculator(rightTypeCarveOutActionMap)
		);
		
		ConflictCheckRunner<TestConflict> conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
		
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
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertEquals(1, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		Collection<RightStrand> primaryRightStrandsReverse = Arrays.asList(exhibitionLicense1, exhibitionLicense2);
		Collection<RightStrand> conflictingRightStrandsReverse = Arrays.asList(exclusivityLicense1, distributionRights);
		
		Set<ConflictOverride> existingOverridesReverse = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflictsReverse = new HashSet<>();
		Set<TestConflict> siblingLeafConflictsReverse = new HashSet<>();
		
		runConflictCheck(
				conflictCalculator,
				conflictCheckRunner,
				primaryRightStrandsReverse,
				conflictingRightStrandsReverse,
				existingOverridesReverse,
				true,
				primaryLeafConflictsReverse,
				siblingLeafConflictsReverse
			);
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflictsReverse));
		assertTrue(contains(
				primaryLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertEquals(1, getNumConflictsIgnorePMTL(siblingLeafConflictsReverse));
		assertTrue(contains(
				siblingLeafConflictsReverse,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
}
