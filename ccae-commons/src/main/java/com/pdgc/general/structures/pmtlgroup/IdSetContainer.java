package com.pdgc.general.structures.pmtlgroup;

import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;

/**
 * Relates a PMTLIdSet to a generic object (e.g. RightStrand, Date, etc) 
 *  
 * @author Linda Xu
 *
 * @param <E>
 */
public class IdSetContainer<E extends IdSet> {

	protected E idSet;
	protected Object sourceObject;
	
	public IdSetContainer(
		E idSet,
		Object sourceObject
	) {
		this.idSet = idSet;
		this.sourceObject = sourceObject;
	}
	
	public E getIdSet() {
		return idSet;
	}
	
	public Object getSourceObject() {
		return sourceObject;
	}
}
