package com.pdgc.general.structures;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class FoxDealInfo implements Serializable, RightStrandSource {

    private static final long serialVersionUID = 1L;

    private Long dealId;
    private Long businessUnitId;
    private String displaySource;
    private String displaySourceType;
    private String dealName;
    private Media primaryMedia;
    private Territory primaryTerritory;
    private Language primaryLanguage;
    private LocalDate startDate;
    private LocalDate endDate;
    private double licenseFee;
    private String customerName;
    private Long customerId;
    private String startOrderNumber;
    private String salesRefNumber;
    private Long contractId;
    private String currencyCode;
    private Long reservationTypeId;
    private Boolean isExcluded;
    private Long lineOfBusinessId;

    public String getCustomerNameAndId() {
        return customerName + ((customerId != null && customerId != 0L) ? " (" + customerId + ")" : "");
    }

    @Override
    public String toString() {
        return "DealSeq: " + dealId + " Customer: " + customerName + " SO#: " + startOrderNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        FoxDealInfo obj2 = (FoxDealInfo) obj;
        return dealId.equals(obj2.getDealId());

    }

    @Override
    public int hashCode() {
        return dealId.hashCode();
    }
}
