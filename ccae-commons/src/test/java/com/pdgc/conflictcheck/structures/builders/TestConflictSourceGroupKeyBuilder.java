package com.pdgc.conflictcheck.structures.builders;

import com.pdgc.conflictcheck.structures.component.impl.TestConflictSourceGroupKey;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class TestConflictSourceGroupKeyBuilder {

	private static TestConflictSourceGroupKey nullConflictSourceGroupKey = new TestConflictSourceGroupKey(null, null);
	
	public static TestConflictSourceGroupKey getConflictSourceGroupKey(RightStrand rightStrand) {
		if (rightStrand == null) {
			return nullConflictSourceGroupKey;
		}
		return new TestConflictSourceGroupKey(rightStrand.getRightSource(), rightStrand.getRightType());
	}
}
