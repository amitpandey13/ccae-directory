package com.pdgc.avails.structures.rollup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.avails.service.AvailsRollup;
import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.criteria.SecondaryRightRequest;
import com.pdgc.avails.structures.criteria.TimeSpanHelper;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityResultStruct;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityWindow;
import com.pdgc.avails.structures.rollup.intermediate.SecondaryAvailabilityResult;
import com.pdgc.avails.structures.rollup.intermediate.SortedCriteriaRequests;
import com.pdgc.avails.structures.rollup.tab.result.LeafSource;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

public class AvailsResultHelper {

    /**
     * Builds an AvailsResult with the primary rights and additional rights calculated. 
     * Secondary rights and other filters are not applied
     * @param runParams
     * @param leafSources
     * @param criteriaRequests
     * @param relevantPeriod
     * @param usePartialAvailability
     * @return
     */
    public static BaseAvailsResult buildBaseAvailsResult(
        AvailsRunParams runParams,
        Collection<LeafSource> leafSources,
        Map<Set<LeafPMTLIdSet>, SortedCriteriaRequests> criteriaRequests,
        TimePeriod relevantPeriod,
        boolean usePartialAvailability
    ) {
        BaseAvailsResult availsResult = new BaseAvailsResult();
        
        //Sort the leaf sources first
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Collection<LeafSource>>> leafSourcePMTLRequestMap = new HashMap<>();
        Map<RightRequest, Collection<LeafSource>> leafSourceRequestMap = new HashMap<>();
        for (LeafSource leafSource : leafSources) {
            leafSourcePMTLRequestMap.computeIfAbsent(leafSource.pmtl, k -> new HashMap<>())
                .computeIfAbsent(leafSource.request, k -> new ArrayList<>())
                .add(leafSource);
            
            leafSourceRequestMap.computeIfAbsent(leafSource.request, k -> new ArrayList<>())
                .add(leafSource);
        }
        
        //Evaluate the criteria availabilities
        {
            availsResult.allPrimaryOptional = true;
            for (Entry<Set<LeafPMTLIdSet>, SortedCriteriaRequests> pmtlEntry : criteriaRequests.entrySet()) {
                Map<RightRequest, Collection<LeafSource>> leafSourceRequestMapForPMTL = leafSourcePMTLRequestMap.get(pmtlEntry.getKey());
                
                for (OptionalWrapper<RightRequest> request : pmtlEntry.getValue().getPrimaryRights()) {
                    if (!TimePeriod.hasIntersection(relevantPeriod, request.getElement().getTimePeriod())) {
                        continue;
                    }
                    
                    availsResult.allPrimaryOptional &= request.isOptional();
                    
                    for (LeafSource leafSource : leafSourceRequestMapForPMTL.get(request.getElement())) {
                        availsResult.primaryRights.computeIfAbsent(pmtlEntry.getKey(), k -> new HashMap<>())
                            .merge(request, leafSource.leafResultMeta.availabilities, AvailabilityResultStruct::combine);
                    }
                }
            }
            
            if (runParams.getAvailsCriteria().allowPartialAvailability()) { 
                //traverse all the stored right impacts and see if any one product is available for *all* criteria
                
                //Sort the availability impacts to their appropriate leaf products
                Map<Integer, Collection<AvailabilityResultStruct>> productAvailabilityMap = new HashMap<>();
                for (Entry<Set<LeafPMTLIdSet>, Map<OptionalWrapper<RightRequest>, AvailabilityResultStruct>> pmtlEntry : availsResult.primaryRights.entrySet()) {
                    Set<Integer> products = new HashSet<>();
                    for (LeafPMTLIdSet pmtl : pmtlEntry.getKey()) {
                        products.addAll(pmtl.getProductIds());
                    }
                    
                    for (Integer product : products) {
                        Collection<AvailabilityResultStruct> productAvailabilities = productAvailabilityMap.get(product);
                        if (productAvailabilities == null) {
                            productAvailabilities = new HashSet<>();
                        }
                        
                        for (Entry<OptionalWrapper<RightRequest>, AvailabilityResultStruct> requestEntry : pmtlEntry.getValue().entrySet()) {
                            if (availsResult.allPrimaryOptional || !requestEntry.getKey().isOptional()) {
                                productAvailabilities.add(requestEntry.getValue());
                            }
                        }
                        
                        productAvailabilityMap.put(product, productAvailabilities);
                    }
                }
                
                //Calculate the net availability for each leaf product
                Map<Integer, AvailabilityResultStruct> productNetAvailabilityMap = new HashMap<>();
                for (Entry<Integer, Collection<AvailabilityResultStruct>> productEntry : productAvailabilityMap.entrySet()) {
                    productNetAvailabilityMap.put(
                        productEntry.getKey(), 
                        calculateNetAvailability(productEntry.getValue(), availsResult.allPrimaryOptional)
                    );
                }
                
                availsResult.primaryNetResult = calculateNetAvailability(
                    productNetAvailabilityMap.values(),
                    true
                );
            } else {
                Collection<AvailabilityResultStruct> availabilities = new ArrayList<>();
                for (Entry<Set<LeafPMTLIdSet>, Map<OptionalWrapper<RightRequest>, AvailabilityResultStruct>> pmtlEntry : availsResult.primaryRights.entrySet()) {
                    for (Entry<OptionalWrapper<RightRequest>, AvailabilityResultStruct> requestEntry : pmtlEntry.getValue().entrySet()) {
                        if (availsResult.allPrimaryOptional || !requestEntry.getKey().isOptional()) {
                            availabilities.add(requestEntry.getValue());
                        }
                    }
                }
                
                availsResult.primaryNetResult = calculateNetAvailability(
                    availabilities, 
                    availsResult.allPrimaryOptional
                );
            }
        }
    
        //Evaluate the additional request availabilities
        for (RightRequest request : runParams.getAdditionalRequests()) {
            AvailabilityResultStruct mergedAvailability = new AvailabilityResultStruct();
            for (LeafSource leafSource : leafSourceRequestMap.get(request)) {
                mergedAvailability = AvailabilityResultStruct.combine(
                    mergedAvailability, 
                    leafSource.leafResultMeta.availabilities
                );
            }
            availsResult.additionalRights.put(request, AvailsRollup.reviseForNetCalc(mergedAvailability));
        }
        
        availsResult.cleanupAvailsResult();

        return availsResult;
    }
    
