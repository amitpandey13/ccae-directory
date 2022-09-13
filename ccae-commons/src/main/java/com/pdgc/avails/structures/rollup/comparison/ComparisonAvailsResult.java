package com.pdgc.avails.structures.rollup.comparison;

import java.util.HashMap;
import java.util.Map;

import com.pdgc.avails.structures.rollup.FullAvailsResult;

public class ComparisonAvailsResult extends FullAvailsResult {

	private static final long serialVersionUID = 1L;
	
	public ComparisonSource comparisonSource;
	public Map<ResultChangeType, Boolean> comparisonResults;

	public ComparisonAvailsResult(
	    FullAvailsResult a2, 
		ComparisonSource comparisonSource
	) {
		super(a2, false);
		
		this.comparisonSource = comparisonSource;
		
		comparisonResults = new HashMap<>();
		comparisonResults.put(ResultChangeType.TermPeriodChanged, false);
		comparisonResults.put(ResultChangeType.MissingTermInNewResult, false);
		comparisonResults.put(ResultChangeType.MissingTermInOldResult, false);
		comparisonResults.put(ResultChangeType.MissingTimePeriodInNewResult, false);
		comparisonResults.put(ResultChangeType.MissingTimePeriodInOldResult, false);
		comparisonResults.put(ResultChangeType.ValueChanged, false);
	}
}
