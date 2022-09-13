package com.pdgc.avails.structures.rollup.comparison;

import com.pdgc.avails.structures.rollup.FullAvailsResult;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class ComparisonSource {

	private Term oldTerm;
	private Term newTerm;
	
	private TimePeriod oldPeriod;
	private TimePeriod newPeriod;
	
	private FullAvailsResult oldResult;
	private FullAvailsResult newResult;
	
	public ComparisonSource(
		Term oldTerm,
		Term newTerm,
		TimePeriod oldPeriod,
		TimePeriod newPeriod,
		FullAvailsResult oldResult,
		FullAvailsResult newResult
	) {
		this.oldTerm = oldTerm;
		this.newTerm = newTerm;
		
		this.oldPeriod = oldPeriod;
		this.newPeriod = newPeriod;
		
		this.oldResult = oldResult;
		this.newResult = newResult;
	}
	
	public Term getOldTerm() {
		return oldTerm;
	}
	
	public Term getNewTerm() {
		return newTerm;
	}
	
	public TimePeriod getOldTimePeriod() {
		return oldPeriod;
	}
	
	public TimePeriod getNewTimePeriod() {
		return newPeriod;
	}
	
	public FullAvailsResult getOldResult() {
		return oldResult;
	}
	
	public FullAvailsResult getNewResult() {
		return newResult;
	}
}
