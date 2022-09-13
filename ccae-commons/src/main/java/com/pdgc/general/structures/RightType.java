package com.pdgc.general.structures;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pdgc.general.structures.classificationEnums.RightTypeType;

import lombok.Builder;

/**
 * Class used to define a right type
 * 
 * @author Vishal Raut
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@Builder(toBuilder=true)
public class RightType implements Comparable<RightType>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected long rightTypeId;
	
	protected String rightTypeDesc;
	protected String shortName;
	
	protected RightTypeType rightTypeType;
	
	@Builder.Default
	protected Boolean allowsEpisodeLimit = false;
	
	@Builder.Default
	protected Integer episodeLimit = 0;

	public RightType(
		long rightTypeId, 
		String rightTypeDesc, 
		String shortName, 
		RightTypeType rightTypeType, 
		Boolean allowsEpisodeLimit, 
		Integer episodeLimit
	) {
		this.rightTypeId = rightTypeId;
		this.rightTypeDesc = rightTypeDesc;
		
		String updatedShortName = shortName; 
		if (shortName == null || shortName.trim().length() == 0) {
			updatedShortName = rightTypeDesc;
		}
		
		this.rightTypeType = rightTypeType;
		this.allowsEpisodeLimit = allowsEpisodeLimit;
		
		if (allowsEpisodeLimit && episodeLimit != null) {
			this.episodeLimit = episodeLimit;
			if (episodeLimit > 0) {
				updatedShortName += " (" + episodeLimit.toString() + ")";
			}
		} else {
			this.episodeLimit = 1;
		}
		
		this.shortName = updatedShortName;
	}
	
	public Long getRightTypeId() {
		return rightTypeId;
	}

	public String getRightTypeDesc() {
		return rightTypeDesc;
	}

	public String getShortName() {
		return shortName;
	}
	
	public RightTypeType getRightTypeType() {
		return rightTypeType;
	}
	
	public Boolean allowsEpisodeLimit() {
		return allowsEpisodeLimit;
	}

	public Integer getEpisodeLimit() {
		return episodeLimit;
	}
	
	@Override
	public String toString() {
		return "RightType:id=[" + this.rightTypeId + "]\tdesc=[" + rightTypeDesc + "]\tepisodeLimit=[" + episodeLimit + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + episodeLimit;
		result = prime * result + (int) (rightTypeId ^ (rightTypeId >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		RightType other = (RightType) obj;
		if (Long.compare(rightTypeId, other.rightTypeId) != 0)
			return false;
		if (Integer.compare(episodeLimit, other.episodeLimit) != 0) 
			return false;
		return true;
	}
	
	@Override
	public int compareTo(RightType rt) {
		return Long.compare(this.rightTypeId, rt.rightTypeId);
	}
}
