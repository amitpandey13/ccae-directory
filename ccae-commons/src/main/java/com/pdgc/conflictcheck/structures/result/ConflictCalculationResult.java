package com.pdgc.conflictcheck.structures.result;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.conflictcheck.structures.Conflict;

public class ConflictCalculationResult<E extends Conflict> {

    private Set<E> primaryLeafConflicts;
    private Set<E> siblingLeafConflicts;
    private Set<E> corporateLeafConflicts;
    
    public ConflictCalculationResult(
    	Collection<E> primaryLeafConflicts,
    	Collection<E> siblingLeafConflicts,
    	Collection<E> corporateLeafConflicts
    ) {
    	this.primaryLeafConflicts = ImmutableSet.copyOf(primaryLeafConflicts);
    	this.siblingLeafConflicts = ImmutableSet.copyOf(siblingLeafConflicts);
    	this.corporateLeafConflicts = ImmutableSet.copyOf(corporateLeafConflicts);
    }
    
    public Set<E> getPrimaryLeafConflicts() {
    	return primaryLeafConflicts;
    }
    
    public Set<E> getSiblingLeafConflicts() {
    	return siblingLeafConflicts;
    }
    
    public Set<E> getCorporateLeafConflicts() {
    	return corporateLeafConflicts;
    }
}
