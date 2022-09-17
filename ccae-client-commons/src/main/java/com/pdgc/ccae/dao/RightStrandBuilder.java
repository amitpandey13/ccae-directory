package com.pdgc.ccae.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.pdgc.ccae.dao.intermediateobjects.RightsEntry;
import com.pdgc.general.cache.dictionary.impl.BusinessUnitDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerDictionary;
import com.pdgc.general.cache.dictionary.impl.KeyWithBusinessUnit;
import com.pdgc.general.cache.dictionary.impl.RightTypeDictionary;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.FoxSalesWindow;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.CarveOutGroup;
import com.pdgc.general.structures.carveout.grouping.FoxCarveOutContainer;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.Customer.CustomerBuilder;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxDealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxDistributionRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxRestrictionRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxSalesBlockRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxSalesWindowDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxSalesWindowRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.util.FoxPMTLUtil;

/**
 * RightStrandBuilder class takes in the current SearchCriteria and a list of Rights returned from the query (both Corporate and Deal).
 * A HashSet of RightStrands will be created from the dictionary of Rights.
 * 
 * @author gowtham
 *
 */
public final class RightStrandBuilder {

    private RightStrandBuilder() {
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public static FoxRightStrand createRightStrand(
		RightsEntry entry,
		Customer criteriaCustomer,
		Map<Long, FoxSalesWindow> salesWindowDictionary
	) {
        //TODO: this is to fix data error with dates in database
        if (entry.getStartDate() == null || entry.getEndDate() == null || entry.getStartDate().isAfter(entry.getEndDate())) {
            return null;
        }

        FoxRightStrand rightStrand = null;
		
		PMTL pmtl = new PMTL(
	        new AggregateProduct(FoxPMTLUtil.getProductsFromIds(entry.getProductIds(), entry.getProductHierarchyId())),
	        new AggregateMedia(FoxPMTLUtil.getMediasFromIds(entry.getMediaIds())),
	        new AggregateTerritory(FoxPMTLUtil.getTerritoriesFromIds(entry.getTerritoryIds())),
	        new AggregateLanguage(FoxPMTLUtil.getLanguagesFromIds(entry.getLanguageIds()))
		);

		Long productHierarchyId = entry.getProductHierarchyId();

		Term rightStrandTerm = new Term(entry.getStartDate(), entry.getEndDate());
		Term originalTerm = new Term(
			(entry.getOriginalStartDate() != null) ? entry.getOriginalStartDate() : entry.getStartDate(),
			(entry.getOriginalEndDate() != null) ? entry.getOriginalEndDate() : entry.getEndDate()
		);
		RightType rightType = RightTypeDictionary.getInstance().get(entry.getRightTypeId());
		if (entry.getEpisodeLimit() != null) {
			rightType = rightType.toBuilder().episodeLimit(entry.getEpisodeLimit()).build();
		}

		FoxRightSource rightSource = RightSourceBuilder.getRightSource(
			entry.getSourceTypeId(),
			entry.getSourceId(),
			entry.getSourceDetailId(),
			BusinessUnitDictionary.getInstance().get(entry.getBusinessUnitId()),
			entry.getDisplaySourceType()
		);

		String comment = entry.getComments().stream().filter(c -> !StringUtils.isBlank(c)).collect(Collectors.joining("; "));

		boolean isCheckedIn = true; //TODO
		Long statusId = entry.getStatusId();

		Long distributionRightsOwner = entry.getDistributionRightsOwnerId();
		Customer customer = entry.getCustomerId() == null 
				? null
				: (CustomerDictionary.getInstance().get(new KeyWithBusinessUnit<>(entry.getCustomerId(), entry.getBusinessUnitId())) != null)
				? CustomerDictionary.getInstance().get(new KeyWithBusinessUnit<>(entry.getCustomerId(), entry.getBusinessUnitId()))
				// If the customer doesn't exist in CustomerDictionary, supplant with a "dummy" customer so that further processing doesn't break
				: new CustomerBuilder(entry.getCustomerId(), entry.getBusinessUnitId()).customerName("Customer " + entry.getCustomerId()).build();

		int sourceTypeId = entry.getSourceTypeId();       
        
        if (sourceTypeId == Constants.SOURCE_TYPE_ID_DEAL) {
          //TODO: before database supports timePeriod, make this field null, we'll assume it's full week
            List<TimePeriodPart> timePeriodParts = null;

            Collection<CarveOutGroup> carveOutGroups = new ArrayList<>();
            if (entry.getCarveOutEntries() != null) {
                carveOutGroups = CarveOutBuilder.createCarveOutGroups(entry.getCarveOutEntries());
            }

            /**
             * TODO last null field is the contract type. Contract type should be considered in Avails calculation as it is in
             * in conflict check where even if 2 customers are in the same customer group, if the 2 rightstrands are Contract vs Reservation
             * then there should still be a conflict
            **/ 
            rightStrand = new FoxDealRightStrand(
                entry.getRightId(),
                pmtl,
                rightStrandTerm,
                rightSource,
                rightType,
                pmtl,
                originalTerm,
                comment,
                isCheckedIn,
                productHierarchyId,
                distributionRightsOwner,
                customer,
                new FoxCarveOutContainer(
                    originalTerm,
                    timePeriodParts == null ? TimePeriod.FULL_WEEK : new TimePeriod(timePeriodParts),
                    carveOutGroups
                ),
                timePeriodParts,
                statusId,
                entry.getDealProduct(),
                null
            );
        } else if (sourceTypeId == Constants.SOURCE_TYPE_ID_DISTRIBUTION) {
            rightStrand = new FoxDistributionRightStrand(
                entry.getRightId(),
                pmtl,
                rightStrandTerm,
                rightSource,
                rightType,
                pmtl,
                originalTerm,
                comment,
                isCheckedIn,
                productHierarchyId,
                distributionRightsOwner
            );
        } else if (sourceTypeId == Constants.SOURCE_TYPE_ID_RESTRICTION
                && (entry.getCustomerId() == null 
                    || entry.getCustomerId().equals(Constants.NULL_CUSTOMER_ID)
                    || criteriaCustomer == null 
                    || entry.getCustomerId() == criteriaCustomer.getCustomerId())) {
            rightStrand = new FoxRestrictionRightStrand(
                entry.getRightId(),
                pmtl,
                rightStrandTerm,
                rightSource,
                rightType,
                pmtl,
                originalTerm,
                comment,
                isCheckedIn,
                productHierarchyId,
                distributionRightsOwner,
                null
            );
        } else if (sourceTypeId == Constants.SOURCE_TYPE_ID_PRODUCT_RESTRICTION) {
            rightStrand = new FoxRestrictionRightStrand(
                entry.getRightId(),
                pmtl,
                rightStrandTerm,
                rightSource,
                rightType,
                pmtl,
                originalTerm,
                comment,
                isCheckedIn,
                productHierarchyId,
                distributionRightsOwner,
                null
            );
        } else if (sourceTypeId == Constants.SOURCE_TYPE_ID_SALES_PLAN) {
            if (rightType.getRightTypeId().equals(Constants.RIGHT_TYPE_ID_SALES_PLAN_AS_DIST_RIGHTS)) {
                rightStrand = new FoxSalesWindowDistributionStrand(
                    entry.getRightId(),
                    pmtl,
                    rightStrandTerm,
                    rightSource,
                    rightType,
                    pmtl,
                    originalTerm,
                    comment,
                    isCheckedIn,
                    productHierarchyId,
                    distributionRightsOwner,
                    customer,
                    entry.getStatusId(),
                    salesWindowDictionary.get(rightSource.getSourceId()),
                    entry.getWindowProduct()
                );
            } else if (Constants.RIGHT_TYPE_ID_SALES_PLAN_WINDOW.equals(rightType.getRightTypeId())) {
                Collection<Media> salesPlanMedias = FoxPMTLUtil.getMediasFromIds(Constants.SALESPLAN_SET_MEDIAS);
                
                // mask media so we don't filter out windows by media
                PMTL salesWindowPMTL = new PMTL(
                    pmtl.getProduct(),
                    new AggregateMedia(salesPlanMedias),
                    pmtl.getTerritory(),
                    pmtl.getLanguage()
                );
                
                rightStrand = new FoxSalesWindowRightStrand(
                    entry.getRightId(),
                    rightType.getRightTypeId().equals(Constants.RIGHT_TYPE_ID_SALES_PLAN_WINDOW) ? salesWindowPMTL : pmtl,
                    rightStrandTerm,
                    rightSource,
                    rightType,
                    pmtl,
                    originalTerm,
                    comment,
                    isCheckedIn,
                    productHierarchyId,
                    distributionRightsOwner,
                    customer,
                    entry.getStatusId(),
                    salesWindowDictionary.get(rightSource.getSourceId()),
                    entry.getWindowProduct()
                );
            } else {
                rightStrand = new FoxSalesBlockRightStrand(
                    entry.getRightId(),
                    pmtl,
                    rightStrandTerm,
                    rightSource,
                    rightType,
                    pmtl,
                    originalTerm,
                    comment,
                    isCheckedIn,
                    productHierarchyId,
                    distributionRightsOwner,
                    customer,
                    entry.getStatusId()
                );
            }
        }
		return rightStrand;
	}
}