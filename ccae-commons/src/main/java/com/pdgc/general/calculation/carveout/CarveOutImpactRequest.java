package com.pdgc.general.calculation.carveout;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class CarveOutImpactRequest {
	
	public Customer customer;
	public Term term;
	public TimePeriod timePeriod;
	public RightType rightType;
	public CarveOutContainer carveOutContainer;
     
    public CarveOutImpactRequest(
		Customer customer,
		Term term,
		TimePeriod timePeriod,
		RightType rightType,
		CarveOutContainer carveOutContainer
	) {
 	   this.customer = customer;
 	   this.term = term;
 	   this.timePeriod = timePeriod;
 	   this.rightType = rightType;
 	   this.carveOutContainer = carveOutContainer;
	}
}
