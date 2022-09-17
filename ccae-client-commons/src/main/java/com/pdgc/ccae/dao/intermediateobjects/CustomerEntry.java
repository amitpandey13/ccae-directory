package com.pdgc.ccae.dao.intermediateobjects;

import java.util.Set;

import com.pdgc.general.cache.dictionary.impl.KeyWithBusinessUnit;

import lombok.Builder;
import lombok.Getter;


/**
* 
*
* @author CCAE
*/


@Builder
public class CustomerEntry {

	private KeyWithBusinessUnit<Long> customerId;
	private String customerName;
	private Set<Long> customerTypes;
	private Set<Long> customerGenres;
	private Set<Long> customerGroups;
	
	
	
	public KeyWithBusinessUnit<Long> getCustomerId() {
		return customerId;
	}



	public void setCustomerId(KeyWithBusinessUnit<Long> customerId) {
		this.customerId = customerId;
	}



	public String getCustomerName() {
		return customerName;
	}



	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}



	public Set<Long> getCustomerTypes() {
		return customerTypes;
	}



	public void setCustomerTypes(Set<Long> customerTypes) {
		this.customerTypes = customerTypes;
	}



	public Set<Long> getCustomerGenres() {
		return customerGenres;
	}



	public void setCustomerGenres(Set<Long> customerGenres) {
		this.customerGenres = customerGenres;
	}



	public Set<Long> getCustomerGroups() {
		return customerGroups;
	}



	public void setCustomerGroups(Set<Long> customerGroups) {
		this.customerGroups = customerGroups;
	}



	public String toString() {
		return customerName + " " + customerGroups.toString();
	}
}
