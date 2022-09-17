package com.pdgc.general.calculation.corporate;

import java.util.Collections;
import java.util.Map;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;

public class FoxConflictCorporateCalculatorResult implements ConflictCorporateCalculatorResult {

    private Map<RightType, Map<TermPeriod, ConflictCorpResult>> availabilityResults;
    
    public FoxConflictCorporateCalculatorResult(
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> availabilityResults
    ) {
        this.availabilityResults = Collections.unmodifiableMap(availabilityResults);
    }
    
    @Override
    public Map<RightType, Map<TermPeriod, ConflictCorpResult>> getAvailabilityResults() {
        return availabilityResults;
    }
}
