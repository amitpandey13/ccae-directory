package com.pdgc.conflictcheck.structures.component;

import java.io.Serializable;

/**
 * Conflict Severity Definitions
 * 
 * <p>
 * 1. Fatal - This severity indicates that the resulting conflict will cause severe problems.
 * 2. Warning - This severity indicates that the resulting conflict 
 * 3. Info - This severity indicates there's no actual conflict but still need to be considered. Examples (HB - HB), (NX - NX)
 * 4. None - There is no conflict here
 * <p>
 * @author Thomas Loh
 *
 */
public enum ConflictSeverity implements Serializable {
	FATAL(41, 3), WARNING(42, 2), INFO(43, 1), NONE(44, 0);
	
	private final int id;
	
	private final int severityLevel; //Similar to the Availability severity, in the sense that values with greature severity will override those with lesser
	
	private ConflictSeverity(int id, int severityLevel) {
		this.id = id;
		this.severityLevel = severityLevel;
	}

	public int getId() {
		return id;
	}
	
	public int getSeverityLevel() {
		return severityLevel;
	}

	public int getValue() {
		return id;
	}
	
	public static ConflictSeverity byValue(int value) {
		for (ConflictSeverity t : ConflictSeverity.values()) {
			if (t.getValue() == value) {
				return t;
			}
		}
		System.err.println("Invalid conflictSeverity ID: " + value);
		return null;
	}
}