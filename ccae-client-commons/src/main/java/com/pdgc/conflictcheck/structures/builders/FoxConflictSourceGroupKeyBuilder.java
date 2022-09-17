package com.pdgc.conflictcheck.structures.builders;

import com.pdgc.conflictcheck.structures.FoxConflictSourceGroupKey;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

/**
 * FoxConflictSourceGroupKeyBuilder
 */
public final class FoxConflictSourceGroupKeyBuilder {

	private static FoxConflictSourceGroupKey nullConflictSourceGroupKey = new FoxConflictSourceGroupKey(null, null);

	private FoxConflictSourceGroupKeyBuilder() {

    }

	public static FoxConflictSourceGroupKey getConflictSourceGroupKey(RightStrand rightStrand) {
		if (rightStrand == null) {
			return nullConflictSourceGroupKey;
		}
		
		return new FoxConflictSourceGroupKey(
			(FoxRightSource)rightStrand.getRightSource(),
			rightStrand.getRightType()
		);
	}
}
