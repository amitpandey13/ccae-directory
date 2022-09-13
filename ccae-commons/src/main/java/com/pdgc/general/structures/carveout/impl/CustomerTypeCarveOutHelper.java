package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;

import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.CustomerType;

public class CustomerTypeCarveOutHelper {

	public static boolean matchesType(
		Customer customer, 
		Collection<CustomerType> relevantTypes
	) {
		for (CustomerType customerType : customer.getCustomerTypes()) {
			if (relevantTypes.contains(customerType)) {
				return true;
			}
		}
		
		return false;
	}
}
