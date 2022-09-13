package com.pdgc.general.structures.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.pdgc.general.lookup.Constants;

public class Customer implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	private Long customerId;
	private Long businessUnitId;
	private String customerName;
	private Collection<CustomerType> customerTypes;
	private Collection<CustomerGenre> customerGenres;
	private Collection<CustomerGroup> customerGroups;

	
	public static class CustomerBuilder {
		private Long customerId;
		private Long businessUnitId;
		private String customerName = "";
		private Collection<CustomerType> customerTypes = new ArrayList<>();
		private Collection<CustomerGenre> customerGenres = new ArrayList<>();
		private Collection<CustomerGroup> customerGroups = new ArrayList<>();
		
		public CustomerBuilder(Long customerId, Long businessUnitId) {
			this.customerId = customerId;
			this.businessUnitId = businessUnitId;
		}
		
		public CustomerBuilder customerName(String customerName) {
			this.customerName = customerName;
			return this;
		}
		
		public CustomerBuilder customerTypes(Collection<CustomerType> customerTypes) {
			this.customerTypes = customerTypes;
			return this;
		}
		
		public CustomerBuilder customerGenres(Collection<CustomerGenre> customerGenres) {
			this.customerGenres = customerGenres;
			return this;
		}
		
		public CustomerBuilder customerGroups(Collection<CustomerGroup> customerGroups) {
			this.customerGroups = customerGroups;
			return this;
		}
		
		public Customer build() {
			return new Customer(this);
		}
	}
	
	public Customer(CustomerBuilder builder) {
		this.customerId = builder.customerId;
		this.businessUnitId = builder.businessUnitId;
		this.customerName = builder.customerName;
		this.customerTypes = builder.customerTypes;
		customerGenres = builder.customerGenres;
		customerGroups = builder.customerGroups;
	}
	
	/**
	 * Copy Constructor
	 */
	public Customer(Customer customer) {
		customerId = customer.customerId;
		businessUnitId = customer.businessUnitId;
		customerName = customer.customerName;
		
		customerTypes = new ArrayList<CustomerType>(customer.customerTypes);
		customerGenres = new ArrayList<CustomerGenre>(customer.customerGenres);
		customerGroups = new ArrayList<CustomerGroup>(customer.customerGroups);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return customerId.equals(((Customer) obj).customerId);
	}

	@Override
	public int hashCode() {
		return customerId.hashCode();
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}
	
	public String getCustomerNameAndId() {
		if (customerId == null) {
            return "";
        }
		else if(customerId.equals(Constants.NULL_CUSTOMER_ID)) {
		    return "No Customer";
        }
		else {
            return customerName + " (" + customerId + ")";
        }
	}

	public Collection<CustomerType> getCustomerTypes() {
		return customerTypes;
	}

	public Collection<CustomerGenre> getCustomerGenres() {
		return customerGenres;
	}
	
	public Collection<CustomerGroup> getCustomerGroups() {
		return customerGroups;
	}

	public Long getBusinessUnitId() {
		return businessUnitId;
	}

	@Override
	public String toString() {
		return 	customerName + " " + customerGroups.toString();
	}
}
