package com.pdgc.conflictcheck.structures;

import java.util.Collection;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.impl.FoxConflictKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;

public class FoxConflict extends Conflict {

	private static final long serialVersionUID = 1L;
	
	Long conflictId;
	
	private FoxConflictSourceNonGroupFields primarySourceInfo;
	private FoxConflictSourceNonGroupFields conflictingSourceInfo;
	private Long userId;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public FoxConflict(
		Long conflictId,
		FoxConflictKey conflictKey,
		Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass,
        FoxConflictSourceNonGroupFields primarySourceInfo,
        FoxConflictSourceNonGroupFields conflictingSourceInfo,
        boolean primaryCheckedIn,
        boolean conflictingCheckedIn,
        Long userId
	) {
		super(
			conflictKey, 
			conflictOverrides, 
			conflictStatus, 
			siblingConflictClass, 
			primaryCheckedIn, 
			conflictingCheckedIn
		);
		
		this.conflictId = conflictId;
		this.userId = userId;
		
		this.primarySourceInfo = primarySourceInfo;
		this.conflictingSourceInfo = conflictingSourceInfo;
		
	}
	
	public FoxConflict(
		FoxConflictKey conflictKey,
		Collection<ConflictOverride> conflictOverrides,
        ConflictStatus conflictStatus,
        ConflictClass siblingConflictClass,
        FoxConflictSourceNonGroupFields primarySourceInfo,
        FoxConflictSourceNonGroupFields conflictingSourceInfo,
        boolean primaryCheckedIn,
        boolean conflictingCheckedIn,
        Long userId
	) {
		this(
			null,
			conflictKey, 
			conflictOverrides, 
			conflictStatus, 
			siblingConflictClass, 
			primarySourceInfo,
			conflictingSourceInfo,
			primaryCheckedIn, 
			conflictingCheckedIn,
			userId
		);
	}
	
	
	
	@Override
	public FoxConflictKey getConflictKey() {
		return (FoxConflictKey)conflictKey;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public Long getProductHierarchyId() {
		return getConflictKey().getProductHierarchyId();
	}
	
	public FoxConflictSourceNonGroupFields getPrimarySourceInfo() {
		return primarySourceInfo;
	}

	public FoxConflictSourceNonGroupFields getConflictingSourceInfo() {
		return conflictingSourceInfo;
	}
}
