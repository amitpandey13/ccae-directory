package com.pdgc.conflictcheck.carveout.customertype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.carveout.grouping.CarveOutGroup;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.carveout.impl.CustomerTypeCarveOut;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.tests.conflictcheck.ConflictCalculationTests.ConflictCalculationTest;

/**
 * Unit Test for the scenario where three customer carveouts that specify
 * two different customers but must follow the highest order
 * 
 * <p>
 * <ul>
 * <li> checkedIn exhibition license 4/15/20 - 6/14/32, TypeId 1
 * <li> checkedIn exhibition license 4/15/20 - 6/14/32, TypeId 2
 * <li> checkedIn exhibition license 4/15/20 - 6/14/32, TypeId 3
 * <li> checkedOut exclusivity license 4/15/21 - 6/14/25 
 * <li> checkedOut carveOut on exclusivity 4/15/21 - 6/14/25, TypeId 1, AND, ORDER 1 
 * <li> checkedOut carveOut on exclusivity 4/15/21 - 6/14/25, TypeId 2, OR , ORDER 2
 * <li> checkedOut carveOut on exclusivity 4/15/21 - 6/14/25, TypeId (1,2,3), AND , ORDER 3
 * </ul> 
 * 
 * <p>
 * 
 * This would result in the carveouts applying for customer 1 or 2 but NOT 3. This is because this is how it 
 * would work in each scenario with the carveOutGroup: 1 OR 2 AND (1,2,3): 
 * 
 * <p>
 * 
 * <ul>
 * <li> exhibition license 1: TRUE OR FALSE AND TRUE
 * <li> exhibition license 2: FALSE OR TRUE AND TRUE
 * <li> exhibition license 3: FALSE OR FALSE AND TRUE
 * </ul>
 * 
 * Thusly 3 is the only conflict that exists
 * 
 * @author Thomas Loh
 *
 */
public class GroupOrderTest3 extends ConflictCalculationTest {
	
	@Test
	public void exhibitionExclusivityLicenseTest() {
		Customer customer1 = new Customer(dummyCustomer);
		customer1.getCustomerTypes().add(dummyCustomerType1);
		Customer customer2 = new Customer(dummyCustomer2);
		customer2.getCustomerTypes().add(dummyCustomerType2);
		Customer customer3 = new Customer(dummyCustomer3);
		customer3.getCustomerTypes().add(dummyCustomerType3);
		
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(customer1);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		TestDealStrand exhibitionLicense2 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense2.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense2.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense2.setRightStrandId(exhibitionLicense2.getRightStrandId() + 2);
		exhibitionLicense2.setCustomer(customer2);
		TestDealSource tdrs2 = new TestDealSource(drSource);
		tdrs2.setSourceId(drSource.getSourceId() + 2);
		tdrs2.setSourceDetailId(drSource.getSourceDetailId() + 2);
		exhibitionLicense2.setRightSource(tdrs2);
		
		TestDealStrand exhibitionLicense3 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense3.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense3.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense3.setRightStrandId(exhibitionLicense3.getRightStrandId() + 3);
		exhibitionLicense3.setCustomer(customer3);
		TestDealSource tdrs3 = new TestDealSource(drSource);
		tdrs3.setSourceId(drSource.getSourceId() + 3);
		tdrs3.setSourceDetailId(drSource.getSourceDetailId() + 3);
		exhibitionLicense3.setRightSource(tdrs3);
		
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		
		Set<CarveOut> carveOuts = new HashSet<>();
		carveOuts.add(CustomerTypeCarveOut.builder()
            	.carveOutId(1L) 
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomerTypes(Arrays.asList(dummyCustomerType1)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(1) 
            	.carveOutGroupCombineRule(CarveOutCombineRule.AND)
            	.build()
            );
		carveOuts.add(CustomerTypeCarveOut.builder()
            	.carveOutId(2L) 
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomerTypes(Arrays.asList(dummyCustomerType2)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.OR)
            	.carveOutOrder(2) 
            	.carveOutGroupCombineRule(CarveOutCombineRule.AND)
            	.build()
            );
		carveOuts.add(CustomerTypeCarveOut.builder()
            	.carveOutId(3L) 
            	.carveOutType(FoxCarveOutType.CUSTOMERS)
            	.origTerm(exclusivityLicense1.getTerm()) 
            	.carveOutCustomerTypes(Arrays.asList(dummyCustomerType1, dummyCustomerType2, dummyCustomerType3)) 
            	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
            	.carveOutCombineRule(CarveOutCombineRule.AND)
            	.carveOutOrder(3) 
            	.carveOutGroupCombineRule(CarveOutCombineRule.AND)
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
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(contains(
				primaryLeafConflicts,
				new TestConflictKey(
					conflictMatrix.getConflictType(exclusivityLicense1, exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
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
					conflictMatrix.getConflictType(exhibitionLicense3, exclusivityLicense1),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exhibitionLicense3),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(exclusivityLicense1),
					exclusivityLicense1.getPMTL(),
					exclusivityLicense1.getTerm(),
					exclusivityLicense1.getTimePeriod()
				),
				pmtlIgnorantConflictKeyEquivalence::equivalent
			));
	}
}
