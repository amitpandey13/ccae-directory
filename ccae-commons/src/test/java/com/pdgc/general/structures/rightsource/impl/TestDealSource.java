package com.pdgc.general.structures.rightsource.impl;

import java.util.Objects;

import com.pdgc.general.structures.rightsource.BaseRightSourceType;
import com.pdgc.general.structures.rightsource.RightSourceType;
import com.pdgc.general.structures.rightsource.TestRightSource;

public class TestDealSource extends TestRightSource {

	private static final long serialVersionUID = 1L;

	String sourceDetailId;
	
	public TestDealSource(RightSourceType sourceType, long dealId, String sourceDetailId) {
		super(sourceType, dealId);
		this.sourceDetailId = sourceDetailId;
		
		if (sourceType.getBaseRightSourceType() != BaseRightSourceType.DEAL) {
			throw new IllegalArgumentException("DealSource must have a parentSourceType of " + BaseRightSourceType.DEAL);
		}
	}
	
	public TestDealSource(TestDealSource drs) {
		this(drs.getSourceType(), drs.getDealId(), drs.getSourceDetailId());
	}
	
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		
		return Objects.equals(sourceDetailId, ((TestDealSource)obj).sourceDetailId);
	}
	
	@Override
	public String toString() {
		return "TestDealSource[sourceType=" + sourceType.toString() 
			+ ", sourceId=" + sourceType
		;
	}
	
	public long getDealId() {
		return sourceId;
	}
	
	public void setDealId(long dealId) {
		setSourceId(dealId);
	}
	
	public String getSourceDetailId() {
		return sourceDetailId;
	}
	
	public void setSourceDetailId(String sourceDetailId) {
		this.sourceDetailId = sourceDetailId;
	}
}
