package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;

public class ConflictEquivalence extends Equivalence<Conflict> {
	
	private Equivalence<? super ConflictKey> conflictKeyEquivalence;
	
	public ConflictEquivalence(
		Equivalence<? super ConflictKey> conflictKeyEquivalence
	) {
		if (conflictKeyEquivalence == null) {
			conflictKeyEquivalence = Equivalence.equals();
		}
		
		this.conflictKeyEquivalence = conflictKeyEquivalence;
	}
	
	@Override
	protected boolean doEquivalent(Conflict left, Conflict right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }
        
        return conflictKeyEquivalence.equivalent(left.getConflictKey(), right.getConflictKey())
        	&& Objects.equals(left.getConflictStatus(), right.getConflictStatus())
        	&& Objects.equals(left.getConflictOverrides(), right.getConflictOverrides()) //Set overrides equals, so this is fine
        ;
	}

	@Override
	protected int doHash(Conflict obj) {
		return conflictKeyEquivalence.hash(obj.getConflictKey())
			^ Objects.hashCode(obj.getConflictStatus())
			^ obj.getConflictOverrides().size()
		;
	}
	
}
