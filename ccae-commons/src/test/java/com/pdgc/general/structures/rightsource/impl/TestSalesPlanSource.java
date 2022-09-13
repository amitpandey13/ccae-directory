package com.pdgc.general.structures.rightsource.impl;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.RightSourceType;
import com.pdgc.general.structures.rightsource.TestRightSource;

public class TestSalesPlanSource extends TestRightSource {
		
	private static final long serialVersionUID = 1L;

	public TestSalesPlanSource(RightSourceType sourceType, long salesWindowId) {
		super(sourceType, salesWindowId);
		
		if (sourceType.getBaseRightSourceType() != BaseRightSourceType.SALESPLAN) {
			throw new IllegalArgumentException("SalesPlanSource must have a parentSourceType of " + Constants.SOURCE_TYPE_ID_SALES_PLAN);
		}
	}
	
	@Override
	public String toString() {
		return "TestSalesPlanSource[sourceType=" + sourceType.toString() 
			+ ", sourceId=" + sourceType
		;
	}
}
