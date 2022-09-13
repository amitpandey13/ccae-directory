package com.pdgc.general.structures.pmtlgroup;

import java.util.Collection;
import java.util.Collections;

public class IdSetSplitResult <E> {
	
	private E intersection; //elements found in both
	private Collection<E> origComplement; //elements found only in the orig set
	
	public IdSetSplitResult (
		E intersection,
		Collection<? extends E> origComplement
	) {
		this.intersection = intersection;
		this.origComplement = Collections.unmodifiableCollection(origComplement);
	}
	
	public E getIntersection() {
		return intersection;
	}
	
	public Collection<E> getOrigComplement() {
		return origComplement;
	}
}
