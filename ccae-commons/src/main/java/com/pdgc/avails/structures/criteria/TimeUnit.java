package com.pdgc.avails.structures.criteria;

public enum TimeUnit {
	DAYS(1, "day"), WEEKS(2, "week"), MONTHS(3, "month"), YEARS(4, "year");

	private final int id;
	private final String description;

	TimeUnit(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getValue() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

	public static TimeUnit byValue(int value) {
		return byValue(value, TimeUnit.MONTHS);
	}

	public static TimeUnit byValue(int value, TimeUnit defaultValue) {
		for (TimeUnit t : TimeUnit.values()) {
			if (t.getValue() == value) {
				return t;
			}
		}
		// value not found be default return
		return defaultValue;
	}
}