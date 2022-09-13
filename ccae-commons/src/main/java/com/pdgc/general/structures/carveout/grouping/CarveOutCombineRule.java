package com.pdgc.general.structures.carveout.grouping;

/**
 * enum defining whether 2 carveouts should be combined using AND or OR.
 * 
 * For now, these rules are neglible because we only ever use AND, but this enum
 * was created exists here in case future requirements specify more complex
 * rules
 * 
 * @author Vishal Raut
 */
public enum CarveOutCombineRule {
	AND(1), OR(2);
	
	private final int id;

	private CarveOutCombineRule(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return id;
	}
	
	public static CarveOutCombineRule byValue(int value, CarveOutCombineRule defaultValue) {
		for (CarveOutCombineRule t : CarveOutCombineRule.values()) {
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
			case AND:
				return "AND";
			case OR:
				return "OR";
			default: 
				throw new IllegalArgumentException("Unknown Carveout Combine Rule");
		}
	}
}