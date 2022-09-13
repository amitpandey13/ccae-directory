package com.pdgc.general.structures.pmtlgroup;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;

public class IdSetFactory <E extends IdSet> {

	private Function<? super E, List<Set<Integer>>> idSetListBuilder;
	private Function<List<Set<Integer>>, E> idSetBuilder;
	
	public IdSetFactory(
		Function<? super E, List<Set<Integer>>> idSetListBuilder,
		Function<List<Set<Integer>>, E> idSetBuilder
	) {
		this.idSetListBuilder = idSetListBuilder;
		this.idSetBuilder = idSetBuilder;
	}
	
	public List<Set<Integer>> buildIdSetList(E idSet) {
		return idSetListBuilder.apply(idSet);
	}
	
	public E buildIdSet(List<Set<Integer>> idSetList) {
		return idSetBuilder.apply(idSetList);
	}
}
