package com.pdgc.conflictcheck.structures.component.override;

import java.io.Serializable;

public class ConflictOverrideType implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long overrideTypeId;
	String overrideTypeDesc;
	
	public ConflictOverrideType() {}
	public ConflictOverrideType(long overrideTypeId, String overrideTypeDesc) {
		this.overrideTypeId = overrideTypeId;
		this.overrideTypeDesc = overrideTypeDesc;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return overrideTypeId.equals(((ConflictOverrideType) obj).overrideTypeId);
	}

	@Override
	public int hashCode() {
		return overrideTypeId.hashCode();
	}

	@Override
	public String toString() {
		return overrideTypeDesc.toString();
	}

	public long getOverrideTypeId() {
		return overrideTypeId;
	}

	public String getOverrideTypeDesc() {
		return overrideTypeDesc;
	}

}
