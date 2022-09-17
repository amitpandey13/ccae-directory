package com.pdgc.general.structures.rightsource.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.FoxRightSource;

public class FoxDealSource extends FoxRightSource {

	private static final long serialVersionUID = 1L;
	
	private Long dealProductId;
	private String displaySourceType;
	private String displaySourceId;

	public FoxDealSource(
		FoxRightSourceType rightSourceType, 
		long dealId, 
		String rightSourceDetailId,
		FoxBusinessUnit businessUnit,
		Long dealProductId, 
		String displaySourceType,
		String displaySourceId
	) {
		super(rightSourceType, dealId, rightSourceDetailId, businessUnit);
		this.dealProductId = dealProductId; 
		this.displaySourceType = (StringUtils.isBlank(displaySourceType) ? "" : displaySourceType);
		this.displaySourceId = (StringUtils.isBlank(displaySourceId) ? "" : displaySourceId);
	}
	
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return super.equals(obj)
			&& Objects.equals(dealProductId, ((FoxDealSource)obj).dealProductId)
		;
	}
	
	public int hashCode() {
		return super.hashCode()
			^ Objects.hashCode(dealProductId);
	}
	
	public Long getDealId() {
		return sourceId;
	}
	
	public Long getDealProductId() {
		return dealProductId;
	}
	
	@Override
	public FoxRightSourceType getSourceType() {
		return (FoxRightSourceType)sourceType;
	}

	public String getDisplaySourceType() {
		return displaySourceType;
	}

	public String getDisplaySourceId() {
		return displaySourceId;
	}

	public void setDisplaySourceId(String displaySourceId) {
		this.displaySourceId = displaySourceId;
	}
}
