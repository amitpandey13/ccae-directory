package com.pdgc.ccae.dao;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightsource.impl.FoxCorporateSource;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;
import com.pdgc.general.structures.rightsource.impl.FoxSalesPlanSource;

/**
 * Factory class for RightSource: DealRightSource, CorporateRightSource, SalesWindowSource
 *
 * @author atarshis
 */
public final class RightSourceBuilder {

    private RightSourceBuilder() {

    }

    /**
     * Factory method for building right sources.
     *
     * @param sourceId
     * @param rightSourceTypeId
     * @param sourceDetailId
     * @return
     */
    public static FoxRightSource getRightSource(
        Integer rightSourceTypeId,
        Long sourceId,
        String sourceDetailId,
        FoxBusinessUnit businessUnit,
        String displaySourceType
    ) {
        if (rightSourceTypeId == null) {
            return null;
        }

        FoxRightSourceType rightSourceType = FoxRightSourceType.byValue(rightSourceTypeId);

        if (rightSourceType.getBaseRightSourceType() == BaseRightSourceType.CORPRIGHTS) {
            return new FoxCorporateSource(rightSourceType, sourceId, businessUnit);
        } else if (rightSourceTypeId == Constants.SOURCE_TYPE_ID_DEAL) {
            return new FoxDealSource(FoxRightSourceType.DEAL, sourceId, sourceDetailId, businessUnit, null, displaySourceType, null);
        } else if (rightSourceTypeId == Constants.SOURCE_TYPE_ID_SALES_PLAN) {
            return new FoxSalesPlanSource(rightSourceType, sourceId, sourceDetailId, businessUnit);
        }

        return null;
    }
}
