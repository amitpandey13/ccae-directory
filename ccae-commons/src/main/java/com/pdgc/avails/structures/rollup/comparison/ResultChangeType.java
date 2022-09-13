package com.pdgc.avails.structures.rollup.comparison;

import java.util.Arrays;
import java.util.Comparator;

public enum ResultChangeType {
    ValueChanged(5),
    MissingTimePeriodInOldResult(4),
    MissingTimePeriodInNewResult(3),
    MissingTermInOldResult(2),
    MissingTermInNewResult(1),
    TermPeriodChanged(0);
	
	private int value;
	ResultChangeType(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	
	public static ResultChangeType[] getResultChangeTypenAscendingOrder() {
		ResultChangeType[] resultCHangeTypeArray = ResultChangeType.values();
        Comparator<ResultChangeType>resultChangeTypeComparator = new Comparator<ResultChangeType>() {
        	@Override
        	public int compare(ResultChangeType one,  ResultChangeType two) {
        		return one.getValue()-two.getValue();
        	}
        };
        Arrays.sort(resultCHangeTypeArray, resultChangeTypeComparator);
        return  resultCHangeTypeArray;
	}
	
  	
}
