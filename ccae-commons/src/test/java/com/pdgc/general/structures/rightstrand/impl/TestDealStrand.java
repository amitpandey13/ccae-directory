package com.pdgc.general.structures.rightstrand.impl;

import java.util.Objects;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.impl.TestDealSource;
import com.pdgc.general.structures.rightstrand.TestRightStrand;

public class TestDealStrand extends TestRightStrand implements DealRightStrand {

	private static final long serialVersionUID = 1L;
    
	private TestDealSource rightSource;
	private Customer customer;
	private CarveOutContainer carveOuts;
	
	public TestDealStrand (
		long rightStrandId,
		PMTL pmtl,
		TermPeriod termPeriod,
		TestDealSource rightSource,
		RightType rightType,
		PMTL actualPMTL,
		Term actualTerm,
		boolean isCheckedIn,
		Customer customer,
		CarveOutContainer carveOuts
	) {
		super(rightStrandId, pmtl, termPeriod, rightType, actualPMTL, actualTerm, "dummy " + rightType.getRightTypeDesc(), isCheckedIn);
		this.rightSource = rightSource;
		this.customer = customer;
		this.carveOuts = carveOuts;
	}

	public TestDealStrand(TestDealStrand rs) {
		super(rs);
		this.rightSource = rs.rightSource;
		this.customer = rs.customer;
		this.carveOuts = rs.carveOuts;
	}
	
	@Override
	public boolean additionalGroupingEquals(RightStrand obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		return Objects.equals(customer, ((TestDealStrand)obj).customer)
			&& Objects.equals(carveOuts, ((TestDealStrand)obj).carveOuts)
		;
	}
	
	public TestDealSource getRightSource() {
		return rightSource;
	}
	
	public void setRightSource(TestDealSource rightSource) {
		this.rightSource = rightSource;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public CarveOutContainer getCarveOuts() {
		return carveOuts;
	}
	
	public void setCarveOuts(CarveOutContainer carveOutContainer) {
		this.carveOuts = carveOutContainer;
	}

	@Override
	public Long getDealId() {
		return getRightSource().getSourceId();
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

