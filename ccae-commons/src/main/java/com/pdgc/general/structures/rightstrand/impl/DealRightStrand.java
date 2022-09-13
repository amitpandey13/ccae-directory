package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.customer.Customer;

public interface DealRightStrand extends RightStrand {

	public Long getDealId();
	public Customer getCustomer();
	public CarveOutContainer getCarveOuts();
}
