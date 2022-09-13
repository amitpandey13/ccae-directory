package com.pdgc.conflictcheck.structures.component;

import java.io.Serializable;

public class ConflictType implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
	private Long conflictId;
	private String conflictDesc;
	
	protected ConflictType() {} 
	public ConflictType(Long conflictId, String conflictDesc) {
		this.conflictId = conflictId;

		if (conflictDesc == null) {
			conflictDesc = "";
		}
		this.conflictDesc = conflictDesc;
	}

	public Long getConflictId() {
		return conflictId;
	}

	public String getConflictDesc() {
		return conflictDesc;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		return conflictId.equals(((ConflictType) obj).conflictId);
	}

	@Override
	public int hashCode() {
		return conflictId.hashCode();
	}

	@Override
	public String toString() {
		return conflictDesc;
	}
}
