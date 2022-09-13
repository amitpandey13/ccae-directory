package com.pdgc.avails.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Equivalence;
import com.pdgc.avails.structures.calculation.AvailabilityMetaData;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.rollup.AvailsRollupResult;
import com.pdgc.avails.structures.rollup.AvailsRollupResultProcessor;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityResultStruct;
import com.pdgc.avails.structures.rollup.intermediate.LeafAvailsResultMeta;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

public class AvailsRollup {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailsRollup.class);

    private Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>> criteriaRequestMap;
    private AvailsQuery availsCriteria;
    private RightTypeCorpAvailMap rightTypeCorpAvailMap;
    private Equivalence<? super RightStrand> rightStrandEquivalence;
    private Set<RightRequest> additionalRequests;
    
    private Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>>> unorderedExplodedResults;
    private Map<Set<LeafPMTLIdSet>, EquivalenceMap<RightStrand, InfoStrandParams>> infoOnlyStrandDetails;
    
    public AvailsRollup(
        Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>> criteriaRequestMap,
        AvailsQuery availsCriteria,
        RightTypeCorpAvailMap rightTypeCorpAvailMap,
        Equivalence<? super RightStrand> rightStrandEquivalence,
        Set<RightRequest> additionalRequests
    ) {
        this.criteriaRequestMap = criteriaRequestMap;
        this.availsCriteria = availsCriteria;
        this.rightTypeCorpAvailMap = rightTypeCorpAvailMap; 
        this.rightStrandEquivalence = rightStrandEquivalence;
        this.additionalRequests = additionalRequests;
        
        unorderedExplodedResults = new HashMap<>();
        infoOnlyStrandDetails = new HashMap<>();
    }
    
    public void addCalcResults(
        Set<LeafPMTLIdSet> pmtl,
        Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>> availabilities
    ) {
        Set<RightRequest> requestedRights = getCriteriaRequests(pmtl);
        requestedRights.addAll(additionalRequests);
        
        for (RightRequest request : requestedRights) {
            updateResults(
                pmtl,
                request,
                availabilities
            );
        }
    }

    public void addInfoStrands(
        Set<LeafPMTLIdSet> pmtl,
        RightStrand rightStrand,
        InfoStrandParams strandParams
    ) {
        EquivalenceMap<RightStrand, InfoStrandParams> strandMap = infoOnlyStrandDetails.get(pmtl);
        if (strandMap == null) {
            strandMap = new EquivalenceMap<>(rightStrandEquivalence);
            infoOnlyStrandDetails.put(pmtl, strandMap);
        }
        
        strandMap.merge(
            rightStrand,
            strandParams,
            InfoStrandParams::combine
        );
    }
    
    public Set<RightRequest> getCriteriaRequests(Set<LeafPMTLIdSet> pmtl) {
        Set<RightRequest> requestedRightTypes = new HashSet<>();
        
        for (CriteriaSource criteria : criteriaRequestMap.get(pmtl)) {
            requestedRightTypes.addAll(CollectionsUtil.select(
                criteria.getPrimaryRequests(), 
                r -> r.getElement()
            ));
            requestedRightTypes.addAll(CollectionsUtil.select(
                criteria.getSecondaryPreRequests(), 
                r -> r.getElement().getRightRequest()
            ));
            requestedRightTypes.addAll(CollectionsUtil.select(
                criteria.getSecondaryPostRequests(), 
                r -> r.getElement().getRightRequest()
            ));
        }
        
        return requestedRightTypes;
    }
    
    /**
     * Modifies the corp and nonCorp availabilities to not be UNSET. This is a destructive operation the modifies the original availabilityResult
     * @param availabilityResult
     */
    public static AvailabilityResultStruct reviseForNetCalc(AvailabilityResultStruct availabilityResult) {
        availabilityResult.setCorpAvailabilityResult(new AvailabilityResult(
            reviseAvailabilityForNetCalc(availabilityResult.getCorpAvailabilityResult().availability, true), 
            availabilityResult.getCorpAvailabilityResult().resultDetails
        ));
        
        availabilityResult.setNonCorpAvailabilityResult(new AvailabilityResult(
            reviseAvailabilityForNetCalc(availabilityResult.getNonCorpAvailabilityResult().availability, false), 
            availabilityResult.getNonCorpAvailabilityResult().resultDetails
        ));
        
        return availabilityResult;
    }
    
    /**
     * Revises UNSET availabilities to either NO or YES so that the final net availability is not UNSET
     * @param availability
     * @param isCorp
     * @return
     */
    private static Availability reviseAvailabilityForNetCalc(Availability availability, boolean isCorp) {
        if (availability != Availability.UNSET) {
            return availability;
        }

        //Upgrade all unfound corp flags to a NO since the complete lack of corporate strands implies we lack rights
        if (isCorp) {
            return Availability.NO;
        }

        return Availability.YES;
    }
    
    private void updateResults(
        Set<LeafPMTLIdSet> pmtl,
        RightRequest request,
        Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>> availabilities
    ) {
        Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> existingTermMap = unorderedExplodedResults
            .computeIfAbsent(pmtl, k -> new HashMap<>())
            .computeIfAbsent(request, k -> new HashMap<>());
        Map<Term, Map<TimePeriod, AvailabilityMetaData>> nonCorpAvailabilityTermMap = 
            availabilities.get(request);
        Map<Term, Map<TimePeriod, AvailabilityMetaData>> corpAvailabilityTermMap = 
            availabilities.get(AvailsCalculation.getCorpRequest(
                rightTypeCorpAvailMap.getRequiredCorpAvailRightType(request.getRightType())
            ));
        
        if (existingTermMap == null && nonCorpAvailabilityTermMap == null && corpAvailabilityTermMap == null) {
            return;
        }
        
        Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> revisedTermMap;
        if (nonCorpAvailabilityTermMap == null && corpAvailabilityTermMap == null) { //do no work if got no new availability info
            revisedTermMap = existingTermMap;
        } else {
            revisedTermMap = new HashMap<>();
            
            Map<TermPeriod, Set<TermPeriod>> cutTermPeriods = getCutTermPeriods(
                existingTermMap, 
                nonCorpAvailabilityTermMap,
                corpAvailabilityTermMap
            ); 
            
            for (Entry<TermPeriod, Set<TermPeriod>> termPeriodEntry : cutTermPeriods.entrySet()) {
                TermPeriod existingResultPeriod = getAppropriateSourcePeriod(
                    existingTermMap, 
                    termPeriodEntry.getValue()
                );
                TermPeriod nonCorpPeriod = getAppropriateSourcePeriod(
                    nonCorpAvailabilityTermMap, 
                    termPeriodEntry.getValue()
                );
                TermPeriod corpPeriod = getAppropriateSourcePeriod(
                    corpAvailabilityTermMap, 
                    termPeriodEntry.getValue()
                );
                
                LeafAvailsResultMeta mergedResultMeta;
                if (Objects.equals(existingResultPeriod, termPeriodEntry.getKey())) {
                    mergedResultMeta = existingTermMap.get(existingResultPeriod.getTerm())
                        .get(existingResultPeriod.getTimePeriod());
                } else if (existingResultPeriod != null) {
                    mergedResultMeta = new LeafAvailsResultMeta(
                        existingTermMap.get(existingResultPeriod.getTerm())
                            .get(existingResultPeriod.getTimePeriod())
                    );
                } else {
                    mergedResultMeta = new LeafAvailsResultMeta(
                        rightStrandEquivalence, 
                        new AvailabilityResultStruct()
                    );
                }
                
                if (nonCorpPeriod != null) {
                    updateResultMeta(
                        mergedResultMeta,
                        nonCorpAvailabilityTermMap.get(nonCorpPeriod.getTerm()).get(nonCorpPeriod.getTimePeriod()),
                        false
                     );
                }
                if (corpPeriod != null) {
                    updateResultMeta(
                        mergedResultMeta,
                        corpAvailabilityTermMap.get(corpPeriod.getTerm()).get(corpPeriod.getTimePeriod()),
                        true
                     );
                }
                
                revisedTermMap.computeIfAbsent(termPeriodEntry.getKey().getTerm(), k -> new HashMap<>())
                    .put(termPeriodEntry.getKey().getTimePeriod(), mergedResultMeta);
            }
        }
        unorderedExplodedResults.computeIfAbsent(pmtl, k -> new HashMap<>())
            .put(request, revisedTermMap);
    }
     
    private Map<TermPeriod, Set<TermPeriod>> getCutTermPeriods(
        Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> existingTermResults,
        Map<Term, Map<TimePeriod, AvailabilityMetaData>> nonCorpAvailabilityTermResults,
        Map<Term, Map<TimePeriod, AvailabilityMetaData>> corpAvailabilityTermResults
    ) {
        Map<Term, Set<TimePeriod>> cuttingTermPeriodMap = new HashMap<>();
        updateCuttingTermPeriodMap(cuttingTermPeriodMap, existingTermResults);
        updateCuttingTermPeriodMap(cuttingTermPeriodMap, nonCorpAvailabilityTermResults);
        updateCuttingTermPeriodMap(cuttingTermPeriodMap, corpAvailabilityTermResults);
        
        return DateTimeUtil.createCutTermPeriodMappings(cuttingTermPeriodMap);
    }
    
    private void updateCuttingTermPeriodMap(
        Map<Term, Set<TimePeriod>> cuttingTermPeriodMap,
        Map<Term, ? extends Map<TimePeriod, ?>> inputTermMap
    ) {
        if (inputTermMap != null) {
            for (Entry<Term, ? extends Map<TimePeriod, ?>> termEntry : inputTermMap.entrySet()) {
                cuttingTermPeriodMap.computeIfAbsent(termEntry.getKey(), k -> new HashSet<>())
                    .addAll(termEntry.getValue().keySet());
            }
        }
    }
    
    private TermPeriod getAppropriateSourcePeriod(
        Map<Term, ? extends Map<TimePeriod, ?>> sourceMap,
        Set<TermPeriod> potentialSourcePeriods
    ) {
        if (sourceMap != null) {
            for (TermPeriod tp : potentialSourcePeriods) {
                Map<TimePeriod, ?> timePeriodMap = sourceMap.get(tp.getTerm());
                if (timePeriodMap != null) {
                    if (timePeriodMap.containsKey(tp.getTimePeriod())) {
                        return tp;
                    }
                }
            }
        }
        return null;
    }
    
    private void updateResultMeta(
        LeafAvailsResultMeta mergedResult,
        AvailabilityMetaData availabilityMeta,
        boolean isCorp
    ) {
        if (isCorp) {
            mergedResult.availabilities.setCorpAvailabilityResult(AvailabilityResult.combine(
                mergedResult.availabilities.getCorpAvailabilityResult(),
                availabilityMeta.getAvailabilityResult()
            ));
        } else {
            mergedResult.availabilities.setNonCorpAvailabilityResult(AvailabilityResult.combine(
                mergedResult.availabilities.getNonCorpAvailabilityResult(),
                availabilityMeta.getAvailabilityResult()
            ));
        }
        
        for (Entry<RightStrand, AvailabilityResult> rightStrandImpactEntry : availabilityMeta.getRightStrandImpacts().entrySet()) {
            mergedResult.addRightStrandImpact(rightStrandImpactEntry.getKey(), rightStrandImpactEntry.getValue());
        }
    }
    
    public void getRollupResults(
        AvailsRollupResultProcessor rollupResultProcessor
    ) throws Exception {

        LOGGER.info("getRollupResults - Beginning to insert empty results");
        insertEmptyResults();
        LOGGER.info("getRollupResults - Finished inserting empty results.. beginning convertToResultMap()");
        
        AvailsRollupResult params = createRollupParams();
        
        rollupResultProcessor.processRollupResults(params);
    }
    
    private void insertEmptyResults() {
        //Empty maps so that we can use getOrDefault() without creating the dummy map every time
        Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>> emptyRequestMap = new HashMap<>();
        Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> emptyTermMap = new HashMap<>();
        Map<TimePeriod, LeafAvailsResultMeta> emptyPeriodMap = new HashMap<>();
        AvailabilityMetaData emptyAvailabilityMeta = new AvailabilityMetaData(rightStrandEquivalence);
        
        for (Set<LeafPMTLIdSet> pmtl : criteriaRequestMap.keySet()) {
            Map<RightRequest, Term> allRequestsForPMTL = AvailsRunnerHelper.getRequestTermMap(
                criteriaRequestMap.get(pmtl), 
                availsCriteria
            );
            for (RightRequest request : additionalRequests) {
                allRequestsForPMTL.put(request, availsCriteria.getEvaluatedPrimaryTerm());
            }
            
            Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>> requestResults = 
                unorderedExplodedResults.getOrDefault(pmtl, emptyRequestMap);
            
            Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>> missingMetaMap = new HashMap<>();
            
            for (Entry<RightRequest, Term> requestEntry : allRequestsForPMTL.entrySet()) {
                Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> termResults = requestResults
                    .getOrDefault(requestEntry.getKey(), emptyTermMap);
                
                Collection<Term> allTerms = DateTimeUtil.findGapTerms(
                    termResults.keySet(), 
                    requestEntry.getValue()
                );          
                allTerms.addAll(termResults.keySet());
                
                for (Term term : allTerms) {
                    Map<TimePeriod, LeafAvailsResultMeta> periodResults = termResults
                        .getOrDefault(term, emptyPeriodMap);
                    
                    TimePeriod leftoverPeriod = TimePeriod.subtractPeriods(
                        requestEntry.getKey().getTimePeriod(), 
                        TimePeriod.unionPeriods(periodResults.keySet())
                    );
                    
                    if (!leftoverPeriod.isEmpty()) {
                        missingMetaMap.computeIfAbsent(requestEntry.getKey(), k -> new HashMap<>())
                            .computeIfAbsent(term, k -> new HashMap<>())
                            .put(leftoverPeriod, emptyAvailabilityMeta);
                    }
                }
            }
            
            if (!missingMetaMap.isEmpty()) {
                addCalcResults(
                    pmtl,
                    missingMetaMap
                );
            }
        }
    }
    
    private AvailsRollupResult createRollupParams() {
        Map<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> additionalStrandDetails = new HashMap<>();
        
        for (Set<LeafPMTLIdSet> pmtl : criteriaRequestMap.keySet()) {
            Map<RightStrand, InfoStrandParams> infoStrandsForPMTL;
            if (infoOnlyStrandDetails.containsKey(pmtl)) {
                infoStrandsForPMTL = infoOnlyStrandDetails.get(pmtl).toMap();
            } else {
                infoStrandsForPMTL = new HashMap<>();
            }
            additionalStrandDetails.put(pmtl, infoStrandsForPMTL);
            
            //Revise any remaining UNSET availabilities
            for (Entry<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>> requestEntry : unorderedExplodedResults.get(pmtl).entrySet()) {
                for (Entry<Term, Map<TimePeriod, LeafAvailsResultMeta>> termEntry : requestEntry.getValue().entrySet()) {
                    for (Entry<TimePeriod, LeafAvailsResultMeta> periodEntry : termEntry.getValue().entrySet()) {
                        reviseForNetCalc(periodEntry.getValue().availabilities);
                    }
                }
            }
        }
        
        return new AvailsRollupResult(
            Collections.unmodifiableMap(unorderedExplodedResults),
            additionalStrandDetails
        );
    }
}
