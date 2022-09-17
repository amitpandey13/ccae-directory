package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;

import lombok.Builder;

/**
 * CustomerCarveOut is also better known as CustomerList CarveOut. 
 * It's a carveOut that has a list of customers that is allowed in the carveOut.
 * For example: A customerListCarveOut with customerId 1,2,3 will allow any conflictingRightStrands with 1, 2, or 3. 
 * 
 * <p>
 * While simple, when it interacts with other CustomerList CarveOuts you can get in dangerous noone applies situations. <p>
 * Example: <p>
 * <ul>
 * <li>CustomerListCO: customerId 1, AND, ORDER 1
 * <li>CustomerListCO: customerId 2, OR, ORDER 2
 * <li>CustomerListCO: customerId 1, AND, ORDER 3
 * </ul>
 * <p>
 * The result of this will end up being only 1 applies and you will never be able to get customerId 2 to apply
 * to the carveOutGroup these exist in. Be extra careful about how these combine in a single group.
 * 
 * @author Linda/Adam but javaDoc written by Thomas
 *
 */
public class CustomerCarveOut extends FoxCarveOut {
	
	private static final long serialVersionUID = 1L;
	
	private Set<Customer> carveOutCustomers;
	private List<Long> mediaIds;
	private List<Long> territoryIds;
	private List<Long> languageIds;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	@Builder
	public CustomerCarveOut(Long carveOutId, FoxCarveOutType carveOutType, Term origTerm,
			Collection<TimePeriodPart> timePeriodParts, String carveOutComment, Collection<Customer> carveOutCustomers,
			CarveOutImpactType carveOutImpactType, CarveOutCombineRule carveOutCombineRule, Integer carveOutOrder,
			Integer carveOutGroupId, CarveOutCombineRule carveOutGroupCombineRule, Integer carveOutGroupOrder,
			List<Long> mediaIds, List<Long> territoryIds, List<Long> languageIds) {
		super(carveOutId, carveOutType, origTerm, timePeriodParts, carveOutComment, carveOutImpactType,
				carveOutCombineRule, carveOutOrder, carveOutGroupId, carveOutGroupCombineRule, carveOutGroupOrder);

		this.carveOutCustomers = ImmutableSet.copyOf(carveOutCustomers);
		this.mediaIds = mediaIds;
		this.territoryIds = territoryIds;
		this.languageIds = languageIds;

		StringBuilder carveOutStringBuilder = new StringBuilder();
		carveOutStringBuilder.append(carveOutImpactType.toString());
		carveOutStringBuilder.append(" (");
		if (carveOutCustomers.isEmpty()) {
		    carveOutStringBuilder.append("No Customer");
        } else {
		    carveOutStringBuilder.append(carveOutCustomers.stream().map(Customer::getCustomerName).collect(Collectors.joining(", ")));
        }
		carveOutStringBuilder.append(')');
		if (super.getCarveOutTerm() != null) {
			carveOutStringBuilder.append(" from ").append(super.getCarveOutTerm());
		}
		super.setCarveOutString(carveOutStringBuilder.toString());
	}

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
	public CarveOutResult getCarveOutImpact(CarveOutImpactRequest request) {
		RightStrandCarveOutAction gapAction = super.getCarveOutImpactType() == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
		Term relevantTerm = request.term != null ? Term.getIntersectionTerm(request.term, super.getCarveOutTerm()) : super.getCarveOutTerm();
        TimePeriod relevantPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, super.getCarveOutTimePeriod()) : super.getCarveOutTimePeriod();
        
        if (relevantTerm == null || relevantPeriod.isEmpty()) {
        	return new CarveOutResult(new HashMap<>(), gapAction);
        }

        RightStrandCarveOutAction rsAction;
        if (request.customer == null) {
        	rsAction = RightStrandCarveOutAction.CONDITIONAL;
        } else {
            if (CustomerCarveOutHelper.containsCustomer(request.customer, carveOutCustomers)) {
            	rsAction = (super.getCarveOutImpactType() == CarveOutImpactType.EXCEPT_AGAINST) ? RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
            } else {
            	rsAction = (super.getCarveOutImpactType() == CarveOutImpactType.EXCEPT_AGAINST) ? RightStrandCarveOutAction.APPLY_RIGHT_STRAND : RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;
            }
        }
        
        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> carveOutImpact = new HashMap<>();
        carveOutImpact.put(relevantTerm, new HashMap<>());
        carveOutImpact.get(relevantTerm).put(relevantPeriod, rsAction);
        
        return new CarveOutResult(carveOutImpact, gapAction);
	}
	
	public Set<Customer> getCarveOutCustomers() {
		return carveOutCustomers;
	}

	public List<Long> getTerritoryIds() {
		return territoryIds;
	}

	public List<Long> getMediaIds() {
		return mediaIds;
	}

	public List<Long> getLanguageIds() {
		return languageIds;
	}

	



}
