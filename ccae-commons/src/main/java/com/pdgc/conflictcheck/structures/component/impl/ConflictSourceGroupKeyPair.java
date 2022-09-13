package com.pdgc.conflictcheck.structures.component.impl;

/**
 * This object's purpose is so we could begin phasing out the Pairs we have. This allows more flexibility
 * 
 * @author Thomas LOh
 *
 */
public class ConflictSourceGroupKeyPair {
	
	private ConflictSourceGroupKey primary;
	private ConflictSourceGroupKey secondary;
	
	public ConflictSourceGroupKeyPair() {}
	
	public ConflictSourceGroupKeyPair(ConflictSourceGroupKey primary, ConflictSourceGroupKey secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	public ConflictSourceGroupKey getPrimary() {
		return primary;
	}

	public void setPrimary(ConflictSourceGroupKey primary) {
		this.primary = primary;
	}

	public ConflictSourceGroupKey getSecondary() {
		return secondary;
	}

	public void setSecondary(ConflictSourceGroupKey secondary) {
		this.secondary = secondary;
	}
}
