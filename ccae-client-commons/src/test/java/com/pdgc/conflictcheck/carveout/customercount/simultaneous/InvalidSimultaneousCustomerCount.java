package com.pdgc.conflictcheck.carveout.customercount.simultaneous;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.pdgc.general.structures.carveout.attributes.CustomerLimitType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.carveout.grouping.CarveOutGroup;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.carveout.impl.CustomerCountCarveOut;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.tests.conflictcheck.ConflictCalculationTests.ConflictCalculationTest;

public class InvalidSimultaneousCustomerCount extends ConflictCalculationTest {
	
	@Test
	public void twoCustomertwoLicenseNotSimultaneous() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2022, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2022, 6, 14)));
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
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2)
				));

		carveOuts.add(CustomerCountCarveOut.builder()
				.carveOutId(1L) 
				.carveOutType(FoxCarveOutType.MAX_CUSTOMERS)
				.origTerm(exclusivityLicense1.getTerm()) 
				.maxCustomers(1)
				.internalBrandedCustomerCount(0)
				.internalBrandedCustomers(new ArrayList<>())
				.customerLimitType(CustomerLimitType.INCLUDES_INTERNAL_BRANDED)
				.simultaneousCustomersAllowed(false)
				.spanningDimensions(new ArrayList<>())
				.existingLicenses(existingLicenses) 
				.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
				.carveOutCombineRule(CarveOutCombineRule.AND)
				.carveOutOrder(1)
	        	.carveOutGroupId(1) 
	        	.carveOutCombineRule(CarveOutCombineRule.AND)
	        	.carveOutGroupOrder(1)
	        	.build()
			);
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Collections.singleton(new CarveOutGroup(
	        		CarveOutCombineRule.AND,
	        		1,
	        		1,
	        		carveOuts
        	))
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
					exhibitionLicense1.getTerm(),
					exhibitionLicense1.getTimePeriod()
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
					exhibitionLicense2.getTerm(),
					exhibitionLicense2.getTimePeriod()
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
					exhibitionLicense1.getTerm(),
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
					exhibitionLicense2.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
	
	@Test
	public void threeCustomerThreeLicensesSimultaneousTwoMax() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2024, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2024, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// license 3
		TestDealStrand exhibitionLicense3 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense3.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2024, 6, 14)));
		exhibitionLicense3.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense3.setRightStrandId(exhibitionLicense3.getRightStrandId() + 3);
		exhibitionLicense3.setCustomer(dummyCustomer3);
		TestDealSource tdrs3 = new TestDealSource(drSource);
		tdrs3.setSourceId(drSource.getSourceId() + 3);
		tdrs3.setSourceDetailId(drSource.getSourceDetailId() + 3);
		exhibitionLicense3.setRightSource(tdrs3);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2),
				createCustomerCountLicense(exhibitionLicense3)
				));

		carveOuts.add(CustomerCountCarveOut.builder()
				.carveOutId(1L) 
				.carveOutType(FoxCarveOutType.MAX_CUSTOMERS)
				.origTerm(exclusivityLicense1.getTerm()) 
				.maxCustomers(2)
				.internalBrandedCustomerCount(0)
				.internalBrandedCustomers(new ArrayList<>())
				.customerLimitType(CustomerLimitType.INCLUDES_INTERNAL_BRANDED)
				.simultaneousCustomersAllowed(true)
				.spanningDimensions(new ArrayList<>())
				.existingLicenses(existingLicenses) 
				.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
				.carveOutCombineRule(CarveOutCombineRule.AND)
				.carveOutOrder(1)
	        	.carveOutGroupId(1) 
	        	.carveOutCombineRule(CarveOutCombineRule.AND)
	        	.carveOutGroupOrder(1)
	        	.build()
			);
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Collections.singleton(new CarveOutGroup(
	        		CarveOutCombineRule.AND,
	        		1,
	        		1,
	        		carveOuts
        	))
	    ));
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2, exhibitionLicense3);
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
		
		assertEquals(3, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense1.getTerm(),
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
					exhibitionLicense2.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));

		
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense3.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		assertEquals(3, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense1.getTerm(),
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
					exhibitionLicense2.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));

		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense3, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense3.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
	
	@Test
	public void threeCustomerThreePartiallyOverlappingLicensesSimultaneousTwoMax() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2022, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2023, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// license 3
		TestDealStrand exhibitionLicense3 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense3.setTerm(new Term(DateTimeUtil.createDate(2022, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exhibitionLicense3.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense3.setRightStrandId(exhibitionLicense3.getRightStrandId() + 3);
		exhibitionLicense3.setCustomer(dummyCustomer3);
		TestDealSource tdrs3 = new TestDealSource(drSource);
		tdrs3.setSourceId(drSource.getSourceId() + 3);
		tdrs3.setSourceDetailId(drSource.getSourceDetailId() + 3);
		exhibitionLicense3.setRightSource(tdrs3);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2),
				createCustomerCountLicense(exhibitionLicense3)
				));

		carveOuts.add(CustomerCountCarveOut.builder()
				.carveOutId(1L) 
				.carveOutType(FoxCarveOutType.MAX_CUSTOMERS)
				.origTerm(exclusivityLicense1.getTerm()) 
				.maxCustomers(2)
				.internalBrandedCustomerCount(0)
				.internalBrandedCustomers(new ArrayList<>())
				.customerLimitType(CustomerLimitType.INCLUDES_INTERNAL_BRANDED)
				.simultaneousCustomersAllowed(true)
				.spanningDimensions(new ArrayList<>())
				.existingLicenses(existingLicenses) 
				.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
				.carveOutCombineRule(CarveOutCombineRule.AND)
				.carveOutOrder(1)
	        	.carveOutGroupId(1) 
	        	.carveOutCombineRule(CarveOutCombineRule.AND)
	        	.carveOutGroupOrder(1)
	        	.build()
			);
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Collections.singleton(new CarveOutGroup(
	        		CarveOutCombineRule.AND,
	        		1,
	        		1,
	        		carveOuts
        	))
	    ));
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2, exhibitionLicense3);
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
		
		assertEquals(3, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense1.getTerm(),
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
					exhibitionLicense2.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));

		
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense3.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		assertEquals(3, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense1, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense1.getTerm(),
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
					exhibitionLicense2.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));

		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense3, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exhibitionLicense3.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
	
	@Test
	public void twoCustomerTwoPartiallyOverlappingLicensesSimultaneousOneMax() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2018, 4, 15), DateTimeUtil.createDate(2023, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2022, 4, 15), DateTimeUtil.createDate(2030, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2)
				));

		carveOuts.add(CustomerCountCarveOut.builder()
				.carveOutId(1L) 
				.carveOutType(FoxCarveOutType.MAX_CUSTOMERS)
				.origTerm(exclusivityLicense1.getTerm()) 
				.maxCustomers(1)
				.internalBrandedCustomerCount(0)
				.internalBrandedCustomers(new ArrayList<>())
				.customerLimitType(CustomerLimitType.INCLUDES_INTERNAL_BRANDED)
				.simultaneousCustomersAllowed(true)
				.spanningDimensions(new ArrayList<>())
				.existingLicenses(existingLicenses) 
				.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
				.carveOutCombineRule(CarveOutCombineRule.AND)
				.carveOutOrder(1)
	        	.carveOutGroupId(1) 
	        	.carveOutCombineRule(CarveOutCombineRule.AND)
	        	.carveOutGroupOrder(1)
	        	.build()
			);
		
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Collections.singleton(new CarveOutGroup(
	        		CarveOutCombineRule.AND,
	        		1,
	        		1,
	        		carveOuts
        	))
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
					new Term(exclusivityLicense1.getTerm().getStartDate(), exhibitionLicense1.getTerm().getEndDate()),
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
					new Term(exhibitionLicense2.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
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
					new Term(exclusivityLicense1.getTerm().getStartDate(), exhibitionLicense1.getTerm().getEndDate()),
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
					new Term(exhibitionLicense2.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
	
	@Test
	public void threeCustomerTwoOverlappingLicensesNotSimultaneousOneMax() {
		// license 1
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2018, 4, 15), DateTimeUtil.createDate(2023, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		// license 2
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2024, 4, 15), DateTimeUtil.createDate(2030, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(dummyCustomer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		// license 3
		TestDealStrand exhibitionLicense3 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense3.setTerm(new Term(DateTimeUtil.createDate(2024, 4, 15), DateTimeUtil.createDate(2030, 6, 14)));
		exhibitionLicense3.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense3.setRightStrandId(exhibitionLicense3.getRightStrandId() + 3);
		exhibitionLicense3.setCustomer(dummyCustomer3);
		TestDealSource tdrs3 = new TestDealSource(drSource);
		tdrs3.setSourceId(drSource.getSourceId() + 3);
		tdrs3.setSourceDetailId(drSource.getSourceDetailId() + 3);
		exhibitionLicense3.setRightSource(tdrs3);
		
		// exclusivity
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		List<CustomerCountLicense> existingLicenses = new ArrayList<>();
		existingLicenses.addAll(Arrays.asList(
				createCustomerCountLicense(exhibitionLicense1),
				createCustomerCountLicense(exhibitionLicense2), 
				createCustomerCountLicense(exhibitionLicense3)
				));

		carveOuts.add(CustomerCountCarveOut.builder()
				.carveOutId(1L) 
				.carveOutType(FoxCarveOutType.MAX_CUSTOMERS)
				.origTerm(exclusivityLicense1.getTerm()) 
				.maxCustomers(1)
				.internalBrandedCustomerCount(0)
				.internalBrandedCustomers(new ArrayList<>())
				.customerLimitType(CustomerLimitType.INCLUDES_INTERNAL_BRANDED)
				.simultaneousCustomersAllowed(false)
				.spanningDimensions(new ArrayList<>())
				.existingLicenses(existingLicenses) 
				.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
				.carveOutCombineRule(CarveOutCombineRule.AND)
				.carveOutOrder(1)
	        	.carveOutGroupId(1) 
	        	.carveOutCombineRule(CarveOutCombineRule.AND)
	        	.carveOutGroupOrder(1)
	        	.build()
			);
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
			exclusivityLicense1.getTerm(),
			exclusivityLicense1.getTimePeriod(),
        	Collections.singleton(new CarveOutGroup(
	        		CarveOutCombineRule.AND,
	        		1,
	        		1,
	        		carveOuts
        	))
	    ));
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1, exhibitionLicense2, exhibitionLicense3);
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
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					exclusivityLicense1.getPMTL(),
					new Term(exhibitionLicense2.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					exclusivityLicense1.getPMTL(),
					new Term(exhibitionLicense3.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertEquals(2, getNumConflictsIgnorePMTL(siblingLeafConflicts));
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense2, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense2),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					new Term(exhibitionLicense2.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
		
		assertTrue(contains(
				siblingLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exhibitionLicense3, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					new Term(exhibitionLicense3.getTerm().getStartDate(), exclusivityLicense1.getTerm().getEndDate()),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
}
