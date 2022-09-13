package com.pdgc.general.structures.rightstrand.impl;

import java.io.Serializable;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.RightSourceType;

/**
 * Describes a right strand from the datamart
 */
public abstract class NonAggregateRightStrand implements RightStrand, Serializable {
	
	private static final long serialVersionUID = 1L;

	protected long rightStrandId; // original PK from the database
	
	protected PMTL pmtl;

    protected TermPeriod termPeriod;
    protected RightType rightType;

    protected PMTL actualPMTL;
    protected Term origTerm;

    protected String comment; 
    
	protected boolean checkedIn;
	
	protected NonAggregateRightStrand(
    	long rightStrandId,
    	PMTL pmtl,
    	TermPeriod termPeriod,
    	RightType rightType,
    	PMTL actualPMTL,
    	Term origTerm,
    	String comment,
    	boolean checkedIn
    ) {
    	this.rightStrandId = rightStrandId;
    	this.pmtl = pmtl;
    	this.termPeriod = termPeriod;
    	this.rightType = rightType;
    	this.actualPMTL = actualPMTL;
    	this.origTerm = origTerm;
    	this.comment = comment;
    	this.checkedIn = checkedIn;
    }
	
	protected NonAggregateRightStrand(NonAggregateRightStrand rs) {
		rightStrandId = rs.rightStrandId;
        pmtl = rs.pmtl;
        termPeriod = rs.termPeriod;
        rightType = rs.rightType;
        actualPMTL = rs.actualPMTL;
        origTerm = rs.origTerm;
        comment = rs.comment; 
        checkedIn = rs.checkedIn;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":id=[" + rightStrandId + 
			"]\tRightSource=[" + getRightSource() + 
			"]\tRightType=[" + getRightType() + 
			"]\tPMTL=[" + getPMTL() +
			"]\tActualPMTL=[" + getActualPMTL() + 
			"]\tTerm:[" + termPeriod.getTerm().toString() + 
			"]\tOrigTerm:[" + origTerm.toString();
	}

	public long getRightStrandId() {
		return rightStrandId;
	}
	
	public PMTL getPMTL() {
		return pmtl;
	}
	
	public TermPeriod getTermPeriod() {
    	return termPeriod;
    }
    
    public RightSourceType getSourceType() {
    	return getRightSource().getSourceType();
    }
    
    public RightType getRightType() {
    	return rightType;
    }
    
    public PMTL getActualPMTL() {
    	return actualPMTL;
    }
    
    public Term getOrigTerm() {
    	return origTerm;
    }
    
    public boolean isCheckedIn() {
		return checkedIn;
	}

	public String getComment() {
		return comment;
	}
}
