package com.pdgc.general.structures.hierarchy;

import java.util.HashSet;
import java.util.Set;

public interface ILeafMap<E> {
    
	Set<E> getLeaves(E element);
	boolean isLeaf(E element);
	Set<E> getAllLeaves();
	
	/**
	 * Converts all the elements to leaves (ie. call getLeaves() on every element in the iterable)
	 * @param elements
	 * @return
	 */
	public default Set<E> convertToLeaves(Iterable<E> items) {
		Set<E> sanitizedLeaves = new HashSet<E>();
		for (E leafItem : items) {
			sanitizedLeaves.addAll(getLeaves(leafItem));
		}
		return sanitizedLeaves;
	}
}
