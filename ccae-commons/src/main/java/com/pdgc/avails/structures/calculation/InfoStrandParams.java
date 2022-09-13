package com.pdgc.avails.structures.calculation;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InfoStrandParams implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public final boolean introduceDateCuts;
    public final boolean affectsCalcDetails;
    public final Set<Object> strandDetails;
    
    public InfoStrandParams(
        boolean introduceDateCuts,
        boolean affectsCalcDetails,
        Set<Object> strandDetails
    ) {
        this.introduceDateCuts = introduceDateCuts;
        this.affectsCalcDetails = affectsCalcDetails;
        this.strandDetails = Collections.unmodifiableSet(strandDetails);
    }
    
    public InfoStrandParams(
        boolean introduceDateCuts,
        boolean affectsCalcDetails
    ) {
        this(
            introduceDateCuts,
            affectsCalcDetails,
            new HashSet<>()
        );
    }
    
    public static InfoStrandParams combine(InfoStrandParams p1, InfoStrandParams p2) {
        boolean introduceDateCuts = p1.introduceDateCuts || p2.introduceDateCuts;
        boolean affectsCalcDetails = p1.affectsCalcDetails || p2.affectsCalcDetails;
        Set<Object> strandDetails = new HashSet<>(p1.strandDetails);
        strandDetails.addAll(p2.strandDetails);
        
        return new InfoStrandParams(
            introduceDateCuts,
            affectsCalcDetails,
            strandDetails
        );
    }
}
