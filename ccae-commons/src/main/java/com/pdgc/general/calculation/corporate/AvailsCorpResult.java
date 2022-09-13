package com.pdgc.general.calculation.corporate;

import java.util.Collections;
import java.util.Map;

import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class AvailsCorpResult extends CorpResult {

    private static final long serialVersionUID = 1L;
    
    /**
     * Each input right strand's impact on the final availability 
     */
    private Map<RightStrand, AvailabilityResult> rightStrandImpacts;
    
    public AvailsCorpResult(
        AvailabilityResult availabilityResult,
        Map<RightStrand, AvailabilityResult> rightStrandImpacts
    ) {
        super(availabilityResult);
        this.rightStrandImpacts = Collections.unmodifiableMap(rightStrandImpacts);
    }
    
    public Map<RightStrand, AvailabilityResult> getRightStrandImpacts() {
        return rightStrandImpacts;
    }
}
