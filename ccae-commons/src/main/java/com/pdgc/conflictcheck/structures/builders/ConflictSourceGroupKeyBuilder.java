package com.pdgc.conflictcheck.structures.builders;

import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceGroupKey;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface ConflictSourceGroupKeyBuilder {

	public ConflictSourceGroupKey getConflictSourceGroupKey(RightStrand rightStrand);
	
	public ConflictSourceGroupKey getNullConflictSourceGroupKey();
}
