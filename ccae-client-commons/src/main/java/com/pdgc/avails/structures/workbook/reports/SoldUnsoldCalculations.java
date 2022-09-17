package com.pdgc.avails.structures.workbook.reports;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SoldUnsoldCalculations {
    private boolean hasRights;
    private boolean availsToSell;
    private boolean isSold;
    private boolean noRightsEntered;
    private String customerName;
    private String customerId;
    private Integer numOfPrimaryLanguages;
    private Set<Integer> primaryLanguageIds;
    private Integer numOfSoldPrimaryLanguages;
    
    

    public boolean isHasRights() {
		return hasRights;
	}

	public void setHasRights(boolean hasRights) {
		this.hasRights = hasRights;
	}

	public boolean isAvailsToSell() {
		return availsToSell;
	}

	public void setAvailsToSell(boolean availsToSell) {
		this.availsToSell = availsToSell;
	}

	public boolean isSold() {
		return isSold;
	}

	public void setSold(boolean isSold) {
		this.isSold = isSold;
	}

	public boolean isNoRightsEntered() {
		return noRightsEntered;
	}

	public void setNoRightsEntered(boolean noRightsEntered) {
		this.noRightsEntered = noRightsEntered;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Integer getNumOfPrimaryLanguages() {
		return numOfPrimaryLanguages;
	}

	public void setNumOfPrimaryLanguages(Integer numOfPrimaryLanguages) {
		this.numOfPrimaryLanguages = numOfPrimaryLanguages;
	}

	public Integer getNumOfSoldPrimaryLanguages() {
		return numOfSoldPrimaryLanguages;
	}

	public void setNumOfSoldPrimaryLanguages(Integer numOfSoldPrimaryLanguages) {
		this.numOfSoldPrimaryLanguages = numOfSoldPrimaryLanguages;
	}

	public Set<Integer> getPrimaryLanguageIds() {
		return primaryLanguageIds;
	}

	public SoldUnsoldCalculations() {
        hasRights = false;
        availsToSell = false;
        isSold = false;
        noRightsEntered = false;
        customerName = "";
        primaryLanguageIds = new HashSet<>();
        numOfSoldPrimaryLanguages = 0;
        numOfPrimaryLanguages = 0;
    }

    public void setPrimaryLanguageIds(Set<Integer> primaryLanguageIds) {
        this.primaryLanguageIds.addAll(primaryLanguageIds);
        this.numOfPrimaryLanguages = primaryLanguageIds.size();
    }

    public void removePrimaryLanguageId(Integer id) {
        this.primaryLanguageIds.remove(id);
    }

    public void incrementNumOfSoldPrimaryLanguages() {
        this.numOfSoldPrimaryLanguages++;
    }

    public boolean isNumOfSoldEqualToNumOfPrimary() {
        return this.getNumOfSoldPrimaryLanguages().equals(this.getNumOfPrimaryLanguages());
    }
}
