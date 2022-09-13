package com.pdgc.general.structures.pmtlgroup.idSets;

import java.util.List;
import java.util.Set;

public class TestIdSet extends IdSet {
	
	private static final long serialVersionUID = 1L;

	public TestIdSet(
		List<Set<Integer>> idSets
	) {
		super(idSets);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		if (this.getNumDimensions() != ((TestIdSet)obj).getNumDimensions()) {
			return false;
		}
		
		for (int i=0; i<getNumDimensions(); i++) {
			if (!idSetList.get(i).equals(((TestIdSet)obj).idSetList.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
