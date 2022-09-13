package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;

import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.CustomerGroup;
import com.pdgc.general.util.CollectionsUtil;

public class CustomerCarveOutHelper {

	public static boolean containsCustomer(
		Customer customer, 
		Collection<Customer> relevantCustomers
	) {
		return relevantCustomers.contains(customer);
	}
	
	public static boolean containsCustomerGroup(
		Customer customer, 
		Collection<Customer> relevantCustomers
	) {
		for (CustomerGroup customerGroup : customer.getCustomerGroups()) {
			if (CollectionsUtil.any(relevantCustomers, c -> c.getCustomerGroups().contains(customerGroup))) {
				return true;
			}
		}
		return false;
	}
}
