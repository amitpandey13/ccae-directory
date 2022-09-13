package com.pdgc.general.structures.rightstrand.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.rightstrand.impl.NonAggregateRightStrand;

public class RightStrandEquivalenceIdAndPMTLIgnorant extends Equivalence<NonAggregateRightStrand> {

	@Override
	protected boolean doEquivalent(NonAggregateRightStrand left, NonAggregateRightStrand right) {
		if (left == null && right == null) {
            return true;
        }
        if (left == null && right != null) {
            return false;
        }
        if (left != null && right == null) {
            return false;
        }

        return Objects.equals(left.getRightSource(), right.getRightSource())
            && Objects.equals(left.getRightType(), right.getRightType())
            && Objects.equals(left.getTerm(), right.getTerm())
            && Objects.equals(left.getTimePeriod(), right.getTimePeriod())
            && left.additionalGroupingEquals(right)
        ;
	}

	@Override
	protected int doHash(NonAggregateRightStrand obj) {
		return Objects.hashCode(obj.getRightSource())
			^ Objects.hashCode(obj.getRightType())
			^ Objects.hashCode(obj.getTerm())
			^ Objects.hashCode(obj.getTimePeriod())
		;
	}
}
