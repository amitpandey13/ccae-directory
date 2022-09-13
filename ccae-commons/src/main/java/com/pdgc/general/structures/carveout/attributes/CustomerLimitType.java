package com.pdgc.general.structures.carveout.attributes;

public enum CustomerLimitType {
	INCLUDES_INTERNAL_BRANDED(1),
	IN_ADDITION_TO_INTERNAL_BRANDED(2),
	INTERNAL_BRANDED_NOT_APPLICABLE(3);
	
	private int value;
	CustomerLimitType(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
	public static  CustomerLimitType byValue(int value, CustomerLimitType defaultValue) {
		for (CustomerLimitType t :  CustomerLimitType.values()) {
			if (t.getValue() == value) {
				return t;
			}
		}
		// value not found be default return
		return defaultValue;
	}
	
}
