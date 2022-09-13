package com.pdgc.general.calculation;

/**
 * Basically an enum class to describe different levels of availability. This is made a class rather than an actual enum in order to enforce
 * rules for how to combine different levels of availability together. When combining different availabilities, the one with the greatest
 * severity level takes precedence, so a window of a lower-severity availability cannot override a higher-level severity
 * 
 * @author Vishal Raut
 */
public enum Availability {
	UNSET(0, "No Impact", "U"), 
	YES(1, "Available", "Y"), 
	CONDITIONAL_DEAL(2, "Conditionally Available", "C"), 
	CONDITIONAL_CORPORATE(3, "Conditionally Available", "C"), 
	NO(4, "Unavailable", "N");
	
	
	// Used to determine which availabilities override what others when
	// combining availabilities
	int severity;
	String description; 
	String abbreviation;

	private Availability(int severity, String description, String abbreviation) {
		this.severity = severity;
		this.description = description;
		this.abbreviation = abbreviation; 
	}

	public int getSeverity() {
		return severity;
	}
	
	public String getDescription() {
		return description;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	
	public static Availability byValue(int value) {
		for (Availability t : Availability.values()) {
			if (t.getSeverity() == value) {
				return t;
			}
		}
		throw new IllegalArgumentException(String.format("Invalid value: %d", value));
	}
}