    public static void addSecondaryRights(
        FullAvailsResult fullResult,
        Map<Set<LeafPMTLIdSet>, SortedCriteriaRequests> criteriaRequests,
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Set<AvailabilityWindow>>> availabilityWindows,
        Term primaryTerm,
        TimePeriod primaryPeriod
    ) {
        for (Entry<Set<LeafPMTLIdSet>, SortedCriteriaRequests> pmtlEntry : criteriaRequests.entrySet()) {
            for (OptionalWrapper<SecondaryRightRequest> request : pmtlEntry.getValue().getSecondaryPreRights()) {
                if (!TimePeriod.hasIntersection(primaryPeriod, request.getElement().getRightRequest().getTimePeriod()) ) {
                    continue;
                }
                
                Set<AvailabilityWindow> windowsForRequest = availabilityWindows
                    .get(pmtlEntry.getKey())
                    .get(request.getElement().getRightRequest());
                
                SecondaryAvailabilityResult requestResult = createSecondaryAvailabilityResult(
                    request.getElement(),
                    true,
                    primaryTerm,
                    windowsForRequest
                );
                fullResult.passesSecondaryRights &= !request.isOptional() && requestResult.isPassing();
                
                fullResult.secondaryPreRights.computeIfAbsent(pmtlEntry.getKey(), k -> new HashMap<>())
                    .put(request, requestResult);
            }
            
            for (OptionalWrapper<SecondaryRightRequest> request : pmtlEntry.getValue().getSecondaryPostRights()) {
                if (!TimePeriod.hasIntersection(primaryPeriod, request.getElement().getRightRequest().getTimePeriod()) ) {
                    continue;
                }
                
                Set<AvailabilityWindow> windowsForRequest = availabilityWindows
                        .get(pmtlEntry.getKey())
                        .get(request.getElement().getRightRequest());
                
                SecondaryAvailabilityResult requestResult = createSecondaryAvailabilityResult(
                    request.getElement(),
                    false,
                    primaryTerm,
                    windowsForRequest
                );
                fullResult.passesSecondaryRights &= !request.isOptional() && requestResult.isPassing();
                
                fullResult.secondaryPostRights.computeIfAbsent(pmtlEntry.getKey(), k -> new HashMap<>())
                    .put(request, requestResult);
            }
        }
    }

