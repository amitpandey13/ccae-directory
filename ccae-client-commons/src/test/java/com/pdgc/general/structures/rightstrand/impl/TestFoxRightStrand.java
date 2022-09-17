package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;
import com.pdgc.general.structures.rightstrand.TestRightStrand;

public abstract class TestFoxRightStrand extends TestRightStrand implements FoxRightStrand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FoxRightSource rightSource;
	private Long productHierarchyId;
	private Long distributionRightsOwner;
	
	public TestFoxRightStrand(
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
		super(rightStrandId, pmtl, termPeriod, rightType, actualPMTL, origTerm, comment, checkedIn);
		this.rightSource = rightSource;
		this.productHierarchyId = productHierarchyId;
		this.distributionRightsOwner = distributionRightsOwner;
	}
	
	public TestFoxRightStrand(FoxRightStrand rs) {
		this(
			rs.getRightStrandId(),
			rs.getPMTL(),
			rs.getTermPeriod(),
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
	public FoxRightSource getRightSource() {
		return rightSource;
	}
	
	public void setRightSource(FoxRightSource rightSource) {
		this.rightSource = rightSource;
	}
	
	@Override
	public Long getProductHierarchyId() {
		return productHierarchyId;
	}
	
	public void setProductHierarchyid(Long productHierarchyId) {
		this.productHierarchyId = productHierarchyId;
	}
	
	@Override
	public Long getDistributionRightsOwner() {
		return distributionRightsOwner;
	} 

	public void setDistributionRightsOwner(Long distributionRightsOwner) {
		this.distributionRightsOwner = distributionRightsOwner;
	}
}
