package com.pdgc.general.structures.rightstrand.impl;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.DealProduct;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.util.CollectionsUtil;

import lombok.Builder;

public class FoxDealRightStrand extends FoxRightStrandBase implements FoxDealStrand {

    private static final long serialVersionUID = 1L;

    private FoxCarveOutContainer carveOuts;
    private Set<TimePeriodPart> timePeriodParts;
    private Customer customer;
    private Long statusId;
    private DealProduct dealProduct;
    private String contractType;

    @Builder
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public FoxDealRightStrand(
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
        FoxCarveOutContainer carveOuts,
        Iterable<TimePeriodPart> timePeriodParts,
        Long statusId,
        DealProduct dealProduct,
        String contractType
    ) {
        super(
            rightStrandId,
            pmtl,
            new TermPeriod(
                term,
                !CollectionsUtil.isNullOrEmpty(timePeriodParts) ? new TimePeriod(timePeriodParts) : TimePeriod.FULL_WEEK
            ),
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
        this.carveOuts = carveOuts;

        if (!CollectionsUtil.isNullOrEmpty(timePeriodParts)) {
            this.timePeriodParts = ImmutableSet.copyOf(timePeriodParts);
        } else {
            this.timePeriodParts = new HashSet<>();
        }

        this.statusId = statusId;
        this.dealProduct = dealProduct;
        this.contractType = contractType;
    }

    @Override
    public Long getDealId() {
        return getRightSource().getSourceId();
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }
    
    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    @Override
    public FoxCarveOutContainer getCarveOuts() {
        return carveOuts;
    }

    public Set<TimePeriodPart> getOrigTimePeriods() {
        return timePeriodParts;
    }

    @Override
    public Long getStatusId() {
        return statusId;
    }

    public DealProduct getDealProduct() {
        return dealProduct;
    }
}
