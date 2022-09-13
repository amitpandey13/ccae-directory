package com.pdgc.avails.helpers;

import java.util.Arrays;
import java.util.Comparator;

import com.pdgc.general.calculation.Availability;

public abstract class AvailabilityHelper {

	/*
	 * Lets return the description given the availability of the object
	 */

	public static Availability[] getAvailabilityInDescendingOrder() {
	    Availability[] availabilities =  Availability.values();
	    Comparator<Availability> availabilityComparator = new Comparator<Availability>() {
	    	@Override
	    	public int compare(Availability one, Availability two) {
	    		return two.getSeverity()-one.getSeverity();
	    	}
	    };
	    Arrays.sort(availabilities, availabilityComparator);
	    return availabilities;
	}

	/**
	 * Combines 2 different availabilities together. The one with the higher severity takes precedence.
	 * This will never return null - nulls are considered to be UNSET,
	 * and if both sides are null, this will return UNSET
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static Availability combineAvailability(Availability left, Availability right) {
		if (left == null && right == null) {
			return Availability.UNSET;
		}
		
		if (left == null) {
			return right;
		}
		
		if (right == null) {
			return left;
		}
		
		return left.getSeverity() > right.getSeverity() ? left : right;
	}
}
