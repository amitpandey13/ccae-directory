package com.pdgc.general.structures.proxystruct.aggregate.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.proxystruct.DummyTerritory;
import com.pdgc.general.structures.proxystruct.aggregate.IAggregateStruct;

public class AggregateTerritory extends DummyTerritory implements IAggregateStruct<Territory> {
	
	private static final long serialVersionUID = 1L;
	
	protected ImmutableSet<Territory> sourceTerritories;
	
	public AggregateTerritory(Iterable<Territory> territories) {
		super();
		this.sourceTerritories = ImmutableSet.copyOf(territories);
		setCustomName(sourceTerritories.toString());
	}
	
	public AggregateTerritory(Territory...territories) {
		super();
		this.sourceTerritories = ImmutableSet.copyOf(territories);
		setCustomName(sourceTerritories.toString());
	}
	
	@Override
	public Set<Territory> getSourceObjects() {
		return sourceTerritories;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return sourceTerritories.equals(((AggregateTerritory)obj).sourceTerritories);
	}
	
	@Override
	public int hashCode() {
		return sourceTerritories.hashCode();
	}
}
