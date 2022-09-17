package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxRestrictionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestFoxRestrictionRightStrand extends TestFoxCorporateRightStrand implements FoxRestrictionStrand {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Long parentRightSourceId;
	
	public TestFoxRestrictionRightStrand(
		long rightStrandId,
    	PMTL pmtl,
    	Term term,
    	FoxRightSource rightSource,
    	RightType rightType,
    	PMTL actualPMTL,
    	Term origTerm,
    	String comment,
    	boolean checkedIn,
    	Long productHierarchyId,
    	Long distributionRightsOwner,
    	Long parentRightSourceId
	) {
		super(
			rightStrandId,
	    	pmtl,
	    	new TermPeriod(term, TimePeriod.FULL_WEEK),
	    	rightSource,
	    	rightType,
	    	actualPMTL,
	    	origTerm,
	    	comment,
	    	checkedIn,
	    	productHierarchyId,
	    	distributionRightsOwner
		);
		this.parentRightSourceId = parentRightSourceId;
	}
	
	public TestFoxRestrictionRightStrand(FoxRestrictionStrand foxSeriesRestriction) {
		super(foxSeriesRestriction); 
	}

	@Override
	public Long getParentRightSourceId() {
		return parentRightSourceId;
	}
	
	public void setParentRightSourceId(Long parentRightSourceId) {
		this.parentRightSourceId = parentRightSourceId;
	}

	@Override
	public Long getStatusId() {
		return Constants.INITIAL_STATUS;
	}
}
