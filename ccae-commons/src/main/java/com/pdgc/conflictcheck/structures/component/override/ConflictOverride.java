package com.pdgc.conflictcheck.structures.component.override;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.impl.ConflictKey;

public class ConflictOverride implements IConflictKeyContainer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long overrideId; //might not actually exist?
	ConflictOverrideType overrideType;
    String overrideComment;
    ConflictKey conflictKey;
    Long userId;
    LocalDateTime createdAt;

	public ConflictOverride(
		long overrideId, 
		Long userId,
        ConflictOverrideType overrideType, 
        String overrideComment,
        ConflictKey conflictKey
	) {
		this.userId = userId;
		this.overrideId = overrideId;
        this.overrideType = overrideType;
        this.overrideComment = overrideComment;
        
        this.conflictKey = conflictKey;
	}

	public ConflictOverride(long overrideId,
							Long userId,
							ConflictOverrideType overrideType,
							String overrideComment,
							ConflictKey conflictKey,
							LocalDateTime createdAt) {
		this(overrideId, userId, overrideType, overrideComment, conflictKey);
		this.createdAt = createdAt;
	}
    
    @Override
    public String toString() {
    	StringBuilder overrideString = new StringBuilder();
		overrideString.append(overrideType.overrideTypeDesc + " for: ")
    	.append(conflictKey.getConflictType().getConflictDesc() + " | ")
		.append(conflictKey.getPMTL())
		.append(" : ").append(conflictKey.getTerm().toString())
		.append(" | ")
		.append("WC: ")
		.append(conflictKey.getPrimaryRightSource())
		.append("/").append(conflictKey.getPrimaryRightType());

		//Rights-in conflicts wont have a conflicting side so check for null.
		if (conflictKey.getConflictingRightSource() != null) {
			overrideString.append(" CONFLICT: ")
			.append(conflictKey.getConflictingRightSource())
			.append("/").append(conflictKey.getConflictingRightType());
		}
		return overrideString.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
    	
    	ConflictOverride obj2 = (ConflictOverride)obj;
    	
    	return Objects.equals(overrideId, obj2.overrideId)
    		&& Objects.equals(overrideType,  obj2.overrideType)
    		&& Objects.equals(overrideComment, obj2.overrideComment)
    		&& Objects.equals(conflictKey, obj2.conflictKey)
			&& Objects.equals(userId, obj2.userId);
    }
    
    @Override
    public int hashCode() {
    	return Long.hashCode(overrideId)
    		^ Objects.hashCode(overrideType)
    		^ Objects.hashCode(overrideComment)
    		^ Objects.hashCode(conflictKey)
			^ Objects.hashCode(userId);
    }

	public long getOverrideId() {
		return overrideId;
	}
	
    public void setOverrideId(long overrideId) {
		this.overrideId = overrideId;
	}	
	
	public ConflictOverrideType getOverrideType() {
		return overrideType;
	}
	
	public String getOverrideComment() {
		return overrideComment;
	}
	
	@Override
	public ConflictKey getConflictKey() {
		return conflictKey;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
