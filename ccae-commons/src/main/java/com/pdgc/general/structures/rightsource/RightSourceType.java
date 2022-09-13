package com.pdgc.general.structures.rightsource;

import java.io.Serializable;

public abstract class RightSourceType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;	
	private BaseRightSourceType baseRightSourceType;
	
	protected RightSourceType() {}
	
	//This MUST be the only constructor to ensure that all childClasses will have proper entries in allValues
	protected RightSourceType(int id, BaseRightSourceType baseRightSourceType) {
		this.id = id;
		this.baseRightSourceType = baseRightSourceType;
	}
	
	public int getId() {
		return id;
	}

	public BaseRightSourceType getBaseRightSourceType() {
		return baseRightSourceType;
	}
	
	public String getDescription() {
		return baseRightSourceType.toString();
	}

	@Override
	public String toString() {
		return "RightSourceTypeImpl[id=" + id + ", baseRightSourceType=" + baseRightSourceType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((baseRightSourceType == null) ? 0 : baseRightSourceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RightSourceType other = (RightSourceType) obj;
		if (id != other.id)
			return false;
		if (baseRightSourceType != other.baseRightSourceType)
			return false;
		return true;
	}

}
