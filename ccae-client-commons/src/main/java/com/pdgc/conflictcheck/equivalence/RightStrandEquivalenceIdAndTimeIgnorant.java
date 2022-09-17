package com.pdgc.conflictcheck.equivalence;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

/**
 * RightStrandEquivalenceIdAndTimeIgnorant
 */
public class RightStrandEquivalenceIdAndTimeIgnorant extends Equivalence<RightStrand> {

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

        return rightSourceEquals(left.getRightSource(), right.getRightSource())
            && Objects.equals(left.getRightType(), right.getRightType())
            && Objects.equals(left.getPMTL().getMedia(), right.getPMTL().getMedia())
            && Objects.equals(left.getPMTL().getTerritory(), right.getPMTL().getTerritory())
            && Objects.equals(left.getPMTL().getLanguage(), right.getPMTL().getLanguage())
            && left.additionalGroupingEquals(right);
    }

    @Override
    protected int doHash(RightStrand obj) {
        return rightSourceHashCode(obj.getRightSource())
            ^ Objects.hashCode(obj.getRightType())
            ^ Objects.hashCode(obj.getPMTL().getMedia())
            ^ Objects.hashCode(obj.getPMTL().getTerritory())
            ^ Objects.hashCode(obj.getPMTL().getLanguage());
    }

    private static boolean rightSourceEquals(RightSource left, RightSource right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        if (left.getClass() != right.getClass()) {
            return false;
        }

        if (left instanceof FoxDealSource) {
            return ((FoxDealSource) left).getDealId() == ((FoxDealSource) right).getDealId();
        }

        if (Objects.equals(left.getSourceType(), FoxRightSourceType.RESTRICTION)
            || Objects.equals(left.getSourceType(), FoxRightSourceType.PRODUCT_LEVEL_RESTRICTION)) {
            return Objects.equals(left.getSourceType(), right.getSourceType());
        }


        return left.equals(right);
    }

    private static int rightSourceHashCode(RightSource obj) {
        if (obj == null) {
            return Objects.hashCode(0);
        }

        if (obj instanceof FoxDealSource) {
            return Objects.hashCode(((FoxDealSource) obj).getDealId());
        }

        if (Objects.equals(obj.getSourceType(), FoxRightSourceType.RESTRICTION)
            || Objects.equals(obj.getSourceType(), FoxRightSourceType.RESTRICTION)) {
            return Objects.hashCode(obj.getSourceType());
        }

        return Objects.hashCode(obj);
    }

}
