package com.pdgc.general.structures;

public enum DroType {
	FFE(1), 
	FNG(3),
    DISNEY(5);
	
	// Used to determine which DRO a DRO Id maps to
	long droId;

	private DroType(long droId) {
		this.droId = droId;
	}

	public long getDroId() {
		return droId;
	}
	
	public static DroType byValue(long droId) {
		for (DroType t : DroType.values()) {
			if (t.getDroId() == droId) {
				return t;
			}
		}
		throw new IllegalArgumentException(String.format("Invalid value: %d", droId));
	}
}