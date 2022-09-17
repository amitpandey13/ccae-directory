package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.CustomerLimitType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.carveout.impl.CustomerCountCarveOutHelper.CarveOutActionSupplierParams;
import com.pdgc.general.structures.classificationEnums.RightTypeType;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.rightsource.impl.FoxDealSource;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.util.CollectionsUtil;

import lombok.Builder;

/**
 * CustomerCountCarveOut is a carveOut that mainly represents the number
 * of unique customers you can have on the rightStrand in question.
 *
 * @author thomas
 */

public class CustomerCountCarveOut extends FoxCarveOut {

    private static final long serialVersionUID = 1L;

    private int maxCustomers;
    private int internalBrandedCustomerCount; // = 0 but this represents the number of customers that represent just 1
    private Set<Customer> internalBrandedCustomers;
    private CustomerLimitType customerLimitType; // Set customerLimitType = 2
    private boolean simultaneousCustomersAllowed; // Means multiple customers can exist as long as term doesn't overlap with more than the max
    
    /**
     * The pmtl dimensions we count across. 
     * Ex: if spanningDimensions contains media, then customers in FTV and PTV will affect each other
     * Ex: if spanningDimensions contains media and territory, then customers in FTV/Canada and BASC/USA will affect each other
     */
    private Set<MTLDimension> spanningDimensions; 
    
    private Set<CustomerCountLicense> existingLicenses;
    private List<Long> mediaIds;
    private List<Long> territoryIds;
    private List<Long> languageIds;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Builder
    public CustomerCountCarveOut(
        Long carveOutId,
        FoxCarveOutType carveOutType, // This type doesn't matter at all
        Term origTerm,
        Collection<TimePeriodPart> timePeriodParts,
        String carveOutComment,
        int maxCustomers,
        int internalBrandedCustomerCount,
        Collection<Customer> internalBrandedCustomers,
        CustomerLimitType customerLimitType,
        boolean simultaneousCustomersAllowed,
        Collection<MTLDimension> spanningDimensions,
        Collection<CustomerCountLicense> existingLicenses,
        CarveOutImpactType carveOutImpactType,
        CarveOutCombineRule carveOutCombineRule,
        Integer carveOutOrder,
        Integer carveOutGroupId,
        CarveOutCombineRule carveOutGroupCombineRule,
        Integer carveOutGroupOrder,
        List<Long> mediaIds,
        List<Long> territoryIds,
        List<Long> languageIds
    ) {
        super(carveOutId, carveOutType, origTerm, timePeriodParts, carveOutComment, carveOutImpactType, carveOutCombineRule, carveOutOrder, carveOutGroupId, carveOutGroupCombineRule, carveOutGroupOrder);

        this.maxCustomers = maxCustomers;
        this.internalBrandedCustomerCount = internalBrandedCustomerCount;
        this.customerLimitType = customerLimitType;
        this.simultaneousCustomersAllowed = simultaneousCustomersAllowed;
        this.spanningDimensions = ImmutableSet.copyOf(spanningDimensions);
        this.existingLicenses = ImmutableSet.copyOf(existingLicenses);
        this.internalBrandedCustomers = new HashSet<>(internalBrandedCustomers);
        this.mediaIds = mediaIds;
        this.territoryIds = territoryIds;
        this.languageIds = languageIds;

        StringBuilder carveOutStringBuilder = new StringBuilder();
        carveOutStringBuilder.append("Max Customer Count ").append(maxCustomers);
        carveOutStringBuilder.append(", ").append(simultaneousCustomersAllowed ? "Simultaneous" : "Non-Simultaneous");
        if (!internalBrandedCustomers.isEmpty()) {
            carveOutStringBuilder.append(", Internal Customers (").append(internalBrandedCustomers.stream().map(Customer::getCustomerName).collect(Collectors.joining(", "))).append(')');
        }
        if (existingLicenses != null && !existingLicenses.isEmpty()) {
        	carveOutStringBuilder.append(", Existing Customers");
        	Set<String> customerStrings = new HashSet<>(); 
        	for (CustomerCountLicense license : existingLicenses) {
        		Customer customer = license.getCustomer(); 
        		StringBuilder customerStringBuilder = new StringBuilder();
                customerStringBuilder.append(
                        (customer.getCustomerNameAndId().isEmpty()) ? "No Customer" : customer.getCustomerNameAndId());

                if (license.getRightSource() instanceof FoxDealSource) {
                    FoxDealSource rightSource = (FoxDealSource) license.getRightSource();
                    customerStringBuilder.append(" / ")
                            .append(rightSource.getDisplaySourceId())
                            .append(" (").append(rightSource.getDisplaySourceType()).append(") (").append(rightSource.getBusinessUnit().getCode()).append(')');

                }

                customerStrings.add(customerStringBuilder.toString());
        	}
        	carveOutStringBuilder.append(" (").append(String.join(", ", customerStrings)).append(')');
        }
        if (super.getCarveOutTerm() != null) {
            carveOutStringBuilder.append(" from ").append(super.getCarveOutTerm());
        }
        super.setCarveOutString(carveOutStringBuilder.toString());
    }
    
