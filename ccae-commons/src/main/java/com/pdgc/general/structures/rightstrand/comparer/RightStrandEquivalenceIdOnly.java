package com.pdgc.general.structures.rightstrand.comparer;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class RightStrandEquivalenceIdOnly extends Equivalence<RightStrand> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean doEquivalent(RightStrand left, RightStrand right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }
        
        return Objects.equals(left.getRightStrandId(), right.getRightStrandId());
	}

	@Override
	protected int doHash(RightStrand t) {
		return Objects.hashCode(t.getRightStrandId());
	}
}
