package com.pdgc.avails.structures.rollup.intermediate;

import java.io.Serializable;
import java.util.Objects;

import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class AvailabilityWindow implements Serializable {

    private static final long serialVersionUID = 1L;

    public Availability availability;
    public Term windowTerm;
    public TimePeriod windowPeriod;
    
    public AvailabilityWindow() {
        availability = null;
        windowTerm = null;
        windowPeriod = null;
    }
    
    public AvailabilityWindow(AvailabilityWindow a2) {
        availability = a2.availability;
        windowTerm = a2.windowTerm;
        windowPeriod = a2.windowPeriod;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        
        return availability.equals(((AvailabilityWindow)obj).availability)
            && Objects.equals(windowTerm, ((AvailabilityWindow)obj).windowTerm)
            && Objects.equals(windowPeriod, ((AvailabilityWindow)obj).windowPeriod)
        ;
    }
    
    @Override
    public int hashCode() {
        return availability.hashCode()
            ^ Objects.hashCode(windowTerm)
            ^ Objects.hashCode(windowPeriod)
        ;
    }
    
    @Override
    public String toString() {
        return windowTerm + ": " + availability;
    }
}
