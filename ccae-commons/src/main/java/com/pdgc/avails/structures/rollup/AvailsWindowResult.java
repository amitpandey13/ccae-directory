package com.pdgc.avails.structures.rollup;

import java.io.Serializable;

import com.pdgc.avails.structures.criteria.TimeSpan;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityWindow;

public class AvailsWindowResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AvailabilityWindow availabilityWindow;
    public TimeSpan requestedWindowLength;
    public boolean meetsWindowLength;
    
    
}