    public static SecondaryAvailabilityResult createSecondaryAvailabilityResult(
        SecondaryRightRequest secondaryRequest,
        boolean isPreOrPost, //true for pre, false for post
        Term primaryTerm,
        Set<AvailabilityWindow> windowsForRequest
    ) {
        Availability netAvailability;
        Term relevantTerm;
        
        //We can't actually calculate anything before epoch or after perpetuity so leave it undefined
        if ((isPreOrPost && primaryTerm.getStartDate().equals(Constants.EPOCH))
            || (!isPreOrPost && primaryTerm.getEndDate().equals(Constants.PERPETUITY))) {
            netAvailability = Availability.UNSET;
            relevantTerm = null;
        }
        else {
            //Window length adjustment around EPOCH and PERPETUITY is automatically handled
            //by the fact that relevantTerm (ie. the secondary window) dates get adjusted 
            //during the date calculation
            if (isPreOrPost) {
                relevantTerm = getRelevantTermForPreRequest(secondaryRequest, primaryTerm.getStartDate());
            } else {
                relevantTerm = getRelevantTermForPostRequest(secondaryRequest, primaryTerm.getEndDate());
            }
            
            Collection<AvailabilityWindow> relevantWindows = CollectionsUtil.where(
                windowsForRequest, 
                w -> Term.hasIntersection(relevantTerm, w.windowTerm)
            );
            
            Set<Term> relevantWindowTerms = CollectionsUtil.select(
                relevantWindows, 
                w -> w.windowTerm,
                Collectors.toSet()
            );
            
            boolean passesRequest = DateTimeUtil.findGapTerms(
                relevantWindowTerms, 
                relevantTerm
            ).isEmpty();
            
            if (passesRequest) {
                netAvailability = Availability.UNSET;
                if (relevantWindows.size() == 1) {
                    netAvailability = relevantWindows.iterator().next().availability;
                } else {
                    for (AvailabilityWindow window : relevantWindows) {
                        netAvailability = AvailabilityHelper.combineAvailability(netAvailability, window.availability);
                    }
                }
            } else {
                netAvailability = Availability.NO;
            }
        }
        
        return new SecondaryAvailabilityResult(
            netAvailability, 
            relevantTerm
        );
    }
    
    /**
     * consolidates the collection of availabilities into a single AvaialbilityResultStruct object
     * When getMostAvailable is false, then all availabilities are combined together
     * Else, only non-unavailable availabilities are considered, unless there are no available inputs,
     *  in which case all inputs are combined together
     * 
     * @param availabilities
     * @param getMostAvailable
     * @return
     */
    private static AvailabilityResultStruct calculateNetAvailability(
        Collection<AvailabilityResultStruct> availabilities,
        boolean getMostAvailable
    ) {
        AvailabilityResultStruct netAvailability = new AvailabilityResultStruct();
        
        if (getMostAvailable) {
            boolean hasAvailableCriteria = false;
            for (AvailabilityResultStruct availability : availabilities) {
                if (availability.getNetAvailability() != Availability.NO) {
                    hasAvailableCriteria = true;
                    netAvailability = AvailabilityResultStruct.combine(
                        netAvailability, 
                        availability
                    );
                }
            }
            
            if (!hasAvailableCriteria) { //Use the combine rather than a default so that the reasonDetails cascade through to the final result
                for (AvailabilityResultStruct availability : availabilities) {
                    netAvailability = AvailabilityResultStruct.combine(
                        netAvailability, 
                        availability
                    );
                }
            }
        } else {
            for (AvailabilityResultStruct availability : availabilities) {
                netAvailability = AvailabilityResultStruct.combine(
                    netAvailability, 
                    availability
                );
            }
        }
    
        return netAvailability;
    }
    
    public static Term getRelevantTermForPreRequest(
        SecondaryRightRequest request, 
        LocalDate primaryStart
    ) {
        LocalDate anchorDate = TimeSpanHelper.subtractTime(
            primaryStart, 
            request.getGapLength()
        );
        LocalDate startDate = TimeSpanHelper.subtractTime(
            anchorDate, 
            request.getWindowLength()
        );
        LocalDate endDate = anchorDate.plusDays(-1);
        return new Term(startDate, endDate);
    }
    
    public static Term getRelevantTermForPostRequest(
        SecondaryRightRequest request, 
        LocalDate primaryEnd
    ) {
        LocalDate anchorDate = TimeSpanHelper.addTime(
            primaryEnd, 
            request.getGapLength()
        );
        LocalDate startDate = anchorDate.plusDays(1);
        LocalDate endDate = TimeSpanHelper.addTime(
            anchorDate, 
            request.getWindowLength()
        );
        return new Term(startDate, endDate);
    }
}
