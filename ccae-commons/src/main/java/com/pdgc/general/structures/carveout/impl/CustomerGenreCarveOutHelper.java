package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;

import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.CustomerGenre;

public class CustomerGenreCarveOutHelper {

	public static boolean matchesGenre(
		Customer customer, 
		Collection<CustomerGenre> relevantGenres
	) {
		for (CustomerGenre genre : customer.getCustomerGenres()) {
			if (relevantGenres.contains(genre)) {
				return true;
			}
		}
		
		return false;
	}
}
