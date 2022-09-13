package com.pdgc.avails.structures.rollup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.rollup.intermediate.SortedCriteriaRequests;
import com.pdgc.avails.structures.rollup.tab.result.LeafSource;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public abstract class RightStrandResultBuilder {

    public abstract RightStrandResult createRightStrandResult(
        Collection<LeafSource> leafSources,
        Map<RightStrand, InfoStrandParams> rightStrands,
        Map<Set<LeafPMTLIdSet>, SortedCriteriaRequests> sortedCriteriaMap,
        Term relevantTerm,
        TimePeriod relevantPeriod
    );
    
    public abstract RightStrandResult mergeRightStrandResults(
        Iterable<RightStrandResult> rsResults
    );
    
    public RightStrandResult mergeRightStrandResults(
        RightStrandResult... rsResults
    ) {
        return mergeRightStrandResults(Arrays.asList(rsResults));
    }
}
