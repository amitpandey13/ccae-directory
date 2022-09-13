package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;

public class TestDistributionStrand extends TestCorporateStrand {

	private static final long serialVersionUID = 1L;

    public TestDistributionStrand(
		long rightStrandId,
		PMTL pmtl,
		Term term,
		TestCorpSource rightSource, 
		RightType rightType,
		PMTL actualPMTL,
		Term actualTerm,
		int calculationOrder
	) {
		super(rightStrandId, pmtl, term, rightSource, rightType, actualPMTL, actualTerm, "test distribution", calculationOrder);
	}

	public TestDistributionStrand(TestDistributionStrand rs) {
		super(rs);
	}
	
	@Override
	public boolean additionalGroupingEquals(RightStrand obj) {
		return super.additionalGroupingEquals(obj);
	}

	@Override
	public boolean getIsDistribution() {
		return true;
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
