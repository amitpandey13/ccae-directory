package com.pdgc.ccae.dao.intermediateobjects;

import java.time.LocalDate;
import java.util.Set;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightsource.DealProduct;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * The RightsEntry object contains all data related to the right strand
 * 
 * @author gowtham
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AvailsRightsEntry extends RightsEntry {

    private String currencyId;
    private Product elevatedProduct;
	private Set<Long> rightIdSet;
	private Boolean hasCarveOut;
	
	//we appear to like to randomly choose a single product 
	//from the collection of products and operate on it, so store it...
	private ProductLevel productLevel; 
    
	private AggregateMedia rightsGroupMedia;
	private AggregateTerritory rightsGroupTerritory;
	private AggregateLanguage rightsGroupLanguage;
	
	private Set<Long> leafProductSet;
	
	private boolean isExcluded;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@Builder(toBuilder = true)
	public AvailsRightsEntry(
		Long rightId,
		Long rightTypeId,
		Integer episodeLimit,
		Long productHierarchyId,
		Set<Long> productIds,
		Set<Long> territoryIds,
		Set<Long> languageIds,
		Set<Long> mediaIds,
		LocalDate startDate,
		LocalDate endDate,
		Integer sourceTypeId,
		Long sourceId,
		String sourceDetailId,
		Long businessUnitId,
		String rightsGroupId,
		Set<String> comments,
		Long statusId,
		Long customerId,
		Set<CarveOutEntry> carveOutEntries,
		Integer calculationOrder,
		Long parentSourceId,
		Integer parentMediaId,
		Long distributionRightsOwnerId,
		DealProduct dealProduct,
		FoxSalesWindowProduct windowProduct,
		String displaySourceType,
		String reservationTypeId,
		String lifecycleId,
		String currencyId,
		Set<Long> rightIdSet,
		Boolean hasCarveOut,
		ProductLevel productLevel,
		AggregateMedia rightsGroupMedia,
		AggregateTerritory rightsGroupTerritory,
		AggregateLanguage rightsGroupLanguage,
		Product elevatedProduct,
		Set<Long> leafProductSet,
		boolean isExcluded
	) {
		this.setRightId(rightId);
		this.setRightTypeId(rightTypeId);
		this.setEpisodeLimit(episodeLimit);
		this.setProductHierarchyId(productHierarchyId);
		this.setProductIds(productIds);
		this.setTerritoryIds(territoryIds);
		this.setLanguageIds(languageIds);
		this.setMediaIds(mediaIds);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setOriginalStartDate(startDate);
		this.setOriginalEndDate(endDate);
		this.setSourceTypeId(sourceTypeId);
		this.setSourceId(sourceId);
		this.setSourceDetailId(sourceDetailId);
		this.setBusinessUnitId(businessUnitId);
		this.setRightsGroupId(rightsGroupId);
		this.setComments(comments);
		this.setStatusId(statusId);
		this.setCustomerId(customerId);
		this.setCarveOutEntries(carveOutEntries);
		this.setCalculationOrder(calculationOrder);
		this.setParentSourceId(parentSourceId);
		this.setParentMediaId(parentMediaId);
		this.setDistributionRightsOwnerId(distributionRightsOwnerId);
		this.setDealProduct(dealProduct);
		this.setWindowProduct(windowProduct);
		this.setDisplaySourceType(displaySourceType);
		this.setReservationTypeId(reservationTypeId);
		this.setLifecycleId(lifecycleId);
		this.currencyId = currencyId;
		this.rightIdSet = rightIdSet;
		this.hasCarveOut = hasCarveOut;
		this.rightsGroupMedia = rightsGroupMedia;
		this.rightsGroupTerritory = rightsGroupTerritory;
		this.rightsGroupLanguage = rightsGroupLanguage;

		this.productLevel = productLevel;
		this.elevatedProduct = elevatedProduct;

		this.leafProductSet = leafProductSet;
		
		this.isExcluded = isExcluded;
	}

}