    public static boolean isConsideredLicense(RightType rightType) {
        return (rightType != null && (rightType.getRightTypeType() == RightTypeType.EXHIBITION || rightType.getRightTypeType() == RightTypeType.EXCLUSIVE_EXHIBITION));
    }

    @Override
    public CarveOutResult getCarveOutImpact(CarveOutImpactRequest request) {
        RightStrandCarveOutAction gapAction = RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
        Term relevantTerm = request.term != null ? Term.getIntersectionTerm(request.term, super.getCarveOutTerm()) : super.getCarveOutTerm();
        TimePeriod relevantPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, super.getCarveOutTimePeriod()) : super.getCarveOutTimePeriod();

        if (relevantTerm == null || relevantPeriod.isEmpty()) {
            return new CarveOutResult(new HashMap<>(), gapAction);
        }

        //Ignore the carve-out if the request is not an exhibition
        if (!isConsideredLicense(request.rightType)) {
            Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> termMap = new HashMap<>();
            termMap.put(relevantTerm, new HashMap<>());
            termMap.get(relevantTerm).put(relevantPeriod, RightStrandCarveOutAction.APPLY_RIGHT_STRAND);
            return new CarveOutResult(termMap, gapAction);
        }

        Map<Term, Map<TimePeriod, Collection<CustomerCountLicense>>> licenseTermMap = CustomerCountCarveOutHelper.groupCustomerLicenses(
            existingLicenses,
            relevantTerm,
            relevantPeriod
        );

        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> impactMap = CustomerCountCarveOutHelper.getImpactMap(
            request,
            licenseTermMap,
            this::getCarveOutAction
        );

