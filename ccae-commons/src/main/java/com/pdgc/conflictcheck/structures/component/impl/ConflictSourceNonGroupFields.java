package com.pdgc.conflictcheck.structures.component.impl;

import java.io.Serializable;


/**
 * Class for holding fields from the conflict sources.
 * These fields must are allowed to be different between the child conflicts of a rollup.
 * This should be subclassed to customize grouping keys for different clients
 * @author Linda Xu
 *
 */
public abstract class ConflictSourceNonGroupFields implements Serializable{

	private static final long serialVersionUID = 1L;

	public ConflictSourceNonGroupFields() {
		
	}
}
