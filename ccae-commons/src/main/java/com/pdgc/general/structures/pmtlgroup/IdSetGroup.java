package com.pdgc.general.structures.pmtlgroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;

/**
 * Container for source objects that share PMTLIdSets 
 * 
 * @param <E>
 */
public class IdSetGroup<E extends IdSet> {

	protected Set<E> idSets;
	protected Collection<Object> sourceObjects;
	
	public IdSetGroup(
		Collection<E> idSets,
		Collection<Object> sourceObjects
	) {
		this.idSets = Collections.unmodifiableSet(new HashSet<>(idSets));
		this.sourceObjects = Collections.unmodifiableCollection(sourceObjects);
	}	
	
	public Set<E> getIdSets() {
		return idSets;
	}
	
	public Collection<Object> getSourceObjects() {
		return sourceObjects;
	}
	
	@Override
	public String toString() {
		return "IdSets: " + idSets.toString()
			+ "\nSources: " + sourceObjects.toString();
	}
}
