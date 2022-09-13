package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public interface RightStrand {

    long getRightStrandId();
    PMTL getPMTL();
    TermPeriod getTermPeriod();
    RightSource getRightSource();
    RightType getRightType();
    PMTL getActualPMTL();
    Term getOrigTerm();
    boolean isCheckedIn();
    String getComment();
    Long getProductHierarchyId();
    Long getStatusId();

    /**
     * Every subclass should override this and handle the checking of any additional fields that may exist within the subclass
     * @param obj
     * @return
     */
    boolean additionalGroupingEquals(RightStrand obj);

    public default Term getTerm() {
        return getTermPeriod().getTerm();
    }
    
    public default TimePeriod getTimePeriod() {
        return getTermPeriod().getTimePeriod();
    }
}
