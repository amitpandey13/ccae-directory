package com.pdgc.conflictcheck.structures.component.impl;

import java.io.Serializable;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.rightsource.RightSource;

/**
 * Class for holding fields from the conflict sources.
 * These fields must remain the same across all child conflicts in a rollup.
 * This should be subclassed to customize grouping keys for different clients
 * @author Linda Xu
 *
 */
public abstract class ConflictSourceGroupKey implements Serializable{

	private static final long serialVersionUID = 1L;

	protected RightSource rightSource;
	protected RightType rightType;
	
	public ConflictSourceGroupKey(RightSource rightSource, RightType rightType) {
		this.rightSource = rightSource;
		this.rightType = rightType;
	}
	
	public RightSource getRightSource() {
    	return rightSource;
    }
    
    public RightType getRightType() {
    	return rightType;
    }
}
