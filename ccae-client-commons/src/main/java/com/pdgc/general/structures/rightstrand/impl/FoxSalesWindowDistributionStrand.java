package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.FoxSalesWindow;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;
import com.pdgc.general.structures.rightstrand.FoxDistributionStrand;
import com.pdgc.general.structures.rightstrand.FoxSalesWindowStrand;

/**
 * Acts the same as a Sales Plan, but has a specific right type that indicates it also acts as Distribution Rights 
 * 
 * @author CLARA HONG
 *
 */
public class FoxSalesWindowDistributionStrand extends FoxSalesWindowRightStrand implements FoxDistributionStrand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long statusId;  //NOPMD

    @SuppressWarnings("PMD.ExcessiveParameterList")
	public FoxSalesWindowDistributionStrand(
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
	    	statusId,
			salesWindow,
			salesWindowProduct
		);
		
		this.statusId = statusId;
	}

	@Override
	public int getCalculationOrder() {
		return 1;
	}

    @Override
    public boolean additionalGroupingEquals(RightStrand obj) {
        if (!(obj instanceof FoxSalesWindowDistributionStrand)) {
            return false;
        }
        
        return FoxSalesWindowStrand.additionalEquals(this, (FoxSalesWindowStrand)obj)
            && FoxDistributionStrand.additionalEquals(this, (FoxDistributionStrand)obj)
        ;
    }
}
