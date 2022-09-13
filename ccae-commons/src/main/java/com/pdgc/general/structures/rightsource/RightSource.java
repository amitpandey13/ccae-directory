package com.pdgc.general.structures.rightsource;

import java.io.Serializable;
import java.util.Objects;

public abstract class RightSource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long sourceId;
	protected RightSourceType sourceType;

	public RightSource(RightSourceType sourceType, Long sourceId) {
		this.sourceType = sourceType;
		this.sourceId = sourceId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
			
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		return Objects.equals(sourceId, ((RightSource)obj).sourceId)
			&& Objects.equals(sourceType, ((RightSource)obj).sourceType)
		;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(sourceId) 
			^ Objects.hashCode(sourceType);
	}

	public Long getSourceId() {
		return sourceId;
	}

	public RightSourceType getSourceType() {
		return sourceType;
	}
}
