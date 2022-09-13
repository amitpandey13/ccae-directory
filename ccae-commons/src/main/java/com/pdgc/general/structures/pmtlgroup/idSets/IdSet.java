package com.pdgc.general.structures.pmtlgroup.idSets;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class IdSet implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected List<Set<Integer>> idSetList;
	private int hashCode;
	
	public IdSet(List<Set<Integer>> idSets) {
		this.idSetList = Collections.unmodifiableList(idSets);
		
		this.hashCode = 5;
		for (Set<Integer> idSet : idSets) {
			hashCode = 31 * hashCode + (idSet == null ? 0 : idSet.hashCode());
		}
	}
	
	/**
	 * Returns true if any of the dimensions is empty
	 * @return
	 */
	public boolean hasEmptyDimension() {
	    for (Set<Integer> dimension : idSetList) {
	        if (dimension.isEmpty()) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Returns true if all the dimensions are empty
	 * @return
	 */
	public boolean isEmpty() {
	    for (Set<Integer> dimension : idSetList) {
            if (!dimension.isEmpty()) {
                return false;
            }
        }
	    return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		if (idSetList.size() != ((IdSet)obj).idSetList.size()) {
			return false;
		}
		
		for (int i=0; i<idSetList.size(); i++) {
			if (!Objects.equals(idSetList.get(i), ((IdSet)obj).idSetList.get(i))) {
				return false;
			}
        }
		
		return true;
	}
	
	@Override
	public final int hashCode() {
		return hashCode;
	}
	
	public List<Set<Integer>> getIdSetList() {
		return idSetList;
	}
	
	public int getNumDimensions() {
		return idSetList.size();
	}
}
