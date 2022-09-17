package com.pdgc.conflictcheck.structures.component.impl;

import com.pdgc.conflictcheck.structures.FoxConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class FoxConflictKey extends ConflictKey {

    private static final long serialVersionUID = 1L;

    protected Long productHierarchyId;

    public FoxConflictKey(
        ConflictClass conflictClass,
        FoxConflictSourceGroupKey primaryConflictSourceGroupKey,
        FoxConflictSourceGroupKey conflictingConflictSourceGroupKey,
        PMTL pmtl,
        Long productHierarchyId,
        Term term,
        TimePeriod timePeriod
    ) {
        this.conflictClass = conflictClass;
        this.primaryConflictSourceGroupKey = primaryConflictSourceGroupKey;
        this.conflictingConflictSourceGroupKey = conflictingConflictSourceGroupKey;
        this.pmtl = pmtl;
        this.term = term;
        this.timePeriod = timePeriod;
        this.productHierarchyId = productHierarchyId;
    }

    public FoxConflictKey(FoxConflictKey key) {
        conflictClass = key.getConflictClass();
        primaryConflictSourceGroupKey = key.getPrimaryConflictSourceGroupKey();
        conflictingConflictSourceGroupKey = key.getConflictingConflictSourceGroupKey();
        pmtl = key.getPMTL();
        term = key.getTerm();
        timePeriod = key.getTimePeriod();
        this.productHierarchyId = key.getProductHierarchyId();
    }
    
    @Override
    public FoxConflictSourceGroupKey getPrimaryConflictSourceGroupKey() {
        return (FoxConflictSourceGroupKey)primaryConflictSourceGroupKey;
    }
    
    @Override
    public FoxConflictSourceGroupKey getConflictingConflictSourceGroupKey() {
        return (FoxConflictSourceGroupKey)conflictingConflictSourceGroupKey;
    }

    public Long getProductHierarchyId() {
        return productHierarchyId;
    }
}
