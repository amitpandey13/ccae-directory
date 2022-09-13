package com.pdgc.general.calculation.corporate;

import java.util.Collections;
import java.util.Set;

import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class ConflictCorpResult extends CorpResult {

    private static final long serialVersionUID = 1L;
    
    /**
     * These strands will always be a subset of the strands in rightStrandImpacts.
     * If nearest right strands are in play, for example, we may not consider non-nearest strands as 'relevant',
     * which means they become unable to throw conflicts regardless of what's in the conflict matrix
     */
    private Set<RightStrand> conflictRightStrands;
    
    public ConflictCorpResult(
        AvailabilityResult availabilityResult,
        Set<? extends RightStrand> conflictRightStrands
    ) {
        super(availabilityResult);
        this.conflictRightStrands = Collections.unmodifiableSet(conflictRightStrands);
    }
    
    public Set<RightStrand> getConflictRightStrands() {
        return conflictRightStrands;
    }
}
