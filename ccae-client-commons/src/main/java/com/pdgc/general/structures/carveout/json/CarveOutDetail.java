package com.pdgc.general.structures.carveout.json;

public class CarveOutDetail {

	private int[] customerIds;
	private int[] customerTypeIds;
	private int[] internalCustomerIds;
	private int[] customerGenreIds;
	private int customerLimit;
	private int customerLimitTypeId;
	private int internalCustomerCount;
	private int simultaneousCustomersNotAllowed;
	private char[] spanningDimensions; //'m', 't', 'l'
	
	public CarveOutDetail() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

	public int[] getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(int... customerIds) {
		this.customerIds = customerIds;
	}

	public int[] getCustomerTypeIds() {
		return customerTypeIds;
	}

	public void setCustomerTypeIds(int... customerTypeIds) {
		this.customerTypeIds = customerTypeIds;
	}

	public int[] getInternalCustomerIds() {
		return internalCustomerIds;
	}

	public void setInternalCustomerIds(int... internalCustomerIds) {
		this.internalCustomerIds = internalCustomerIds;
	}

	public int getCustomerLimit() {
		return customerLimit;
	}

	public void setCustomerLimit(int customerLimit) {
		this.customerLimit = customerLimit;
	}

	public int getCustomerLimitTypeId() {
		return customerLimitTypeId;
	}

	public void setCustomerLimitTypeId(int customerLimitTypeId) {
		this.customerLimitTypeId = customerLimitTypeId;
	}

	public int getInternalCustomerCount() {
		return internalCustomerCount;
	}

	public void setInternalCustomerCount(int internalCustomerCount) {
		this.internalCustomerCount = internalCustomerCount;
	}

	public int getSimultaneousCustomersNotAllowed() {
		return simultaneousCustomersNotAllowed;
	}

	public void setSimultaneousCustomersNotAllowed(int simultaneousCustomersNotAllowed) {
		this.simultaneousCustomersNotAllowed = simultaneousCustomersNotAllowed;
	}

	public int[] getCustomerGenreIds() {
		return customerGenreIds;
	}

	public void setCustomerGenreIds(int... customerGenreIds) {
		this.customerGenreIds = customerGenreIds;
	}

	public char[] getSpanningDimensions() {
		return spanningDimensions;
	}
	
	public void setSpanningDimensions(char... spanningDimensions) {
		this.spanningDimensions = spanningDimensions;
	}
}
