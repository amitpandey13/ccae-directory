package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxRestrictionStrand;

public abstract class TestFoxCorporateRightStrand extends TestFoxRightStrand implements CorporateRightStrand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TestFoxCorporateRightStrand(
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
	
	public TestFoxCorporateRightStrand(FoxRestrictionStrand foxSeriesRestriction) {
		super(foxSeriesRestriction);
	}

	//Currently no logic from fox that assigns calculation order to right strands
	@Override
	public int getCalculationOrder() {
		return 1;
	}
}
