package com.pdgc.general.structures.pmtlgroup;

import java.util.Collections;
import java.util.Set;

public class DimensionSplitResult {
	
	private Set<Integer> intersection;		//elements found in both
	private Set<Integer> origComplement;	//elements found only in the orig set
	private Set<Integer> newComplement;		//elements found only in the new set
	
	public DimensionSplitResult(
		Set<Integer> intersection,
		Set<Integer> origComplement,
		Set<Integer> newComplement
	) {
		this.intersection = Collections.unmodifiableSet(intersection);
		this.origComplement = Collections.unmodifiableSet(origComplement);
		this.newComplement = Collections.unmodifiableSet(newComplement);
	}
	
	public Set<Integer> getIntersection() {
		return intersection;
	}
	
	public Set<Integer> getOrigComplement() {
		return origComplement;
	}
	
	public Set<Integer> getNewComplement() {
		return newComplement;
	}
}
