package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.impl.TestSalesPlanSource;
import com.pdgc.general.structures.rightstrand.TestRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestSalesPlanStrand extends TestRightStrand implements SalesPlanRightStrand {
	
    private static final long serialVersionUID = 1L;

	private TestSalesPlanSource rightSource;
	private Customer customer;
	
	public TestSalesPlanStrand(
		long rightStrandId,
		PMTL pmtl,
		Term term,
		TestSalesPlanSource rightSource, 
		RightType rightType,
		PMTL actualPMTL,
		Term actualTerm
    ){
		super(rightStrandId, pmtl, new TermPeriod(term, TimePeriod.FULL_WEEK), rightType, actualPMTL, actualTerm, "dummy sales plan", true);
		this.rightSource = rightSource;
    }
	
	public TestSalesPlanStrand(TestSalesPlanStrand rs) {
		super(rs);
		this.rightSource = rs.rightSource;
	}

	@Override
	public Long getSalesWindowId() {
		return getRightSource().getSourceId();
	}

	@Override
	public Customer getCustomer() {
		return customer;
	}

	@Override
	public boolean additionalGroupingEquals(RightStrand obj) {
		return this.getClass() == obj.getClass();
	}
	
	@Override
	public TestSalesPlanSource getRightSource() {
		return rightSource;
	}
	
	public void setRightSource(TestSalesPlanSource rightSource) {
		this.rightSource = rightSource;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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
