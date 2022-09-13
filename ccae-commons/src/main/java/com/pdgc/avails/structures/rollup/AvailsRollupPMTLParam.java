package com.pdgc.avails.structures.rollup;

import java.util.Map;

import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.rollup.intermediate.LeafAvailsResultMeta;
import com.pdgc.avails.structures.rollup.intermediate.SortedCriteriaRequests;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class AvailsRollupPMTLParam {

    public SortedCriteriaRequests criteriaRequests;
    public Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>> calcResults;
    public Map<RightStrand, InfoStrandParams> additionalStrandDetails;
    
    public AvailsRollupPMTLParam(
        SortedCriteriaRequests criteriaRequests,
        Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>> calcResults,
        Map<RightStrand, InfoStrandParams> additionalStrandDetails
    ) {
        this.criteriaRequests = criteriaRequests;
        this.calcResults = calcResults;
        this.additionalStrandDetails = additionalStrandDetails;
    }
}
