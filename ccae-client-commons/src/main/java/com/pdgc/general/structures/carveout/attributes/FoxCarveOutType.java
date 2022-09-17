package com.pdgc.general.structures.carveout.attributes;

/**
 * enum for the different carveOut types These are hard-coded rather than
 * dynamically generated, since the rules for carveOut analysis have to be
 * hard-coded as well
 * 
 * @author Vishal Raut
 *
 */
public enum FoxCarveOutType {
	
	CUSTOMERS(1), 
	CUSTOMER_TYPES(2), 
	CUSTOMER_GENRES(3), 
	MAX_CUSTOMERS(4), 
	OTHER(5);

	private final int id;

	private FoxCarveOutType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static FoxCarveOutType byId(int id) {
		for (FoxCarveOutType t : FoxCarveOutType.values()) {
			if (t.getId() == id) {
				return t;
			}
		}
		
		return FoxCarveOutType.OTHER;
	}
}