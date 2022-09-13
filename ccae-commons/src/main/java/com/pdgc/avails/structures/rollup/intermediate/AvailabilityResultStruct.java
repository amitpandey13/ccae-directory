package com.pdgc.avails.structures.rollup.intermediate;

import java.io.Serializable;

import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.Availability;

public class AvailabilityResultStruct implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Availability netAvailability;
	private AvailabilityResult corpAvailabilityResult;
	private AvailabilityResult nonCorpAvailabilityResult;
	
	public AvailabilityResultStruct() {
		netAvailability = null;
		this.corpAvailabilityResult = new AvailabilityResult();
		this.nonCorpAvailabilityResult = new AvailabilityResult();
	}
	
	public AvailabilityResultStruct(
		AvailabilityResult corpAvailability,
		AvailabilityResult nonCorpAvailability
	) {
		netAvailability = null;
		this.corpAvailabilityResult = corpAvailability;
		this.nonCorpAvailabilityResult = nonCorpAvailability;
	}

	public AvailabilityResultStruct(AvailabilityResultStruct a2) {
		this.netAvailability = a2.netAvailability;
		this.corpAvailabilityResult = new AvailabilityResult(a2.corpAvailabilityResult);
		this.nonCorpAvailabilityResult = new AvailabilityResult(a2.nonCorpAvailabilityResult);
	}
	
	public static AvailabilityResultStruct combine(
		AvailabilityResultStruct a1,
		AvailabilityResultStruct a2
	) {
		return new AvailabilityResultStruct(
			AvailabilityResult.combine(a1.corpAvailabilityResult, a2.corpAvailabilityResult), 
			AvailabilityResult.combine(a1.nonCorpAvailabilityResult, a2.nonCorpAvailabilityResult)
		);
	}
	
	public Availability getNetAvailability() {
		if (netAvailability == null) {
			populateNetAvailability();
		}
		return netAvailability;
	}
	
	public void populateNetAvailability() {
		netAvailability = AvailabilityHelper.combineAvailability(corpAvailabilityResult.availability, nonCorpAvailabilityResult.availability);
	}
	
	public AvailabilityResult getCorpAvailabilityResult() {
		return corpAvailabilityResult;
	}
	
	public void setCorpAvailabilityResult(AvailabilityResult corpAvailabilityResult) {
		netAvailability = null;
		this.corpAvailabilityResult = corpAvailabilityResult;
	}
	
	public AvailabilityResult getNonCorpAvailabilityResult() {
		return nonCorpAvailabilityResult;
	}
	
	public void setNonCorpAvailabilityResult(AvailabilityResult nonCorpAvailabilityResult) {
		netAvailability = null;
		this.nonCorpAvailabilityResult = nonCorpAvailabilityResult;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return corpAvailabilityResult.equals(((AvailabilityResultStruct)obj).corpAvailabilityResult) 
			&& nonCorpAvailabilityResult.equals(((AvailabilityResultStruct)obj).nonCorpAvailabilityResult);
	}

	@Override
	public int hashCode() {
		return corpAvailabilityResult.hashCode() 
			^ nonCorpAvailabilityResult.hashCode();
	}
	
	@Override
	public String toString() {
		return "Net Availability: " + getNetAvailability()
			+ "Corp Availability: " + corpAvailabilityResult
			+ "Non-corp Availability: " + nonCorpAvailabilityResult;
	}
}