        return new CarveOutResult(impactMap, gapAction);
    }

    private RightStrandCarveOutAction getCarveOutAction(
        CarveOutActionSupplierParams params
    ) {
        RightStrandCarveOutAction rsAction;

        int effectiveCustomerLimit;

        switch (customerLimitType) {
            case INCLUDES_INTERNAL_BRANDED:
                if (isInternalBrandedCustomer(params.request.customer)) {
                    effectiveCustomerLimit = 1; // allow internally branded always
                } else {
                    effectiveCustomerLimit = maxCustomers - internalBrandedCustomerCount;
                    effectiveCustomerLimit -= params.existingTermPeriodLicenses.stream()
                        .map(c -> c.getCustomer().getCustomerId())
                        .collect(Collectors.toSet())
                        .size();
                }
                break;
            case IN_ADDITION_TO_INTERNAL_BRANDED:
                if (isInternalBrandedCustomer(params.request.customer)) {
                    effectiveCustomerLimit = 1; // allow internally branded always
                } else {
                    effectiveCustomerLimit = maxCustomers;
                    effectiveCustomerLimit -= params.existingTermPeriodLicenses.stream()
                        .filter(c -> !isInternalBrandedCustomer(c.getCustomer()))
                        .map(c -> c.getCustomer().getCustomerId())
                        .collect(Collectors.toSet())
                        .size();
                }
                break;
            default:
                effectiveCustomerLimit = maxCustomers;
                effectiveCustomerLimit -= params.existingTermPeriodLicenses.stream()
                    .map(c -> c.getCustomer().getCustomerId())
                    .collect(Collectors.toSet())
                    .size();
                break;
        }

        boolean partOfExisting = false;
        if (params.request.customer != null) {
            Set<Customer> existingCustomers = new HashSet<>();
            for (CustomerCountLicense license : params.existingTermPeriodLicenses) {
                existingCustomers.add(license.getCustomer());
            }

            partOfExisting = CustomerCarveOutHelper.containsCustomer(params.request.customer, existingCustomers);
        }

        // decide rsAction here
        if (!simultaneousCustomersAllowed) {
            if (effectiveCustomerLimit < 0 || (!partOfExisting && effectiveCustomerLimit == 0)) {
                if (params.request.customer == null) {
                    rsAction = RightStrandCarveOutAction.CONDITIONAL;
                } else {
                    rsAction = RightStrandCarveOutAction.BLOCKED;
                }
            } else {
                rsAction = RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;
            }
        } else if (effectiveCustomerLimit > 0) {
            // if you have space then allow
            rsAction = RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;
        } else if (partOfExisting && effectiveCustomerLimit == 0) {
            // if you're completely filled (non-negative effectiveCustomerLimit though) but the customer
            // is already registered as one of the occupants. Then allow
            rsAction = RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;
        } else if (params.request.customer == null) {
            // If customer can't be identified then conditional
            rsAction = RightStrandCarveOutAction.CONDITIONAL;
        } else {
            // else apply
            rsAction = RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
        }

        return rsAction;
    }

    /**
     * General method that does a rough calculation of how restrictive the carveOut is.
     * The lower the number, the more restrictive. The higher, the less restrictive.
     * <p>
     * Example: A carveOut with restrictionValue of 1 will be more restrictive
     * than a carveOut with restrictionValue 10
     * <p>
     *
     * @return
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public long calculateRestrictionValue() {
        long result = 0;

        // calculate the spanning dimension value
        long spanningDimensionValue = 1;
        if (!CollectionsUtil.isNullOrEmpty(this.spanningDimensions)) {
            spanningDimensionValue = spanningDimensionValue
                * (this.spanningDimensions.contains(MTLDimension.MEDIA) ? 33 : 1)
                * (this.spanningDimensions.contains(MTLDimension.TERRITORY) ? 934 : 1)
                * (this.spanningDimensions.contains(MTLDimension.LANGUAGE) ? 124 : 1);
        }

        // calculate the value of simultaneous
        long simultaneousValue = 1;
        if (this.simultaneousCustomersAllowed) {
            simultaneousValue = this.maxCustomers;
        }

        result = this.maxCustomers * simultaneousValue * spanningDimensionValue;
        return result;
    }

    private boolean isInternalBrandedCustomer(Customer customer) {
        return internalBrandedCustomers.contains(customer);
    }

    public int getMaxCustomers() {
        return maxCustomers;
    }

    public boolean isSimultaneousCustomersAllowed() {
        return simultaneousCustomersAllowed;
    }

    public Set<MTLDimension> getSpanningDimensions() {
        return spanningDimensions;
    }

    public Set<CustomerCountLicense> getExistingLicenses() {
        return existingLicenses;
    }

    public void setExistingLicenses(Collection<CustomerCountLicense> existingLicenses) {
        this.existingLicenses = ImmutableSet.copyOf(existingLicenses);
    }

    public Set<Customer> getInternalBrandedCustomers() {
        return internalBrandedCustomers;
    }

    public void setInternalBrandedCustomers(Set<Customer> internalBrandedCustomers) {
        this.internalBrandedCustomers = internalBrandedCustomers;
    }

	public List<Long> getMediaIds() {
		return mediaIds;
	}

	public List<Long> getTerritoryIds() {
		return territoryIds;
	}

	public List<Long> getLanguageIds() {
		return languageIds;
	}


	
}
