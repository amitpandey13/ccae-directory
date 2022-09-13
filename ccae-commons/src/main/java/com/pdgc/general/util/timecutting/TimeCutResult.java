package com.pdgc.general.util.timecutting;

import com.google.common.collect.ImmutableSet;

public class TimeCutResult<E> {

	private ImmutableSet<E> removedEntries;
	private ImmutableSet<ReplacementTimeEntry<E>> replacementEntries;
	
	public TimeCutResult(
		Iterable<E> removedEntries,
		Iterable<ReplacementTimeEntry<E>> replacementEntries
	) {
		this.removedEntries = ImmutableSet.copyOf(removedEntries);
		this.replacementEntries = ImmutableSet.copyOf(replacementEntries);
	}
	
	public ImmutableSet<E> getRemovedEntries() {
		return removedEntries;
	}
	
	public ImmutableSet<ReplacementTimeEntry<E>> getReplacementEntries() {
		return replacementEntries;
	}
}
