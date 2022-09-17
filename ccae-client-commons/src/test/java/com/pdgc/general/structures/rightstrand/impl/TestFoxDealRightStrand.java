package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxDealStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class TestFoxDealRightStrand extends TestFoxRightStrand implements FoxDealStrand {

	private static final long serialVersionUID = 1L;
    
	protected CarveOutContainer carveOuts;
	protected Customer customer;
	protected Long statusId; 
	
	public TestFoxDealRightStrand(
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
    	Customer customer,
    	CarveOutContainer carveOuts,
    	Long statusId
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
		
		this.customer = customer;
		this.carveOuts = carveOuts;
		this.statusId = statusId; 
	}
	
	public TestFoxDealRightStrand(FoxDealStrand rs) {
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
	    	rs.getDistributionRightsOwner(),
	    	rs.getCustomer(),
	    	rs.getCarveOuts(),
	    	rs.getStatusId()
		);
	}
	
	@Override
	public Long getDealId() {
		return getRightSource().getSourceId();
	}
	
	@Override
	public Customer getCustomer() {
    	return customer;
    }
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public CarveOutContainer getCarveOuts() {
		return carveOuts;
	}
	
	public void setCarveOutContainer(CarveOutContainer carveOuts) {
		this.carveOuts = carveOuts;
	}
	
	@Override
	public Long getStatusId() {
		return statusId;
	}
	
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
}
