package com.pdgc.conflictcheck.structures.component.impl;

import java.util.Objects;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.rightsource.RightSource;

public class TestConflictSourceGroupKey extends ConflictSourceGroupKey {

	private static final long serialVersionUID = 1L;

	public TestConflictSourceGroupKey(RightSource rightSource, RightType rightType) {
		super(rightSource, rightType);
	}
	
	@Override
    public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		return Objects.equals(rightSource, ((TestConflictSourceGroupKey)obj).rightSource)
            && Objects.equals(rightType, ((TestConflictSourceGroupKey)obj).rightType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rightSource)
            ^ Objects.hashCode(rightType);
    }
}
