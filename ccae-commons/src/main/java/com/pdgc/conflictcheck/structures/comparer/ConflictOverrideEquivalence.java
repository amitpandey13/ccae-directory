package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;

public class ConflictOverrideEquivalence extends Equivalence<ConflictOverride>{

	private Equivalence<? super ConflictKey> conflictKeyEquivalence;
	
	public ConflictOverrideEquivalence(
		Equivalence<? super ConflictKey> conflictKeyEquivalence
	) {
		if (conflictKeyEquivalence == null) {
			conflictKeyEquivalence = Equivalence.equals();
		}
		
		this.conflictKeyEquivalence = conflictKeyEquivalence;
	}
	
	@Override
	protected boolean doEquivalent(ConflictOverride left, ConflictOverride right) {
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
			&& Objects.equals(left.getOverrideType(), right.getOverrideType())
			&& Objects.equals(left.getOverrideComment(), right.getOverrideComment())
			&& Objects.equals(left.getUserId(), right.getUserId())
		;
	}

	@Override
	protected int doHash(ConflictOverride obj) {
		return conflictKeyEquivalence.hash(obj.getConflictKey())
			^ Objects.hashCode(obj.getOverrideType())
			^ Objects.hashCode(obj.getOverrideComment())
			^ Objects.hashCode(obj.getUserId())
		;
	}
}
