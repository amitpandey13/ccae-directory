package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxSalesBlockStrand;

/**
 * A sales plan block
 * @author Linda Xu
 *
 */
public class FoxSalesBlockRightStrand extends FoxSalesPlanRightStrand implements FoxSalesBlockStrand {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public FoxSalesBlockRightStrand(
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
    	Long statusId
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
	}
}
