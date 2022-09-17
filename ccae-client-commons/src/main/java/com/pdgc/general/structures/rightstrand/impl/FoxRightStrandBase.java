package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;

@SuppressWarnings("PMD.AbstractNaming")
public abstract class FoxRightStrandBase extends NonAggregateRightStrand implements FoxRightStrand {

	private static final long serialVersionUID = 1L;

	private FoxRightSource rightSource;
	private Long productHierarchyId;
	private Long distributionRightsOwner;

    @SuppressWarnings("PMD.ExcessiveParameterList")
	public FoxRightStrandBase(
		long rightStrandId,
    	PMTL pmtl,
    	TermPeriod termPeriod,
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
	    	termPeriod,
	    	rightType,
	    	actualPMTL,
	    	origTerm,
	    	comment,
	    	checkedIn
		);
		this.rightSource = rightSource;
		this.productHierarchyId = productHierarchyId;
		this.distributionRightsOwner = distributionRightsOwner;
	}
	
	@Override
	public FoxRightSource getRightSource() {
		return rightSource;
	}
	
	@Override
	public Long getProductHierarchyId() {
		return productHierarchyId;
	}
	
	@Override
	public Long getDistributionRightsOwner() {
		return distributionRightsOwner;
	}
}
