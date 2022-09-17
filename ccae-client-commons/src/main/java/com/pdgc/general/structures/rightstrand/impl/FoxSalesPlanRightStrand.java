package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class FoxSalesPlanRightStrand extends FoxRightStrandBase implements SalesPlanRightStrand {

    private static final long serialVersionUID = 1L;    

    private Long statusId;
    private Customer customer;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FoxSalesPlanRightStrand(
        long rightStrandId,
        PMTL pmtl,
        Term term,
        FoxRightSource rightSource,
        RightType rightType,
        PMTL actualPMTL,
        Term origTerm,
        String comment,
        boolean checkedIn,
        Long productHierarchyId,
        Long distributionRightsOwner,
        Customer customer,
        Long statusId
    ) {
        super(
            rightStrandId,
            pmtl,
            new TermPeriod(term, TimePeriod.FULL_WEEK),
            rightSource,
            rightType,
            actualPMTL,
            origTerm,
            comment,
            checkedIn,
            productHierarchyId,
            distributionRightsOwner
        );
        
        this.customer = customer;
        this.statusId = statusId;
    }
    
    @Override
    public Customer getCustomer() {
        return customer;
    }
    
    @Override
    public Long getSalesWindowId() {
        return this.getRightSource().getSourceId();
    }

    @Override
    public Long getStatusId() {
        return statusId;
    }
}
