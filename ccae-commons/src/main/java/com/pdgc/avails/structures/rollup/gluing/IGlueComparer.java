package com.pdgc.avails.structures.rollup.gluing;

public interface IGlueComparer<E> {

	/**
	 * Compares result1 and result2, and if they are 'equivalent', merges them
	 * If they are not 'equivalent', then return null
	 * @param result1
	 * @param result2
	 * @return
	 */
	public E compareAndMerge(E result1, E result2);
	
}
