package com.pdgc.general.lookup.maps;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public abstract class ConflictMatrix {

	public abstract ConflictClass getConflictType(RightStrand primaryRightStrand, RightStrand conflictingRightStrand);
	
}
