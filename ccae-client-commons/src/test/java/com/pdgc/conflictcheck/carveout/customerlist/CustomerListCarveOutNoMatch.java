package com.pdgc.conflictcheck.carveout.customerlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
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
 * CustomerCarveOut test that will test that a customerCarveOut doesn't remove conflicts that don't match.
 * 
 * <p>
 * <ul>
 * <li> checkedIn exhibition license 4/15/20 - 6/14/32, customerId: 1
 * <li> checkedOut exclusivity license 4/15/21 - 6/14/25
 * <li> checkedOut carveOut on exclusivity 4/15/21 - 6/14/25, customerId: 2
 * </ul> 
 * <p>
 * 
 * The result will be conflicts generated.
 *
 * @author thomas
 *
 */
public class CustomerListCarveOutNoMatch extends ConflictCalculationTest {
	
	@Test
	public void exhibitionExclusivityLicenseTest() {
		TestDealStrand exhibitionLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exhibitionLicense1.setTerm(new Term(DateTimeUtil.createDate(2020, 4, 15), DateTimeUtil.createDate(2032, 6, 14)));
		exhibitionLicense1.setRightType(TestRightType.NONEXCLUSIVE_LICENSE);
		exhibitionLicense1.setRightStrandId(exhibitionLicense1.getRightStrandId() + 1);
		exhibitionLicense1.setCustomer(dummyCustomer);
		TestDealSource tdrs = new TestDealSource(drSource);
		tdrs.setSourceId(drSource.getSourceId() + 1);
		tdrs.setSourceDetailId(drSource.getSourceDetailId() + 1);
		exhibitionLicense1.setRightSource(tdrs);
		
		TestDealStrand exclusivityLicense1 = new TestDealStrand(simpleEpisodeLicense);
		exclusivityLicense1.setTerm(new Term(DateTimeUtil.createDate(2021, 4, 15), DateTimeUtil.createDate(2025, 6, 14)));
		exclusivityLicense1.setRightType(TestRightType.HOLDBACK);
		exclusivityLicense1.setCarveOuts(new FoxCarveOutContainer(
				exclusivityLicense1.getTerm(),
				exclusivityLicense1.getTimePeriod(),
	        	Collections.singleton(new CarveOutGroup(
		        		CarveOutCombineRule.AND,
		        		1,
		        		1,
	        		Sets.newHashSet(CustomerCarveOut.builder()
	                	.carveOutType(FoxCarveOutType.CUSTOMERS)
	                	.origTerm(exclusivityLicense1.getTerm()) 
	                	.carveOutCustomers(Arrays.asList(dummyCustomer2)) 
	                	.carveOutImpactType(CarveOutImpactType.EXCEPT_AGAINST) 
	                	.carveOutCombineRule(CarveOutCombineRule.AND)
	                	.carveOutGroupCombineRule(CarveOutCombineRule.AND)
	                	.build()
	                )
	        	))
	        ));
		TestDistributionStrand distributionRights = new TestDistributionStrand(seriesDistributionRights);
		
		List<RightStrand> primaryRightStrands = Arrays.asList(exclusivityLicense1);
		List<RightStrand> conflictingRightStrands = Arrays.asList(distributionRights, exhibitionLicense1);
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
	}
}