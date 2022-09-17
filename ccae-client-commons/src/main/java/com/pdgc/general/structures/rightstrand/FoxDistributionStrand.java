package com.pdgc.general.structures.rightstrand;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface FoxDistributionStrand extends CorporateRightStrand, FoxRightStrand {
    
    @Override
    public default boolean getIsDistribution() {
        return true;
    }

    public static boolean additionalEquals(FoxDistributionStrand rs1, FoxDistributionStrand rs2) {
        return FoxRightStrand.additionalEquals(rs1, rs2)
        ;
    }
    
    @Override
    public default boolean additionalGroupingEquals(RightStrand obj) {
        if (!(obj instanceof FoxDistributionStrand)) {
            return false;
        }
        
        return additionalEquals(this, (FoxDistributionStrand)obj);
    }
    
    public default boolean isPreliminary() {
        return getRightType().getRightTypeId().longValue() == Constants.RIGHT_TYPE_ID_PRELIMINARY_RIGHTS
            || getRightType().getRightTypeId().longValue() == Constants.RIGHT_TYPE_ID_SALES_PLAN_AS_DIST_RIGHTS
        ;
    }
}
