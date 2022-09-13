package com.pdgc.avails.structures.calculation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.general.calculation.Availability;

public class AvailabilityResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Availability availability;
	public final Set<Object> resultDetails; //can be as simple as a string, or a custom structure with a bunch of fields. This field is mutable
	
	public AvailabilityResult() {
		availability = Availability.UNSET;
		resultDetails = new HashSet<>();
	}
	
	public AvailabilityResult(
		Availability availability,
		Iterable<? extends Object> resultDetails
	) {
		this.availability = availability;
		this.resultDetails = Sets.newHashSet(resultDetails);
	}
	
	public AvailabilityResult(
		AvailabilityResult availabilityResult
	) {
		this(
			availabilityResult.availability,
			availabilityResult.resultDetails
		);
	}
	
	public static AvailabilityResult combine(
		AvailabilityResult a1,
		AvailabilityResult a2
	) {
	    if (a1 == null) {
	        return a2;
	    }
	    
	    if (a2 == null) {
	        return a1;
	    }
	    
		return new AvailabilityResult(
			AvailabilityHelper.combineAvailability(a1.availability, a2.availability),
			Sets.union(a1.resultDetails, a2.resultDetails)
		);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return availability == ((AvailabilityResult)obj).availability
			&& resultDetails.equals(((AvailabilityResult)obj).resultDetails) ;
	}

	@Override
	public int hashCode() {
		return availability.hashCode()
			^ resultDetails.hashCode();
	}
	
	@Override
	public String toString() {
		return "Availability: " + availability
			+ "Result Details: " + resultDetails;
	}
}
