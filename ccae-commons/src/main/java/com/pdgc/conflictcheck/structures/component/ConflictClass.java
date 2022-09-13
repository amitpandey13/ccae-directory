package com.pdgc.conflictcheck.structures.component;

import java.io.Serializable;

/**
 * structure to hold ConflictType and ConflictSeverity, since a conflict is
 * truly identified by the combination of these two rather than the type alone
 * 
 * @author Vishal Raut
 */
public class ConflictClass implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ConflictType conflictType;
	private ConflictSeverity conflictSeverity;

	public ConflictClass(ConflictType conflictType, ConflictSeverity conflictSeverity) {
		this.conflictType = conflictType;
		this.conflictSeverity = conflictSeverity;
	}

	public ConflictType getConflictType() {
		return conflictType;
	}

	public ConflictSeverity getConflictSeverity() {
		return conflictSeverity;
	}

	@Override
	public String toString() {
		return conflictType.toString() + "(" + conflictSeverity.toString() + ")";
	}

}
