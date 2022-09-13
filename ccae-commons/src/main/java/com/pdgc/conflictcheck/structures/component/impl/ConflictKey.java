package com.pdgc.conflictcheck.structures.component.impl;

import java.io.Serializable;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.ConflictType;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public abstract class ConflictKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected ConflictClass conflictClass;
	protected ConflictSourceGroupKey primaryConflictSourceGroupKey;
	protected ConflictSourceGroupKey conflictingConflictSourceGroupKey;
	protected PMTL pmtl;
	protected Term term;
	protected TimePeriod timePeriod;

	protected ConflictKey() {}
	
	public ConflictKey(
		ConflictClass conflictClass,
        ConflictSourceGroupKey primaryConflictSourceGroupKey,
        ConflictSourceGroupKey conflictingConflictSourceGroupKey,
        PMTL pmtl,
        Long productHierarchyId,
        Term term,
        TimePeriod timePeriod
	){
		this.conflictClass = conflictClass;
        this.primaryConflictSourceGroupKey = primaryConflictSourceGroupKey;
        this.conflictingConflictSourceGroupKey = conflictingConflictSourceGroupKey;
        this.pmtl = pmtl;
        this.term = term;
        this.timePeriod = timePeriod;
	}

	public ConflictKey(ConflictKey key)  {
		conflictClass = key.getConflictClass();
        primaryConflictSourceGroupKey = key.getPrimaryConflictSourceGroupKey();
        conflictingConflictSourceGroupKey = key.getConflictingConflictSourceGroupKey();
        pmtl = key.getPMTL();
        term = key.getTerm();
        timePeriod =key.getTimePeriod();
	}

	@Override
	public String toString() {
		try {
			StringBuilder conflictString = new StringBuilder();
			conflictString.append((conflictClass.getConflictType().getConflictDesc() + " on " + pmtl) + System.getProperty("line.separator"));
			conflictString.append((term.toString()) + System.getProperty("line.separator"));
			return conflictString.toString();
		} catch (RuntimeException dummyCatchVar0) {
			throw dummyCatchVar0;
		} catch (Exception dummyCatchVar0) {
			throw new RuntimeException(dummyCatchVar0);
		}
	}
	
	public ConflictKey getConflictKey() {
		return this;
	}
	
	public ConflictClass getConflictClass() {
		return conflictClass;
	}

	public ConflictType getConflictType() {
		return conflictClass.getConflictType();
	}

	public ConflictSeverity getConflictSeverity() {
		return conflictClass.getConflictSeverity();
	}
	
	public ConflictSourceGroupKey getPrimaryConflictSourceGroupKey() {
		return primaryConflictSourceGroupKey;
	}
	
	public RightSource getPrimaryRightSource() {
		return primaryConflictSourceGroupKey.getRightSource();
	}

	public RightType getPrimaryRightType() {
		return primaryConflictSourceGroupKey.getRightType();
	}

	public ConflictSourceGroupKey getConflictingConflictSourceGroupKey() {
		return conflictingConflictSourceGroupKey;
	}
	
	public RightSource getConflictingRightSource() {
		if (conflictingConflictSourceGroupKey == null) {
			return null;
		}
		return conflictingConflictSourceGroupKey.getRightSource();
	}

	public RightType getConflictingRightType() {
		if (conflictingConflictSourceGroupKey == null) {
			return null;
		}
		return conflictingConflictSourceGroupKey.getRightType();
	}

	public PMTL getPMTL() {
		return pmtl;
	}

	public Term getTerm() {
		return term;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}
}
