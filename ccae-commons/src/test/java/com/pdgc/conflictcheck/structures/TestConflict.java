package com.pdgc.conflictcheck.structures;

import java.util.Collection;
import java.util.HashSet;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.ConflictType;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.impl.TestConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestConflict extends Conflict {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//the super's conflictKey should be pointed to this editable version of the conflictKey
	TestConflictKey testConflictKey;
	
	public TestConflict(
		TestConflictKey conflictKey,
		Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass
	) {
		super(
			conflictKey,
			conflictOverrides,
			conflictStatus,
			siblingConflictClass,
			true,
			true
		);
		testConflictKey = new TestConflictKey(conflictKey);
		this.conflictKey = testConflictKey;
	}
	
	public TestConflict(
		ConflictClass conflictClass,
        TestConflictSourceGroupKey primaryConflictSourceGroupKey,
        TestConflictSourceGroupKey conflictingConflictSourceGroupKey,
        PMTL pmtl,
        Term term,
        TimePeriod timePeriod,
        Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass
    ) {
        this(
        	new TestConflictKey(
        		conflictClass,
            	primaryConflictSourceGroupKey, 
            	conflictingConflictSourceGroupKey, 
            	pmtl,
            	term, 
            	timePeriod
        	),
        	conflictOverrides, 
        	conflictStatus, 
        	siblingConflictClass
        );
    }

    public TestConflict(Conflict c) {
    	super();
    	
    	testConflictKey = new TestConflictKey(conflictKey);
        conflictKey = testConflictKey;
    }
    
    @Override
    public TestConflictKey getConflictKey() {
    	return testConflictKey;
    }
    
    public void setConflictClass(ConflictClass conflictClass) {
    	this.testConflictKey.setConflictClass(conflictClass);
    }

    public void setConflictType(ConflictType conflictType) {
        this.testConflictKey.setConflictType(conflictType);
    }

    public void setConflictSeverity(ConflictSeverity conflictSeverity) {
        this.testConflictKey.setConflictSeverity(conflictSeverity);
    }

    public void setPrimaryConflictSourceGroupKey(ConflictSourceGroupKey primaryConflictSourceGroupKey) {
        this.testConflictKey.setPrimaryConflictSourceGroupKey(primaryConflictSourceGroupKey);
    }

    public void setConflictingConflictSourceGroupKey(ConflictSourceGroupKey conflictingConflictSourceGroupKey) {
        this.testConflictKey.setConflictingConflictSourceGroupKey(conflictingConflictSourceGroupKey);
    }

    public void setPMTL(PMTL pmtl) {
        this.testConflictKey.setPMTL(pmtl);
    }

    public void setProduct(Product product) {
        this.testConflictKey.setProduct(product);
    }

    public void setMTL(MTL mtl) {
        this.testConflictKey.setMTL(mtl);
    }

    public void setTerm(Term term) {
        this.testConflictKey.setTerm(term);
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.testConflictKey.setTimePeriod(timePeriod);
    }

    public void setConflictOverrides(Collection<ConflictOverride> conflictOverrides) {
        this.conflictOverrides = new HashSet<ConflictOverride>(conflictOverrides);
    }

    public void setConflictStatus(ConflictStatus conflictStatus) {
        this.conflictStatus = conflictStatus;
    }

    public void setSiblingConflictClass(ConflictClass siblingConflictClass) {
    	this.siblingConflictClass = siblingConflictClass;
    }
}
