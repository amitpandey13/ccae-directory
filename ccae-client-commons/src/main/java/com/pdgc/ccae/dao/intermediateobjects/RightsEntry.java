package com.pdgc.ccae.dao.intermediateobjects;

import java.time.LocalDate;
import java.util.Set;

import com.pdgc.general.structures.rightsource.DealProduct;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;

import lombok.Data;

/**
 *
 */
@Data
@SuppressWarnings({"PMD.AbstractNaming","PMD.AbstractClassWithoutAbstractMethod"})
public abstract class RightsEntry {
    protected Long rightId;

	protected Long rightTypeId;
	protected Integer episodeLimit;

	protected Long productHierarchyId;

	protected Set<Long> productIds;
	protected Set<Long> territoryIds;
	protected Set<Long> languageIds;
	protected Set<Long> mediaIds;

	protected LocalDate startDate;
	protected LocalDate endDate;
	protected LocalDate originalStartDate;
	protected LocalDate originalEndDate;

	protected Integer sourceTypeId;
	protected Long sourceId;
	protected String sourceDetailId;
	protected Long businessUnitId;
	protected String rightsGroupId;
	protected Set<String> comments;

	protected Long statusId;

	// sales window lifecycle
	protected String lifecycleId;

	protected Long customerId;
	protected Set<CarveOutEntry> carveOutEntries;

	protected Integer calculationOrder;
	protected Long parentSourceId;
	protected Integer parentMediaId;

	protected Long distributionRightsOwnerId;
	protected DealProduct dealProduct;
	protected String displaySourceType;
	protected String reservationTypeId;
	protected FoxSalesWindowProduct windowProduct;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (rightId ^ (rightId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RightsEntry other = (RightsEntry) obj;
		return (rightId == other.rightId);

	}

	@Override
	public String toString() {
		return "RightsEntry [rightId=" + rightId + ", rightTypeId=" + rightTypeId + ", product=" + productIds
				+ ", territory=" + territoryIds + ", language=" + languageIds + ", media=" + mediaIds
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", sourceTypeId=" + sourceTypeId + ", sourceId="
				+ sourceId + ", sourceDetailId=" + sourceDetailId + ", customerId=" + customerId + "]";
	}
}
