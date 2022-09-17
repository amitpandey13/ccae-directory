package com.pdgc.general.structures.carveout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.HashSet;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.RightSource;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;

class CustomerCountCarveOutTest {

	@BeforeAll
	private static void setup() {
		Constants.instantiateConstants(); 
	}
	
	@Test
	void given2UniqueCustomers_whenToString_then2CustomerStringInfos() {
		Customer customer1 = new CustomerBuilder(1L, 810L).customerName("Customer 1").build();
		Customer customer2 = new CustomerBuilder(2L, 810L).customerName("Customer 2").build();
		
		RightSource source1 = new FoxDealSource(
				FoxRightSourceType.DEAL, 
				1L,
				"sourceDetail 1",
				new FoxBusinessUnit(1L, "desc", "BUS"),
				1L, 
				"CONTRACT 1", "1");
		RightSource source2 = new FoxDealSource(
				FoxRightSourceType.DEAL, 
				2L,
				"sourceDetail 2",
				new FoxBusinessUnit(1L, "desc", "BUS"),
				1L, 
				"CONTRACT 2", "2");
		
		Collection<CustomerCountLicense> customerCountLicenses = new HashSet<>();
		customerCountLicenses.add(
				CustomerCountLicense.builder()
					.customer(customer1)
					.rightSource(source1)
					.build());
		customerCountLicenses.add(
				CustomerCountLicense.builder()
					.customer(customer2)
					.rightSource(source2)
					.build());
		
		CustomerCountCarveOut customerCountCarveOut = CustomerCountCarveOut.builder()
				.maxCustomers(1)
				.simultaneousCustomersAllowed(false)
				.existingLicenses(customerCountLicenses)
				.spanningDimensions(Sets.newHashSet())
				.internalBrandedCustomers(Lists.emptyList())
				.build();
		
		assertEquals("Max Customer Count 1, Non-Simultaneous, Existing Customers (Customer 2 (2) / 2 (CONTRACT 2) (BUS), Customer 1 (1) / 1 (CONTRACT 1) (BUS))",
				customerCountCarveOut.toString());
	}

	@Test
	void given2IdenticalCustomers_whenToString_then1CustomerStringInfo() {
		Customer customer1 = new CustomerBuilder(1L, 810L).customerName("Customer 1").build();
		
		RightSource source1 = new FoxDealSource(
				FoxRightSourceType.DEAL, 
				1L,
				"sourceDetail 1",
				new FoxBusinessUnit(1L, "desc", "BUS"),
				1L, 
				"CONTRACT 1", "1");
		
		Collection<CustomerCountLicense> customerCountLicenses = new HashSet<>();
		customerCountLicenses.add(
				CustomerCountLicense.builder()
					.customer(customer1)
					.rightSource(source1)
					.build());
		customerCountLicenses.add(
				CustomerCountLicense.builder()
					.customer(customer1)
					.rightSource(source1)
					.build());
		
		CustomerCountCarveOut customerCountCarveOut = CustomerCountCarveOut.builder()
				.maxCustomers(1)
				.simultaneousCustomersAllowed(false)
				.existingLicenses(customerCountLicenses)
				.spanningDimensions(Sets.newHashSet())
				.internalBrandedCustomers(Lists.emptyList())
				.build();
		
		assertEquals("Max Customer Count 1, Non-Simultaneous, Existing Customers (Customer 1 (1) / 1 (CONTRACT 1) (BUS))",
				customerCountCarveOut.toString());
	}
}
