package com.pdgc.conflictcheck.structures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.util.CollectionsUtil;

/**
 * This looks to be a replacement for any LeafConflict Objects
 * 
 * 
 * @author Thomas Loh
 *
 */
public abstract class Conflict implements IConflictKeyContainer, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected ConflictKey conflictKey;
	protected Set<ConflictOverride> conflictOverrides;
	protected ConflictStatus conflictStatus;
	protected ConflictClass siblingConflictClass; //The type/severity of the bidirectional conflict that will be produced		
	
	protected boolean primaryCheckedIn;
	protected boolean conflictingCheckedIn;

	protected Conflict() {}
	
	public Conflict(
		ConflictKey conflictKey,
		Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass,
        boolean primaryCheckedIn,
        boolean conflictingCheckedIn
	) {
		this.conflictKey = conflictKey;
		
		if (CollectionsUtil.isNullOrEmpty(conflictOverrides)) {
            this.conflictOverrides = new HashSet<ConflictOverride>();
        }
        else {
            this.conflictOverrides = new HashSet<ConflictOverride>(conflictOverrides);
        }

        this.conflictStatus = conflictStatus;
        this.siblingConflictClass = siblingConflictClass;
		this.primaryCheckedIn = primaryCheckedIn;
		this.conflictingCheckedIn = conflictingCheckedIn;
	}

	public void addConflictOverride(ConflictOverride conflictOverride, ConflictStatus conflictStatus) {
        if (conflictOverrides == null) {
            conflictOverrides = new HashSet<ConflictOverride>();
        }

       conflictOverrides.add(conflictOverride); 

        if (this.conflictStatus != ConflictStatus.RESOLVED && conflictStatus != ConflictStatus.DEFAULT) {
            this.conflictStatus = conflictStatus;
        }
    }
	
	public Set<ConflictOverride> getConflictOverrides() {
		return Collections.unmodifiableSet(conflictOverrides);
	}
	
    public ConflictStatus getConflictStatus() {
    	return conflictStatus;
    }
    
	public ConflictClass getSiblingConflictClass() {
		return siblingConflictClass;
	}

	@Override
	public String toString() {
		StringBuilder conflictString = new StringBuilder();
		conflictString.append(conflictKey.getConflictType().getConflictDesc() + " | ")
		.append(conflictKey.getPMTL())
		.append(" : ").append(conflictKey.getTerm().toString())
		.append(" | ")
		.append("WC: ")
		.append(conflictKey.getPrimaryRightSource())
		.append("/").append(conflictKey.getPrimaryRightType());

		//Rights-in conflicts wont have a conflicting side so check for null.
		if (conflictKey.getConflictingRightSource() != null) {
			conflictString.append(" CONFLICT: ")
			.append(conflictKey.getConflictingRightSource())
			.append("/").append(conflictKey.getConflictingRightType());
		}
		return conflictString.toString();
	}
	
	public boolean isPrimaryCheckedIn() {
		return primaryCheckedIn;
	}
	
	public boolean isConflictingCheckedIn() {
		return conflictingCheckedIn;
	}
	
	@Override
	public ConflictKey getConflictKey() {
    	return conflictKey;
    }
}
