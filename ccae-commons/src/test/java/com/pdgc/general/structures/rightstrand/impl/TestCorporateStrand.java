package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightsource.impl.TestCorpSource;
import com.pdgc.general.structures.rightstrand.TestRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public abstract class TestCorporateStrand extends TestRightStrand implements CorporateRightStrand {

	private static final long serialVersionUID = 1L;
    
	private TestCorpSource rightSource;
	private int calculationOrder;
	
	public TestCorporateStrand(
		long rightStrandId,
		PMTL pmtl,
		Term term,
		TestCorpSource rightSource, 
		RightType rightType,
		PMTL actualPMTL,
		Term actualTerm,
		String comment,
		int calculationOrder
    ){
		super(rightStrandId, pmtl, new TermPeriod(term, TimePeriod.FULL_WEEK), rightType, actualPMTL, actualTerm, comment, true);
		this.rightSource = rightSource;
		this.calculationOrder = calculationOrder;
    }
	
	public TestCorporateStrand(TestCorporateStrand rs) {
		super(rs);
		this.rightSource = rs.rightSource;
		this.calculationOrder = rs.calculationOrder;
	}
	
	@Override
	public boolean additionalGroupingEquals(RightStrand obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		return Integer.compare(calculationOrder, ((TestCorporateStrand)obj).calculationOrder) == 0;
	}
	
	public TestCorpSource getRightSource() {
		return rightSource;
	}
	
	public void setRightSource(TestCorpSource rightSource) {
		this.rightSource = rightSource;
	}
	
	public int getCalculationOrder() {
		return calculationOrder;
	}
	
	public void setCalculationOrder(int calculationOrder) {
		this.calculationOrder = calculationOrder;
	}
}
