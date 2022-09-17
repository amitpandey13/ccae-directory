package com.pdgc.general.structures.rightsource;

import java.util.Objects;

import com.pdgc.general.structures.FoxRightSourceType;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class FoxRightSource extends RightSource {

	private static final long serialVersionUID = 1L;
	
	private String sourceDetailId;
	private FoxBusinessUnit businessUnit;
	
	public FoxRightSource(
		FoxRightSourceType sourceType,
		Long sourceId,
		String sourceDetailId,
		FoxBusinessUnit businessUnit
	) {
		super(sourceType, sourceId);
		
		this.sourceDetailId = sourceDetailId;
		this.businessUnit = businessUnit;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		
		return Objects.equals(sourceDetailId, ((FoxRightSource)obj).sourceDetailId)
			&& Objects.equals(businessUnit, ((FoxRightSource)obj).businessUnit)
		;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode()
			^ Objects.hashCode(sourceDetailId)
			^ Objects.hashCode(businessUnit)
		;
	}
	
	@Override
	public FoxRightSourceType getSourceType() {
		return (FoxRightSourceType)sourceType;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[sourceType=" + sourceType.toString() 
			+ ", sourceId=" + sourceType
			+ ", sourceDetailId=" + sourceDetailId
			+ ", businessUnit=" + businessUnit
		;
	}
	
	public String getSourceDetailId() {
		return sourceDetailId;
	}
	
	public FoxBusinessUnit getBusinessUnit() {
		return businessUnit;
	}
}
