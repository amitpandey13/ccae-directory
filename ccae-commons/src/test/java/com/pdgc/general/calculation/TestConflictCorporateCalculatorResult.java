package com.pdgc.general.calculation;

import java.util.Collections;
import java.util.Map;

import com.pdgc.general.calculation.corporate.ConflictCorpResult;
import com.pdgc.general.calculation.corporate.ConflictCorporateCalculatorResult;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;

public class TestConflictCorporateCalculatorResult implements ConflictCorporateCalculatorResult {

    private Map<RightType, Map<TermPeriod, ConflictCorpResult>> availabilityResults;
    
    public TestConflictCorporateCalculatorResult(
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> availabilityResults
    ) {
        this.availabilityResults = Collections.unmodifiableMap(availabilityResults);
    }
    
    @Override
    public Map<RightType, Map<TermPeriod, ConflictCorpResult>> getAvailabilityResults() {
        return availabilityResults;
    }
}
