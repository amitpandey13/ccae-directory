package com.pdgc.general.calculation.corporate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.FoxDistributionStrand;
import com.pdgc.general.structures.rightstrand.FoxRestrictionStrand;
import com.pdgc.general.structures.rightstrand.FoxSalesWindowStrand;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.FoxSalesWindowDistributionStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.StopWatch;

import lombok.Builder;

/**
 * For Fox, "nearest match" is implemented. Look at the request right strands
 * and match against the corp right strands first by media, then by territory,
 * then by language. If a match is found, throw out any strands with mtl above
 * that one in the hierarchy but include any mtl below that one in the
 * hierarchy.
 * 
 * @author atarshis
 *
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
public class FoxCorporateCalculator extends AbstractFoxDisneyCorporateCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoxCorporateCalculator.class);
    
    private RightTypeImpactMatrix rightTypeImpactMatrix;
    private NearestStrandsCalculator nearestStrandsCalculator;
    private boolean calculateSalesPlansAsDistrRights;
    private boolean filterOutPreWindowTerms;
    
    @Builder
    public FoxCorporateCalculator(
        RightTypeImpactMatrix rightTypeImpactMatrix,
        NearestStrandsCalculator nearestStrandsCalculator,
        boolean calculateSalesPlansAsCorpRights,
        boolean filterOutPreWindowTerms
    ) {
        this.rightTypeImpactMatrix = rightTypeImpactMatrix;
        this.nearestStrandsCalculator = nearestStrandsCalculator;
        this.calculateSalesPlansAsDistrRights = calculateSalesPlansAsCorpRights; 
        this.filterOutPreWindowTerms = filterOutPreWindowTerms;
    }
    
    @Override
    public boolean needsToGoThroughCalculator(RightStrand rightStrand) {
        return rightStrand instanceof CorporateRightStrand 
            || rightStrand instanceof FoxSalesWindowStrand
        ;
    }
    
    @Override
    public FoxAvailsCorporateCalculatorResult calculateForNonEmptyAvails(
        CorporateCalculatorParams params
    ) {
        PreProcessResults preProcessResult = preProcessStrands(params, true);
        
        //Analyze the distribution rights and tba restrictions
        SortedSet<Term> distributionTerms = new TreeSet<>();
        Set<TermPeriod> distributionlessWindows = new HashSet<>();
        boolean hasTBAStart = false;
        boolean hasTBAEnd = false;
        for (Entry<TermPeriod, Set<CorporateRightStrand>> termPeriodEntry : preProcessResult.sortedRelevantStrands.entrySet()) {
            boolean hasDistribution = false;
            for (CorporateRightStrand rightStrand : termPeriodEntry.getValue()) {
                if (rightStrand instanceof FoxDistributionStrand) {
                    distributionTerms.add(termPeriodEntry.getKey().getTerm());
                    hasDistribution = true;
                } else if (Long.compare(rightStrand.getRightType().getRightTypeId(), Constants.RIGHT_TYPE_ID_RESTRICTION_TBA_START) == 0) {
                    hasTBAStart = true;
                } else if (Long.compare(rightStrand.getRightType().getRightTypeId(), Constants.RIGHT_TYPE_ID_RESTRICTION_TBA_END) == 0) {
                    hasTBAEnd = true;
                }
            }
            
            if (!hasDistribution) {
                distributionlessWindows.add(termPeriodEntry.getKey());
            }
        }
        
        Map<RightType, Map<TermPeriod, AvailsCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            TermPeriodAnalysisResults analysisResult = calculateAvailabilities(calcRequest, preProcessResult);
            boolean needsDistributions = needsDistributionRights(calcRequest.requestedRightType);
            
            for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : analysisResult.availabilityMap.entrySet()) {
                TermPeriod termPeriod = termPeriodEntry.getKey();
                AvailabilityResult availabilityResult = termPeriodEntry.getValue();
                
                //add the correct detail (pre-/post-/no-distribution window details to all windows missing distribution rights)
                if (needsDistributions && distributionlessWindows.contains(termPeriod)) {
                    Set<Object> availabilityDetails = new HashSet<>(termPeriodEntry.getValue().resultDetails);
                    if (distributionTerms.isEmpty()) {
                        availabilityDetails.add(FoxWindowReasonDetail.NO_VALID_DISTRIBUTION);
                    } else if (termPeriod.getStartDate().isAfter(distributionTerms.last().getEndDate())) {
                        availabilityDetails.add(FoxWindowReasonDetail.POST_DISTRIBUTION);
                    } else {
                        availabilityDetails.add(FoxWindowReasonDetail.PRE_DISTRIBUTION);
                    }
                    
                    availabilityResult = new AvailabilityResult(termPeriodEntry.getValue().availability, availabilityDetails);
                }
                
                Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>(analysisResult.rightStrandImpactMap.get(termPeriod));
                
                //Add the ignored corp strands to the rightStrandImpact maps
                for (Entry<CorporateRightStrand, Set<FoxRecordReasonDetail>> rsEntry : preProcessResult.sortedIgnoredStrands.get(termPeriod).entrySet()) {
                    rightStrandImpacts.put(rsEntry.getKey(), new AvailabilityResult(Availability.UNSET, rsEntry.getValue()));
                }
                
                resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                    .put(termPeriodEntry.getKey(), new AvailsCorpResult(availabilityResult, rightStrandImpacts));
            }
        }
        
        return new FoxAvailsCorporateCalculatorResult(
            resultMap,
            preProcessResult.infoStrandDetails,
            distributionTerms,
            hasTBAStart,
            hasTBAEnd
        );
    }
    
    @Override
    public FoxConflictCorporateCalculatorResult calculateForNonEmptyConflictCheck(
        CorporateCalculatorParams params
    ) {
        PreProcessResults preProcessResults = preProcessStrands(params, false);
        
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            TermPeriodAnalysisResults analysisResult = calculateAvailabilities(calcRequest, preProcessResults);
            
            for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : analysisResult.availabilityMap.entrySet()) {
                resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                    .put(termPeriodEntry.getKey(), new ConflictCorpResult(
                        termPeriodEntry.getValue(),
                        preProcessResults.sortedRelevantStrands.get(termPeriodEntry.getKey())
                    ));
            }
        }
        
        return new FoxConflictCorporateCalculatorResult(
            resultMap
        );
    }

    /**
     * Runs all the sorting/grouping needed for the right strands before entering
     * the term-period analysis
     * @param params
     * @param isAvailsContext - true for avails, false for conflict-check
     */
    private PreProcessResults preProcessStrands(
        CorporateCalculatorParams params,
        boolean isAvailsContext
    ) {
        PreProcessResults output = new PreProcessResults();
        
        separateCuttingAndNonCuttingStrands(
            params.requestedPMTL,
            params.rightStrands,
            output
        );
        
        StopWatch timer = new StopWatch();
        timer.start();
        
        Map<TermPeriod, Set<CorporateRightStrand>> sortedStrands;
        Term requestedTerm = null;
        TimePeriod requestedTimePeriod = TimePeriod.EMPTY_WEEK;
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            requestedTerm = Term.getUnion(requestedTerm, calcRequest.requestedTerm);
            requestedTimePeriod = TimePeriod.unionPeriods(requestedTimePeriod, calcRequest.requestedTimePeriod);
        }
        
        if (isAvailsContext) {
            //Avails needs a full analysis of all terms, not just the request in order to find the 
            //earliest/latest distribution, so the sorting had to happen across epoch - perpetuity
            //we do still want to inject the requested term's date cuts to the final answer, tho
            sortedStrands = sortRightStrands(
                output.cuttingStrandDetails.keySet(),
                new Term(Constants.EPOCH, Constants.PERPETUITY),
                TimePeriod.FULL_WEEK
            );
            
            injectDateCut(sortedStrands, requestedTerm.getStartDate(), true);
            injectDateCut(sortedStrands, requestedTerm.getEndDate(), false);
            injectTimePeriod(sortedStrands, requestedTimePeriod);
        } else {
            sortedStrands = sortRightStrands(
                output.cuttingStrandDetails.keySet(),
                requestedTerm,
                requestedTimePeriod
            );
        }
        
        if (filterOutPreWindowTerms) {
            getEarliestWindowDate(
                params.rightStrands,
                output
            );
            
            injectDateCut(
                sortedStrands,
                output.earliestWindowDate, 
                true
            );
        } else {
            output.earliestWindowDate = LocalDate.MIN; //just set it instead of risking null pointers or something
        }
        
        Map<TermPeriod, Set<CorporateRightStrand>> sortedRelevantStrands = new HashMap<>();
        Map<TermPeriod, Map<CorporateRightStrand, Set<FoxRecordReasonDetail>>> sortedIgnoredStrands = new HashMap<>();
        
        for (Entry<TermPeriod, Set<CorporateRightStrand>> termPeriodEntry : sortedStrands.entrySet()) {
            Set<CorporateRightStrand> relevantStrands = new HashSet<>();
            Map<CorporateRightStrand, Set<FoxRecordReasonDetail>> ignoredStrands = new HashMap<>();
            separateRelevantAndNonRelevantCorpStrandsInTermPeriod(
                termPeriodEntry.getValue(),
                relevantStrands,
                ignoredStrands
            );
            
            sortedRelevantStrands.put(termPeriodEntry.getKey(), relevantStrands);
            sortedIgnoredStrands.put(termPeriodEntry.getKey(), ignoredStrands);
        }
        
        output.sortedRelevantStrands = sortedRelevantStrands;
        output.sortedIgnoredStrands = sortedIgnoredStrands;
        
        timer.split();
        LOGGER.debug("Sort strands F {} ms", timer.getSplitTime());
        
        return output;
    }
    
    private TermPeriodAnalysisResults calculateAvailabilities(
        CorporateCalculationRequest calcRequest,
        PreProcessResults preProcessResults
    ) {
        TermPeriodAnalysisResults result = new TermPeriodAnalysisResults();
        result.availabilityMap = new HashMap<>();
        result.rightStrandImpactMap = new HashMap<>();
        
        boolean needsDistribution = needsDistributionRights(calcRequest.requestedRightType);
        
        TermPeriod requestedTermPeriod = new TermPeriod(calcRequest.requestedTerm, calcRequest.requestedTimePeriod);
        for (Entry<TermPeriod, Set<CorporateRightStrand>> termPeriodEntry : preProcessResults.sortedRelevantStrands.entrySet()) {
            TermPeriod termPeriod = termPeriodEntry.getKey();
            if (!TermPeriod.hasIntersection(termPeriod, requestedTermPeriod)) {
                continue;
            }
            
            Availability windowAvailability = getDefaultAvailability(calcRequest.requestedRightType);
            Set<FoxWindowReasonDetail> windowDetails = new HashSet<>();
            Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>();
            
            //if the term is before the earliest sales window, add it to the windowDetails
            //don't rely on the right strands to do it, because there may not be any distribution rights
            boolean windowFiltered = false;
            if (needsDistribution && filterOutPreWindowTerms && termPeriod.getTerm().getStartDate().isBefore(preProcessResults.earliestWindowDate)) {
                windowFiltered = true;
                windowDetails.add(convertRecordDetailToWindowDetail(preProcessResults.windowDateType));
            }
            
            for (CorporateRightStrand rightStrand : termPeriodEntry.getValue()) {
                Availability strandAvailability = rightTypeImpactMatrix.getAvailabilityImpact(calcRequest.requestedRightType, rightStrand.getRightType());
                Set<FoxRecordReasonDetail> strandResultDetails = new HashSet<>(preProcessResults.cuttingStrandDetails.get(rightStrand));
                
                if (needsDistribution) {
                    //Reverse any impacts back to UNSET if the term is before the earliest sales window
                    if (windowFiltered) {
                        strandAvailability = Availability.UNSET;
                        strandResultDetails.add(preProcessResults.windowDateType);
                    } else {
                        if (rightStrand instanceof FoxDistributionStrand && ((FoxDistributionStrand)rightStrand).isPreliminary()) {
                            strandResultDetails.add(FoxRecordReasonDetail.PRELIMINARY_DISTRIBUTION);
                        } else if (rightStrand instanceof FoxRestrictionStrand && strandAvailability == Availability.NO) {
                            strandResultDetails.add(FoxRecordReasonDetail.RESTRICTS_AVAILABILITY);
                        }
                    }
                } else {
                    if (rightStrand instanceof FoxRestrictionStrand) {
                        if (strandAvailability == Availability.NO) {
                            strandResultDetails.add(FoxRecordReasonDetail.RESTRICTS_AVAILABILITY);
                        } else {
                            // Guarantees that the restriction does not get displayed as an impacting Restriction Code. 
                            // Marked as No Impact if the requested right is an Exclusivity and the restriction does not impact availability
                            strandResultDetails.add(FoxRecordReasonDetail.NO_IMPACT_RESTRICTION);
                        }
                    }
                }
                
                AvailabilityResult strandAvailabilityResult = new AvailabilityResult(
                    strandAvailability,
                    strandResultDetails
                );
                
                windowAvailability = AvailabilityHelper.combineAvailability(windowAvailability, strandAvailabilityResult.availability);
                for (FoxRecordReasonDetail recordDetail : strandResultDetails) {
                    FoxWindowReasonDetail windowDetail = convertRecordDetailToWindowDetail(recordDetail);
                    if (windowDetail != null) {
                        windowDetails.add(windowDetail);
                    }
                }
                
                rightStrandImpacts.put(rightStrand, strandAvailabilityResult);
            }
            
            result.availabilityMap.put(termPeriod, new AvailabilityResult(windowAvailability, windowDetails));
            result.rightStrandImpactMap.put(termPeriod, rightStrandImpacts);
        }
        
        return result;
    }
    
    private void separateCuttingAndNonCuttingStrands(
        PMTL requestedPMTL, 
        Collection<RightStrand> rightStrands,
        //Outputs
        PreProcessResults output
    ) {
        StopWatch timer = new StopWatch();
        timer.start();
        
        Set<CorporateRightStrand> corpStrands = new HashSet<>();
        for (RightStrand rs : rightStrands) {
            if (rs instanceof FoxSalesWindowDistributionStrand && calculateSalesPlansAsDistrRights) {
                corpStrands.add((CorporateRightStrand)rs);
            } else if (rs instanceof CorporateRightStrand) {
                corpStrands.add((CorporateRightStrand)rs);
            }
        }
        Set<CorporateRightStrand> nearestStrands = nearestStrandsCalculator.findNearestStrands(requestedPMTL, corpStrands);

        timer.split();
        LOGGER.debug("Find nearest Corp E {} ms corpStrands size {} nearestCorpStrands size {}", timer.getSplitTime(), corpStrands.size(), nearestStrands.size());
        
        Map<CorporateRightStrand, Set<FoxRecordReasonDetail>> cuttingStrandDetails = new HashMap<>();
        Map<RightStrand, Set<FoxRecordReasonDetail>> infoStrandDetails = new HashMap<>();
        for (RightStrand rs : rightStrands) {
            Set<FoxRecordReasonDetail> strandDetails = new HashSet<>();
            
            if (corpStrands.contains(rs)) {
                if (nearestStrands.contains(rs)) {
                    cuttingStrandDetails.put((CorporateRightStrand)rs, strandDetails);             
                } else {
                    strandDetails.add(FoxRecordReasonDetail.NOT_NEAREST);
                    infoStrandDetails.put(rs, strandDetails);
                }
            } else {
                infoStrandDetails.put(rs, strandDetails);
            }
        }
        
        output.cuttingStrandDetails = cuttingStrandDetails;
        output.infoStrandDetails = infoStrandDetails;
    }
    
    /**
     * Finds the date of the earliest current sales window start
     * @param rightStrands,
     * @param output - the output object to get populated
     * @return
     */
    private void getEarliestWindowDate(
        Collection<RightStrand> rightStrands,
        PreProcessResults output
    ) {
        Set<FoxSalesWindowStrand> currentWindows = Sets.newHashSet();
        if (filterOutPreWindowTerms) {
            //Find all the sales window strands and map them to their terms
            for (RightStrand rs : rightStrands) {
                if (rs instanceof FoxSalesWindowStrand) {
                    FoxSalesWindowStrand castedStrand = (FoxSalesWindowStrand)rs;
                    if (Objects.equals(castedStrand.getLifecycle().getId(), Constants.SALES_PLAN_CURRENT_LIFECYCLE_ID)) {
                        currentWindows.add(castedStrand);
                    }
                }
            }
        }
        
        LocalDate earliestWindowDate = LocalDate.MIN;
        FoxRecordReasonDetail windowDateType = null;
        if (filterOutPreWindowTerms && !currentWindows.isEmpty()) {
            LocalDate earliestWindowStartDate = LocalDate.MAX;
            
            for (FoxSalesWindowStrand rs: currentWindows) {
                earliestWindowStartDate = DateTimeUtil.getMinDate(
                    rs.getSalesWindowProduct().getStartDate(), 
                    earliestWindowStartDate
                );
            }
            
            earliestWindowDate = earliestWindowStartDate;
            windowDateType = FoxRecordReasonDetail.PRIOR_TO_SALES_WINDOW;
        }
        
        output.earliestWindowDate = earliestWindowDate;
        output.windowDateType = windowDateType;
    }
    
    /**
     * Modifies the sortedStrands terms so that they are cut by the cuttingDate
     * @param sortedStrands
     * @param cuttingDate
     * @param isStart - true if the cuttingDate is intended to be a start date, false if it was an end date
     */
    private void injectDateCut(
        Map<TermPeriod, Set<CorporateRightStrand>> sortedStrands,
        LocalDate cuttingDate,
        boolean isStart
    ) {
        SortedSet<Term> terms = new TreeSet<>();
        for (TermPeriod tp : sortedStrands.keySet()) {
            terms.add(tp.getTerm());
        }
        
        if ((isStart && cuttingDate.equals(LocalDate.MIN)) 
                || (!isStart && cuttingDate.equals(LocalDate.MAX))) {
            return;
        }
        
        LocalDate revisedStartDate = isStart ? cuttingDate : cuttingDate.plusDays(1);
        for (Term term : terms) {
            //Because the terms are sorted, the first term that passes this check is the one that must be cut
            if (term.getStartDate().equals(revisedStartDate)) {
                break;
            } else if (!term.getEndDate().isBefore(revisedStartDate)) {
                Term newTerm1 = new Term(term.getStartDate(), revisedStartDate.minusDays(1));
                Term newTerm2 = new Term(revisedStartDate, term.getEndDate());
                
                Collection<TermPeriod> relevantTermPeriods = CollectionsUtil.where(sortedStrands.keySet(), tp -> tp.getTerm().equals(term));
                for (TermPeriod tp : relevantTermPeriods) {
                    Set<CorporateRightStrand> strandsInTermPeriod = sortedStrands.get(tp);
                    sortedStrands.remove(tp);
                    sortedStrands.put(new TermPeriod(newTerm1, tp.getTimePeriod()), strandsInTermPeriod);
                    sortedStrands.put(new TermPeriod(newTerm2, tp.getTimePeriod()), strandsInTermPeriod);
                }
                break; //only one term ever has to get cut
            }
        }
    }
    
    /**
     * Modifies the sortedStrands timePeriods so that they are cut by the cuttingPeriod
     * @param sortedStrands
     * @param cuttingPeriod
     */
    private void injectTimePeriod(
        Map<TermPeriod, Set<CorporateRightStrand>> sortedStrands,
        TimePeriod cuttingPeriod
    ) {
        if (cuttingPeriod.equals(TimePeriod.FULL_WEEK)) {
            return; //No work needed, since a full week will never cut an equal/smaller period
        }
        
        Set<TermPeriod> termPeriods = new HashSet<>(sortedStrands.keySet());
        for (TermPeriod tp : termPeriods) {
            TimePeriod intersection = TimePeriod.intersectPeriods(tp.getTimePeriod(), cuttingPeriod);
            if (intersection.equals(tp.getTimePeriod()) || intersection.isEmpty()) {
                continue; //The cutting period was equal/larger than the original or didn't even intersect, so no cuts
            }
            
            TimePeriod leftover = TimePeriod.subtractPeriods(tp.getTimePeriod(), intersection);
            Set<CorporateRightStrand> strandsInTermPeriod = sortedStrands.get(tp);
            sortedStrands.remove(tp);
            sortedStrands.put(new TermPeriod(tp.getTerm(), intersection), strandsInTermPeriod);
            sortedStrands.put(new TermPeriod(tp.getTerm(), leftover), strandsInTermPeriod);
        }
    }
    
    /**
     * Separates out relevant and non-relevant right strands for the term period.
     * Irrelevant strands are those that are not of the max calculation order or are ignored restrictions
     * @param corpStrands
     * @param relevantStrands
     * @param ignoredStrands
     */
    private void separateRelevantAndNonRelevantCorpStrandsInTermPeriod(
        Collection<CorporateRightStrand> corpStrands,
        //Outputs
        Collection<CorporateRightStrand> relevantStrands,
        Map<CorporateRightStrand, Set<FoxRecordReasonDetail>> ignoredStrands
    ) {
        if (corpStrands.isEmpty()) {
            return;
        }
        
        Map<Integer, Collection<CorporateRightStrand>> rightStrandOrderMap = sortRightStrandsByCalculationOrder(corpStrands);
        int maxCalcOrder = rightStrandOrderMap.keySet().stream().max(Integer::compareTo).get();
        for (Entry<Integer, Collection<CorporateRightStrand>> calcOrderEntry : rightStrandOrderMap.entrySet()) {
            if (calcOrderEntry.getKey().equals(maxCalcOrder)) {
                relevantStrands.addAll(calcOrderEntry.getValue());
            } else {
                for (CorporateRightStrand rs : calcOrderEntry.getValue()) {
                    ignoredStrands.put(rs, Sets.newHashSet(FoxRecordReasonDetail.NOT_MAX_CALC_ORDER));
                }
            }
        }
        
        Map<FoxRestrictionStrand, Set<FoxRecordReasonDetail>> ignoredRestrictions = findIgnoredRestrictions(rightStrandOrderMap.get(maxCalcOrder));
        for (Entry<FoxRestrictionStrand, Set<FoxRecordReasonDetail>> restrictionEntry : ignoredRestrictions.entrySet()) {
            ignoredStrands.put(restrictionEntry.getKey(), restrictionEntry.getValue());
            relevantStrands.remove(restrictionEntry.getKey());
        }
    }
    
    /**
     * Ignore certain restrictions based on client requirements.
     * @param corpStrands 
     * @return the list of restriction strands that should be ignored.
     */
    private Map<FoxRestrictionStrand, Set<FoxRecordReasonDetail>> findIgnoredRestrictions(
        Collection<CorporateRightStrand> corpStrands
    ) {
        List<FoxDistributionStrand> distributionStrands = corpStrands.stream()
            .filter(rs -> rs instanceof FoxDistributionStrand)
            .map(rs -> (FoxDistributionStrand)rs)
            .collect(Collectors.toCollection(ArrayList::new));
        List<FoxRestrictionStrand> restrictionStrands = corpStrands.stream()
            .filter(rs -> rs instanceof FoxRestrictionStrand)
            .map(rs -> (FoxRestrictionStrand)rs)
            .collect(Collectors.toCollection(ArrayList::new));
        
        Map<FoxRestrictionStrand, Set<FoxRecordReasonDetail>> ignoredRestrictions = new HashMap<>();
        
        for (FoxRestrictionStrand restriction : restrictionStrands) {
            /*
             * If the restriction is tied to a specific distribution right, and that distribution right does not exist, then remove the restriction
             */
            if (restriction.getParentRightSourceId() != null) {
                boolean foundParent = CollectionsUtil.any(
                    distributionStrands, 
                    drs -> drs.getRightSource().getSourceId().equals(restriction.getParentRightSourceId())
                );
                
                if (!foundParent) {
                    Set<FoxRecordReasonDetail> recordDetails = ignoredRestrictions.get(restriction);
                    if (recordDetails == null) {
                        recordDetails = new HashSet<>();
                        ignoredRestrictions.put(restriction, recordDetails);
                    }
                    recordDetails.add(FoxRecordReasonDetail.ORPHAN_RESTRICTION);
                    continue;
                }
            }
        }
        
        return ignoredRestrictions;
    }

    /**
     * Output structure for updating the various variables being tracked before entering 
     * analysis of the individual term-periods of the sorted strands
     * @author Linda Xu
     */
    private class PreProcessResults {
        Map<CorporateRightStrand, Set<FoxRecordReasonDetail>> cuttingStrandDetails;
        Map<RightStrand, Set<FoxRecordReasonDetail>> infoStrandDetails;
        Map<TermPeriod, Set<CorporateRightStrand>> sortedRelevantStrands;
        Map<TermPeriod, Map<CorporateRightStrand, Set<FoxRecordReasonDetail>>> sortedIgnoredStrands;
        LocalDate earliestWindowDate;
        FoxRecordReasonDetail windowDateType;
    }
}
