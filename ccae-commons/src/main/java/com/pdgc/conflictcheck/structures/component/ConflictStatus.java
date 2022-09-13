package com.pdgc.conflictcheck.structures.component;

import java.io.Serializable;

public enum ConflictStatus implements Serializable
{
    DEFAULT(1),
    NEEDS_ACK(2),
    RESOLVED(3),
	PENDING(4);
	
	private final int id;
	
	private ConflictStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public int getValue() {
		return id;
	}

	public static ConflictStatus byValue(int value, ConflictStatus defaultValue) {
		for (ConflictStatus t : ConflictStatus.values()) {
			if (t.getValue() == value) {
				return t;
			}
		}
		// value not found be default return
		return defaultValue;
	}
	
}
