package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxDistributionStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestFoxDistributionRightStrand extends TestFoxCorporateRightStrand implements FoxDistributionStrand {

	private static final long serialVersionUID = 1L;

    public TestFoxDistributionRightStrand(
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
    	Long distributionRightsOwner
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
	}
	
	public TestFoxDistributionRightStrand(FoxDistributionStrand rs) {
		this(
			rs.getRightStrandId(),
	    	rs.getPMTL(),
	    	rs.getTerm(),
	    	rs.getRightSource(),
	    	rs.getRightType(),
	    	rs.getActualPMTL(),
	    	rs.getOrigTerm(),
	    	rs.getComment(),
	    	rs.isCheckedIn(),
	    	rs.getProductHierarchyId(),
	    	rs.getDistributionRightsOwner()
		);
	}
	
	@Override
	public Long getStatusId() {
		return Constants.INITIAL_STATUS;
	}
}
