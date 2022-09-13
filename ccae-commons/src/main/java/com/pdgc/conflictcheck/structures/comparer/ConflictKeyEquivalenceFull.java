package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;

public class ConflictKeyEquivalenceFull extends Equivalence<ConflictKey> {

	private ConflictKeyEquivalencePMTLIgnorant childEquivalence;
	
	public ConflictKeyEquivalenceFull() {
		this(Equivalence.equals());
	}	
	
	public ConflictKeyEquivalenceFull(Equivalence<? super ConflictSourceGroupKey> groupKeyEquivalence) {
		if (groupKeyEquivalence == null) {
			groupKeyEquivalence = Equivalence.equals();
		}
		
		this.childEquivalence = new ConflictKeyEquivalencePMTLIgnorant(groupKeyEquivalence);
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
        	&& Objects.equals(left.getConflictKey().getPMTL(), right.getConflictKey().getPMTL())
        ;
	}

	@Override
	protected int doHash(ConflictKey obj) {
		return childEquivalence.hash(obj)
            ^ Objects.hashCode(obj.getConflictKey().getPMTL())
        ;
	}

}
