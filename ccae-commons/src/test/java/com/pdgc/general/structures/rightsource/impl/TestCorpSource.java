package com.pdgc.general.structures.rightsource.impl;

import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.RightSourceType;
import com.pdgc.general.structures.rightsource.TestRightSource;

public class TestCorpSource extends TestRightSource {

	private static final long serialVersionUID = 1L;

	public TestCorpSource(RightSourceType sourceType, Long sourceId) {
		super(sourceType, sourceId);
		
		if (sourceType.getBaseRightSourceType() != BaseRightSourceType.CORPRIGHTS) {
			throw new IllegalArgumentException("CorpSource must have a parentSourceType of " + BaseRightSourceType.CORPRIGHTS);
		}
	}
	
	@Override
	public String toString() {
		return "TestCorpSource[sourceType=" + sourceType.toString() 
			+ ", sourceId=" + sourceType
		;
	}
}
