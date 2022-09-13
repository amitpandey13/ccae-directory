package com.pdgc.conflictcheck.service;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Equivalence;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyContainerEquivalence;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLTimeIgnorant;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

public class OverrideApplier {

	private Equivalence<? super ConflictSourceGroupKey> overrideGroupKeyEquivalence; //defines the equals() method to be used when applying overrides to conflicts
	
	public OverrideApplier(
		Equivalence<? super ConflictSourceGroupKey> overrideGroupKeyEquivalence //defines the equals() method to be used when applying overrides to conflicts
	) {
		if (overrideGroupKeyEquivalence == null) {
       		overrideGroupKeyEquivalence = Equivalence.equals();
       	}
       	this.overrideGroupKeyEquivalence = overrideGroupKeyEquivalence;
	}
	
	/**
     * Applies the existing conflict overrides to the new conflicts and updates the conflict statuses 
     * @param newLeafConflicts
     * @param existingOverrides
     * @param groupKeyEquivalence - defines what kind of equals() should be used when comparing the conflictSourceGroupKeys
     */
    public void applyOverrides(    		
        Collection<? extends Conflict> newLeafConflicts,
        Collection<ConflictOverride> existingOverrides
    ) {
    	if (CollectionsUtil.isNullOrEmpty(existingOverrides)) {
    		return;
    	}
    	
        EquivalenceMap<IConflictKeyContainer, Collection<ConflictOverride>> sortedOverrides = groupConflictOverridesIgnorePMTLTime(existingOverrides);
    	
    	for (Conflict conflict : newLeafConflicts) {
    		Collection<ConflictOverride> potentialOverrides = sortedOverrides.get(conflict);
    		
    		if (potentialOverrides == null) {
    			continue;
    		}
    		
    		Collection<ConflictOverride> applicableOverrides = CollectionsUtil.where(
    			potentialOverrides, 
    			o -> Term.hasIntersection(o.getTerm(), conflict.getTerm()) 
    				&& TimePeriod.hasIntersection(o.getTimePeriod(), conflict.getTimePeriod())
    		);
    		
    		for (ConflictOverride override : applicableOverrides) {
    			Term intersectionTerm = Term.getIntersectionTerm(conflict.getTerm(), override.getTerm());
    			TimePeriod intersectionPeriod = TimePeriod.intersectPeriods(conflict.getTimePeriod(), override.getTimePeriod());
    			
    			if (intersectionTerm.equals(conflict.getTerm()) && intersectionPeriod.equals(conflict.getTimePeriod())) {
    				conflict.addConflictOverride(override, ConflictStatus.RESOLVED);
    			}
    			else {
    				conflict.addConflictOverride(override, ConflictStatus.NEEDS_ACK);
    			}
    		}
    	}
    }
	
	private EquivalenceMap<IConflictKeyContainer, Collection<ConflictOverride>> groupConflictOverridesIgnorePMTLTime(
    	Collection<ConflictOverride> conflictOverrides
    ) {
    	EquivalenceMap<IConflictKeyContainer, Collection<ConflictOverride>> groupedOverrides = 
    		new EquivalenceMap<IConflictKeyContainer, Collection<ConflictOverride>>(new ConflictKeyContainerEquivalence(new ConflictKeyEquivalencePMTLTimeIgnorant(overrideGroupKeyEquivalence)));
        
    	for (ConflictOverride override : conflictOverrides) {
    		Collection<ConflictOverride> sameKeyItems = groupedOverrides.get(override);
    		if (sameKeyItems == null) {
    			sameKeyItems = new HashSet<ConflictOverride>();
    			groupedOverrides.put(override,  sameKeyItems);
    		}
    		sameKeyItems.add(override);
    	}
		
		return groupedOverrides;
    }
	
	public Equivalence<? super ConflictSourceGroupKey> getOverrideEquivalence() {
    	return overrideGroupKeyEquivalence;
    }
}
