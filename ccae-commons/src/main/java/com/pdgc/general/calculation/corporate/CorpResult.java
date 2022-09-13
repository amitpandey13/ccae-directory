package com.pdgc.general.calculation.corporate;

import java.io.Serializable;

import com.pdgc.avails.structures.calculation.AvailabilityResult;

public abstract class CorpResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * final calculated availability
     */
    protected AvailabilityResult availabilityResult;
    
    public CorpResult(
        AvailabilityResult availabilityResult
    ) {
        this.availabilityResult = availabilityResult;
    }
    
    public AvailabilityResult getAvailabilityResult() {
        return availabilityResult;
    }
}
