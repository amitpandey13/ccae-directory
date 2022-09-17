package com.pdgc.conflictcheck.convenience;

import com.pdgc.conflictcheck.structures.FoxConflictSourceGroupKey;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;

public class RollupRequestSourceGroupKeyFields {
    private Long rightSourceId;
    private String rightSourceDetailId;
    private Long rightSourceTypeId;
    private Long businessUnitId;

    public RollupRequestSourceGroupKeyFields(FoxConflictSourceGroupKey sourceGroupKey) {
        if (sourceGroupKey.getRightSource() == null) {
            rightSourceId = null;
            rightSourceDetailId = null;
            rightSourceTypeId = null;
            businessUnitId = null;
        } else {
            if (sourceGroupKey.getRightSource() instanceof FoxDealSource) {
                rightSourceDetailId = ((FoxDealSource) sourceGroupKey.getRightSource()).getSourceDetailId();
            } else {
                rightSourceDetailId = null;
            }
            rightSourceId = sourceGroupKey.getRightSource().getSourceId();
            rightSourceTypeId = Long.valueOf(sourceGroupKey.getRightSource().getSourceType().getId());
            businessUnitId = Long.valueOf(sourceGroupKey.getRightSource().getBusinessUnit().getId());
        }
    }

    public Long getRightSourceId() {
        return rightSourceId;
    }

    public Long getRightSourceTypeId() {
        return rightSourceTypeId;
    }

    public Long getBusinessUnitId() {
        return businessUnitId;
    }

    public String getRightSourceDetailId() {
        return rightSourceDetailId;
    }

}
