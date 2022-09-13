package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;

public class ConflictKeyEquivalencePMTLTimeIgnorant extends Equivalence<ConflictKey> {
	
	private Equivalence<? super ConflictSourceGroupKey> groupKeyEquivalence;
	
	public ConflictKeyEquivalencePMTLTimeIgnorant() {
		this(Equivalence.equals());
	}	
	
	public ConflictKeyEquivalencePMTLTimeIgnorant(Equivalence<? super ConflictSourceGroupKey> groupKeyEquivalence) {
		if (groupKeyEquivalence == null) {
			groupKeyEquivalence = Equivalence.equals();
		}
		
		this.groupKeyEquivalence = groupKeyEquivalence;
	}
	
	@Override
	protected boolean doEquivalent(ConflictKey left, ConflictKey right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }
         
    	return Objects.equals(left.getConflictType(), right.getConflictType())
			&& Objects.equals(left.getConflictSeverity(), right.getConflictSeverity())
			&& groupKeyEquivalence.equivalent(left.getPrimaryConflictSourceGroupKey(), right.getPrimaryConflictSourceGroupKey())
			&& groupKeyEquivalence.equivalent(left.getConflictingConflictSourceGroupKey(), right.getConflictingConflictSourceGroupKey())
		;
	}

	@Override
	protected int doHash(ConflictKey obj) {
		return Objects.hashCode(obj.getConflictType())
			^ Objects.hashCode(obj.getConflictSeverity())
			^ groupKeyEquivalence.hash(obj.getPrimaryConflictSourceGroupKey())
			^ groupKeyEquivalence.hash(obj.getConflictingConflictSourceGroupKey())
		;
	}
}
