package com.pdgc.avails.structures.rollup.intermediate;

import java.io.Serializable;

import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.Term;

public class SecondaryAvailabilityResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public final Availability netAvailability;
    public final Term relevantTerm;
    
    public SecondaryAvailabilityResult(
        Availability netAvailability,
        Term relevantTerm
    ) {
        this.netAvailability = netAvailability;
        this.relevantTerm = relevantTerm;
    }
    
    public boolean isPassing() {
        return netAvailability != Availability.NO;
    }
}
