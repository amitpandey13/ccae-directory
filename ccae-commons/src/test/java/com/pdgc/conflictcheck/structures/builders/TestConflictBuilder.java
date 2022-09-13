package com.pdgc.conflictcheck.structures.builders;

import java.util.Collection;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.impl.TestConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestConflictBuilder implements ConflictBuilder<TestConflict>{

	@Override
	public TestConflict buildConflict(
		ConflictClass conflictClass, 
		RightStrand primaryRightStrand, 
		RightStrand conflictingRightStrand, 
		PMTL pmtl, Term term, 
		TimePeriod timePeriod, 
		Collection<ConflictOverride> conflictOverrides, 
		ConflictStatus conflictStatus, 
		ConflictClass siblingConflictClass
	) {
		return new TestConflict(
			conflictClass,
	        TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(primaryRightStrand),
	        TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(conflictingRightStrand),
	        pmtl,
	        term,
	        timePeriod,
	        conflictOverrides,
	        conflictStatus,
	        siblingConflictClass
		);
	}

	@Override
	public TestConflict buildRolledConflict(
		TestConflict templateConflict, 
		PMTL pmtl, 
		Term term, 
		TimePeriod timePeriod, 
		Collection<TestConflict> sourceConflicts
	) {
		return new TestConflict(
			templateConflict.getConflictClass(),
			(TestConflictSourceGroupKey)templateConflict.getPrimaryConflictSourceGroupKey(),
			(TestConflictSourceGroupKey)templateConflict.getConflictingConflictSourceGroupKey(),
	        pmtl,
	        term,
	        timePeriod,
	        templateConflict.getConflictOverrides(),
	        templateConflict.getConflictStatus(),
	        templateConflict.getSiblingConflictClass()
		);
	}

	@Override
	public TestConflict cloneConflictWithNewPMTL(TestConflict templateConflict, PMTL pmtl) {
		return new TestConflict(
			templateConflict.getConflictClass(),
			(TestConflictSourceGroupKey)templateConflict.getPrimaryConflictSourceGroupKey(),
			(TestConflictSourceGroupKey)templateConflict.getConflictingConflictSourceGroupKey(),
	        pmtl,
	        templateConflict.getTerm(),
	        templateConflict.getTimePeriod(),
	        templateConflict.getConflictOverrides(),
	        templateConflict.getConflictStatus(),
	        templateConflict.getSiblingConflictClass()
		);
	}

}
