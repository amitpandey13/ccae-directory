package com.pdgc.general.calculation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pdgc.general.calculation.corporate.AvailsCorpResult;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class TestAvailsCorporateCalculatorResult implements AvailsCorporateCalculatorResult {

    private static final long serialVersionUID = 1L;
    
    private Map<RightType, Map<TermPeriod, AvailsCorpResult>> availabilityResults;
    
    public TestAvailsCorporateCalculatorResult(
        Map<RightType, Map<TermPeriod, AvailsCorpResult>> availabilityResults
    ) {
        this.availabilityResults = Collections.unmodifiableMap(availabilityResults);
    }
    
    @Override
    public Map<RightType, Map<TermPeriod, AvailsCorpResult>> getAvailabilityResults() {
        return availabilityResults;
    }
    
    @Override
    public Map<RightStrand, ? extends Collection<? extends Object>> getNonCuttingRights() {
        return new HashMap<>();
    }
}
