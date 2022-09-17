package com.pdgc.conflictcheck.structures;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;


public class FoxConflictSourceGroupKey extends ConflictSourceGroupKey implements Serializable {

    private static final long serialVersionUID = 1L;

    public FoxConflictSourceGroupKey(
        FoxRightSource rightSource,
        RightType rightType
    ) {
        super(rightSource, rightType);
    }

    public FoxRightSource getRightSource() {
        return (FoxRightSource) rightSource;
    }

    public String getSourceDetailId() {
        if (rightSource instanceof FoxDealSource) {
            return ((FoxDealSource) rightSource).getSourceDetailId();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        return rightSourceEquals((FoxRightSource) rightSource, (FoxRightSource) ((FoxConflictSourceGroupKey) obj).rightSource)
            && Objects.equals(rightType, ((FoxConflictSourceGroupKey) obj).rightType)
            ;
    }

    @Override
    public int hashCode() {
        return rightSourceHashCode((FoxRightSource) rightSource)
            ^ Objects.hashCode(rightType)
            ;
    }

    private static boolean rightSourceEquals(FoxRightSource left, FoxRightSource right) {
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
                && Objects.equals(left.getSourceDetailId(), right.getSourceDetailId())
                ;
        }

        if (Objects.equals(left.getSourceType(), FoxRightSourceType.RESTRICTION)) {
            return Objects.equals(left.getSourceType(), right.getSourceType())
                && Objects.equals(left.getBusinessUnit(), right.getBusinessUnit());
        }

        return left.equals(right);
    }

    private static int rightSourceHashCode(FoxRightSource obj) {
        if (obj == null) {
            return Objects.hashCode(0);
        }

        if (obj instanceof FoxDealSource) {
            return Objects.hashCode(((FoxDealSource) obj).getDealId())
                ^ Objects.hashCode(obj.getSourceDetailId())
                ;
        }

        if (Objects.equals(obj.getSourceType(), FoxRightSourceType.RESTRICTION)) {
            return Objects.hashCode(obj.getSourceType())
                ^ Objects.hashCode(obj.getBusinessUnit());
        }

        return Objects.hashCode(obj);
    }
}
