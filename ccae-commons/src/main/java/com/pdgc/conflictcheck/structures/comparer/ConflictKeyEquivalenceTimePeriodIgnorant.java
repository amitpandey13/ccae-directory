package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;

public class ConflictKeyEquivalenceTimePeriodIgnorant extends Equivalence<ConflictKey>{
	
	private ConflictKeyEquivalenceTimeIgnorant childEquivalence;
	
	public ConflictKeyEquivalenceTimePeriodIgnorant() {
		this(Equivalence.equals());
	}	
	
	public ConflictKeyEquivalenceTimePeriodIgnorant(Equivalence<? super ConflictSourceGroupKey> groupKeyEquivalence) {
		if (groupKeyEquivalence == null) {
			groupKeyEquivalence = Equivalence.equals();
		}
		
		this.childEquivalence = new ConflictKeyEquivalenceTimeIgnorant(groupKeyEquivalence);
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

		return childEquivalence.equivalent(left, right)
			&& Objects.equals(left.getTerm(), right.getTerm())
		;
	}

	@Override
	protected int doHash(ConflictKey obj) {
		return childEquivalence.doHash(obj)
			^ Objects.hashCode(obj.getTerm());
	}
}
