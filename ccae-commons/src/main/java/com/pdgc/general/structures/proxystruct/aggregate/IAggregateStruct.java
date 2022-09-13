package com.pdgc.general.structures.proxystruct.aggregate;

import java.util.Set;

public interface IAggregateStruct<E> {

	public Set<E> getSourceObjects();
	
}
