package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.FoxSalesWindow;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;
import com.pdgc.general.structures.rightstrand.FoxSalesWindowStrand;

public class FoxSalesWindowRightStrand extends FoxSalesPlanRightStrand implements FoxSalesWindowStrand {

	private static final long serialVersionUID = 1L;

	private FoxSalesWindow salesWindow; //window strand *needs* to have the actual window info...block strands don't necessarily need them
	private FoxSalesWindowProduct salesWindowProduct;
	
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public FoxSalesWindowRightStrand(
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
    	Long statusId,
    	FoxSalesWindow salesWindow,
    	FoxSalesWindowProduct salesWindowProduct
	) {
		super(
			rightStrandId, 
			pmtl, 
			term, 
			rightSource, 
			rightType, 
			actualPMTL, 
			origTerm, 
			comment, 
			checkedIn, 
			productHierarchyId, 
			distributionRightsOwner, 
			customer, 
			statusId
		);
		
		this.salesWindow = salesWindow;
		this.salesWindowProduct = salesWindowProduct;
	}
	
	public FoxSalesWindow getSalesWindow() {
		return salesWindow;
	}
	
	public FoxSalesWindowProduct getSalesWindowProduct() {
		return salesWindowProduct;
	}
}
