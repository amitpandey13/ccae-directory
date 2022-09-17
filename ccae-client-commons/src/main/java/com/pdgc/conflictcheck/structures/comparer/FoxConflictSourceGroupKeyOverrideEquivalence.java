package com.pdgc.conflictcheck.structures.comparer;

import java.util.Objects;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;

/**
 * FoxConflictSourceGroupKeyOverrideEquivalence
 */
public class FoxConflictSourceGroupKeyOverrideEquivalence extends Equivalence<ConflictSourceGroupKey> {

    @Override
    protected boolean doEquivalent(ConflictSourceGroupKey left, ConflictSourceGroupKey right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        
        return rightSourceEquals(left.getRightSource(), right.getRightSource())
            && rightTypeEquals(left.getRightType(), right.getRightType())
        ;
    }

    @Override
    protected int doHash(ConflictSourceGroupKey obj) {
        return rightSourceHashCode(obj.getRightSource())
            ^ rightTypeHashCode(obj.getRightType())
        ;
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
            return Objects.equals(((FoxDealSource) left).getDealId(), ((FoxDealSource) right).getDealId())
                ;
        }

        if (Objects.equals(left.getSourceType(), FoxRightSourceType.RESTRICTION)
            || Objects.equals(left.getSourceType(), FoxRightSourceType.PRODUCT_LEVEL_RESTRICTION)) {
            return Objects.equals(left.getSourceType(), right.getSourceType());
//					&& Objects.equals(left.getBusinessUnit(), right.getBusinessUnit());
//			BusinessUnit specifically excluded here because restrictions don't get the businessUnitId
        }


        return left.equals(right);
    }

    private static int rightSourceHashCode(RightSource obj) {
        if (obj == null) {
            return Objects.hashCode(0);
        }

        if (obj instanceof FoxDealSource) {
            return Objects.hashCode(((FoxDealSource) obj).getDealId())
                ;
        }

        if (Objects.equals(obj.getSourceType(), FoxRightSourceType.RESTRICTION)
            || Objects.equals(obj.getSourceType(), FoxRightSourceType.PRODUCT_LEVEL_RESTRICTION)) {
            return Objects.hashCode(obj.getSourceType());
//			BusinessUnit specifically excluded here because restrictions are not business unit specific.
        }

        return Objects.hashCode(obj);
    }

    private static boolean rightTypeEquals(RightType left, RightType right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        //both should be non-null by this point
        return Objects.equals(left.getRightTypeId(), right.getRightTypeId());
    }

    private static int rightTypeHashCode(RightType obj) {
        if (obj == null) {
            return 0;
        }
        
        return Objects.hashCode(obj.getRightTypeId());
    }
}
