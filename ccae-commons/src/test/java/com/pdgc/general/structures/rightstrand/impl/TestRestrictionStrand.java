package com.pdgc.general.structures.rightstrand.impl;

import java.util.Objects;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;

public class TestRestrictionStrand extends TestCorporateStrand {

	private static final long serialVersionUID = 1L;
    
	private Long parentRightSourceId;
	private Long customerId;
	
	public TestRestrictionStrand(
		long rightStrandId,
		PMTL pmtl,
		Term term,
		TestCorpSource rightSource, 
		RightType rightType,
		PMTL actualPMTL,
		Term actualTerm,
		int calculationOrder,
		Long parentRightSourceId
	) {
		super(rightStrandId, pmtl, term, rightSource, rightType, actualPMTL, actualTerm, "dummy restriction", calculationOrder);
		this.parentRightSourceId = parentRightSourceId;
	}
	
	public TestRestrictionStrand(TestRestrictionStrand rs) {
		super(rs);
		this.parentRightSourceId = rs.parentRightSourceId;
	}

	@Override
	public boolean additionalGroupingEquals(RightStrand obj) {
		if (!(super.additionalGroupingEquals(obj))) {
			return false;
		}
		
		return Objects.equals(parentRightSourceId, ((TestRestrictionStrand)obj).parentRightSourceId);
	}
	
	public Long getParentRightSourceId() {
		return parentRightSourceId;
	}
	
	public void setParentRightSourceId(Long parentRightSourceId) {
		this.parentRightSourceId = parentRightSourceId;
	}
	
	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	@Override
	public boolean getIsDistribution() {
		return false;
	}

	@Override
	public Long getProductHierarchyId() {
		return 100L;
	}

	@Override
	public Long getStatusId() {
		return Constants.INITIAL_STATUS;
	}
}
