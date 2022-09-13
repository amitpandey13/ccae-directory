package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CustomerCountLicense;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class CustomerCountCarveOutHelper {

	public static Map<Term, Map<TimePeriod, Collection<CustomerCountLicense>>> groupCustomerLicenses(
		Collection<CustomerCountLicense> existingLicenses,
		Term relevantTerm,
		TimePeriod relevantPeriod
	) {
		Map<Term, Map<TimePeriod, Collection<CustomerCountLicense>>> termMap = new HashMap<>();
		
		//Initialize the termMap to make sure there is an answer for the entire term/period
		termMap.put(relevantTerm, new HashMap<>());
		termMap.get(relevantTerm).put(relevantPeriod, new HashSet<>());
		
		Supplier<Collection<CustomerCountLicense>> defaultValueProducer = new Supplier<Collection<CustomerCountLicense>>() {
			@Override
			public Collection<CustomerCountLicense> get() {
				return new HashSet<>();
			}
		};
		
		Function<Collection<CustomerCountLicense>, Collection<CustomerCountLicense>> valueDeepCopy = new Function<Collection<CustomerCountLicense>, Collection<CustomerCountLicense>>() {
			@Override
			public Collection<CustomerCountLicense> apply(Collection<CustomerCountLicense> arg0) {
				return new HashSet<>(arg0);
			}
		};
		
		for (CustomerCountLicense existingLicense : existingLicenses) {
			Function<Collection<CustomerCountLicense>, Collection<CustomerCountLicense>> valueUpdater = new Function<Collection<CustomerCountLicense>, Collection<CustomerCountLicense>>() {
				@Override
				public Collection<CustomerCountLicense> apply(Collection<CustomerCountLicense> arg0) {
					arg0.add(existingLicense);
					return arg0;
				}
			};
			
			DateTimeUtil.updateTermPeriodValueMap(
				termMap, 
				existingLicense.getTerm(), 
				existingLicense.getTimePeriod(), 
				defaultValueProducer, 
				valueDeepCopy, 
				valueUpdater
			);
		}
		
		return termMap;
	}

	public static Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> getImpactMap(
		CarveOutImpactRequest request,
		Map<Term, Map<TimePeriod, Collection<CustomerCountLicense>>> licenseTermMap,
		Function<CarveOutActionSupplierParams, RightStrandCarveOutAction> carveOutActionSupplier
	) {
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> impactMap = new HashMap<>();
        for (Entry<Term, Map<TimePeriod, Collection<CustomerCountLicense>>> termEntry : licenseTermMap.entrySet()) {
        	Map<TimePeriod, RightStrandCarveOutAction> periodMap = new HashMap<>();
            for(Entry<TimePeriod, Collection<CustomerCountLicense>> periodEntry : termEntry.getValue().entrySet()) {
                RightStrandCarveOutAction rsAction = carveOutActionSupplier.apply(new CarveOutActionSupplierParams(
                	request, 
                	periodEntry.getValue()
                ));
                periodMap.put(periodEntry.getKey(), rsAction);
            }
            impactMap.put(termEntry.getKey(), periodMap);
        }
        
        return impactMap;
	}
	
	public static class CarveOutActionSupplierParams {
		CarveOutImpactRequest request;
		Collection<CustomerCountLicense> existingTermPeriodLicenses;
		
		public CarveOutActionSupplierParams(
			CarveOutImpactRequest request,
			Collection<CustomerCountLicense> existingTermPeriodLicenses
		) {
			this.request = request;
			this.existingTermPeriodLicenses = existingTermPeriodLicenses;
		}
	}
}
