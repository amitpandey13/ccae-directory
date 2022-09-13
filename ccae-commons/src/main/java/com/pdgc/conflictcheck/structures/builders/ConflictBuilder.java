package com.pdgc.conflictcheck.structures.builders;

import java.util.Collection;

import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public interface ConflictBuilder<E extends Conflict> {

	public E buildConflict(
		ConflictClass conflictClass,
		RightStrand primaryRightStrand,
		RightStrand conflictingRightStrand,
		PMTL pmtl,
        Term term,
        TimePeriod timePeriod,
        Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass
	);
	
	public E buildRolledConflict(
		E templateConflict,
		PMTL pmtl,
		Term term,
		TimePeriod timePeriod,
		Collection<E> sourceConflicts
	);
	
	public E cloneConflictWithNewPMTL(
		E templateConflict,
		PMTL pmtl
	);
}
