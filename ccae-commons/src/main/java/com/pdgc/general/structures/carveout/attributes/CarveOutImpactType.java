package com.pdgc.general.structures.carveout.attributes;

/**
 * Enum to describe the two carveOut impact types
 * 
 * Except Against: The tied right strand applies to everybody "except" the customers on list 
 * Only Against:   The tied right strand only applies to specific customer
 * 
 * @author Vishal Raut
 */
public enum CarveOutImpactType {
	EXCEPT_AGAINST(1), ONLY_AGAINST(2);
	
	
	private final int id;

	private CarveOutImpactType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return id;
	}
	
	public static CarveOutImpactType byValue(int value, CarveOutImpactType defaultValue) {
		for (CarveOutImpactType t : CarveOutImpactType.values()) {
			if (t.getValue() == value) {
				return t;
			}
		}
		// value not found be default return
		return defaultValue;
	}	
	
	@Override
	public String toString() {
		switch (this) {
			case EXCEPT_AGAINST:
				return "Allow Customers";
			case ONLY_AGAINST:
				return "Exclude Customers";
			default: 
				throw new IllegalArgumentException("Unknown Carveout Impact Type");
		}
	}
}
