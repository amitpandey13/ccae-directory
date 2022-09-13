package com.pdgc.conflictcheck.structures.comparer;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;

public class ConflictKeyContainerEquivalence extends Equivalence<IConflictKeyContainer> {

    private Equivalence<? super ConflictKey> conflictKeyEquivalence;
    
    public ConflictKeyContainerEquivalence(
        Equivalence<? super ConflictKey> conflictKeyEquivalence
    ) {
        if (conflictKeyEquivalence == null) {
            conflictKeyEquivalence = Equivalence.equals();
        }
        
        this.conflictKeyEquivalence = conflictKeyEquivalence;
    }
    
    @Override
    protected boolean doEquivalent(IConflictKeyContainer left, IConflictKeyContainer right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }
        
        return conflictKeyEquivalence.equivalent(left.getConflictKey(), right.getConflictKey());
    }

    @Override
    protected int doHash(IConflictKeyContainer obj) {
        return conflictKeyEquivalence.hash(obj.getConflictKey());
    }
}
