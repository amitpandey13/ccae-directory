package com.pdgc.avails.structures.calculation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;

public class CondensedAvailsCalcResult implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Map<Integer, Set<LeafPMTLIdSet>> reversePMTLMap;
    private Map<Integer, RightRequest> reverseRightRequestMap;
    private Map<Integer, Term> reverseTermMap;
    private Map<Integer, TimePeriod> reverseTimePeriodMap;
    private Map<Integer, AvailabilityMetaData> reverseMetaDataMap;
    
    private Map<Set<LeafPMTLIdSet>, Integer> pmtlMap;
    private Map<RightRequest, Integer> rightRequestMap;
    private Map<Term, Integer> termMap;
    private Map<TimePeriod, Integer> timePeriodMap;
    private Map<AvailabilityMetaData, Integer> metaDataMap;

    private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> condensedResults;
    private Map<Integer, Map<RightStrand, InfoStrandParams>> condensedInfoStrands;
    private Map<Integer, AvailsCorporateCalculatorResult> condensedCorpResults;

    public CondensedAvailsCalcResult() {
        reversePMTLMap = new HashMap<>();
        reverseTermMap = new HashMap<>();
        reverseTimePeriodMap = new HashMap<>();
        reverseRightRequestMap = new HashMap<>();
        reverseMetaDataMap = new HashMap<>();

        condensedResults = new HashMap<>();
        condensedInfoStrands = new HashMap<>();
        condensedCorpResults = new HashMap<>();
    }

    public CondensedAvailsCalcResult(
        AvailsCalculationResult availsCalcResult
    ) {
        reversePMTLMap = new HashMap<>();
        reverseTermMap = new HashMap<>();
        reverseTimePeriodMap = new HashMap<>();
        reverseRightRequestMap = new HashMap<>();
        reverseMetaDataMap = new HashMap<>();
        pmtlMap = new HashMap<>();
        termMap = new HashMap<>();
        timePeriodMap = new HashMap<>();
        rightRequestMap = new HashMap<>();
        metaDataMap = new HashMap<>();

        condensedResults = new HashMap<>();
        for (Entry<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>>> pmtlEntry : availsCalcResult.getCalcResults().entrySet()) {
            int pmtlDummy = addPMTLMapping(pmtlEntry.getKey());
            Map<Integer, Map<Integer, Map<Integer, Integer>>> requestResults = new HashMap<>();
            condensedResults.put(pmtlDummy, requestResults);

            for (Entry<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>> requestEntry : pmtlEntry.getValue().entrySet()) {
                Integer requestDummy = addRightRequestMapping(requestEntry.getKey());
                Map<Integer, Map<Integer, Integer>> termResults = new HashMap<>();
                requestResults.put(requestDummy, termResults);
                
                for (Entry<Term, Map<TimePeriod, AvailabilityMetaData>> termEntry : requestEntry.getValue().entrySet()) {
                    Integer termDummy = addTermMapping(termEntry.getKey()); 
                    Map<Integer, Integer> periodResults = new HashMap<Integer, Integer>();
                    termResults.put(termDummy, periodResults);
                    
                    for (Entry<TimePeriod, AvailabilityMetaData> periodEntry : termEntry.getValue().entrySet()) {
                        Integer timePeriodDummy = addTimePeriodMapping(periodEntry.getKey());
                        Integer metaDataDummy = addMetaDataMapping(periodEntry.getValue());
                        periodResults.put(timePeriodDummy, metaDataDummy);
                    }
                }
            }
        }
    
        condensedInfoStrands = new HashMap<>();
        for (Entry<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> pmtlEntry : availsCalcResult.getInfoOnlyStrands().entrySet()) {
            int pmtlDummy = addPMTLMapping(pmtlEntry.getKey());
            condensedInfoStrands.put(pmtlDummy, pmtlEntry.getValue());
        }
        
        condensedCorpResults = new HashMap<>();
        for (Entry<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> pmtlEntry : availsCalcResult.getCorpResults().entrySet()) {
            int pmtlDummy = addPMTLMapping(pmtlEntry.getKey());
            condensedCorpResults.put(pmtlDummy, pmtlEntry.getValue());
        }
    }

    public CondensedAvailsCalcResult(
        Map<Integer, Set<LeafPMTLIdSet>> reversePMTLMap, 
        Map<Integer, Term> reverseTermMap, 
        Map<Integer, TimePeriod> reverseTimePeriodMap,
        Map<Integer, RightRequest> reverseRightRequestMap, 
        Map<Integer, AvailabilityMetaData> reverseMetaDataMap, 
        Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> condensedAvailsCalcResults
    ) {
        this.reversePMTLMap = reversePMTLMap;
        this.reverseTermMap = reverseTermMap;
        this.reverseTimePeriodMap = reverseTimePeriodMap;
        this.reverseRightRequestMap = reverseRightRequestMap;
        this.reverseMetaDataMap = reverseMetaDataMap;

        this.condensedResults = condensedAvailsCalcResults;
    }

    private int addPMTLMapping(Set<LeafPMTLIdSet> pmtl) {
        Integer pmtlDummy = pmtlMap.get(pmtl); 
        if (pmtlDummy == null) {
            pmtlDummy = pmtlMap.size();
            pmtlMap.put(pmtl, pmtlDummy);
            reversePMTLMap.put(pmtlDummy, pmtl);
        }
        return pmtlDummy;
    }

    private int addTermMapping(Term term) {
        Integer termDummy = termMap.get(term);
        if (termDummy == null) {
            termDummy = termMap.size();
            termMap.put(term, termDummy);
            reverseTermMap.put(termDummy, term);
        }
        return termDummy;
    }
    
    private int addTimePeriodMapping(TimePeriod timePeriod) {
        Integer timePeriodDummy = timePeriodMap.get(timePeriod);
        if (timePeriodDummy == null) {
            timePeriodDummy = timePeriodMap.size();
            timePeriodMap.put(timePeriod, timePeriodDummy);
            reverseTimePeriodMap.put(timePeriodDummy, timePeriod);
        }
        return timePeriodDummy;
    }

    private int addRightRequestMapping(RightRequest rightType) {
        Integer rightTypeDummy = rightRequestMap.get(rightType);
        if (rightTypeDummy == null) {
            rightTypeDummy = rightRequestMap.size();
            rightRequestMap.put(rightType, rightTypeDummy);
            reverseRightRequestMap.put(rightTypeDummy, rightType);
        }
        return rightTypeDummy;
    }

    private int addMetaDataMapping(AvailabilityMetaData metaData) {
        Integer metaDataDummy = metaDataMap.get(metaData);
        if (metaDataDummy == null) {
            metaDataDummy = metaDataMap.size();
            reverseMetaDataMap.put(metaDataDummy, new AvailabilityMetaData(metaData)); // Need to deep-copy this object
            metaDataMap.put(reverseMetaDataMap.get(metaDataDummy), metaDataDummy);
        }
        return metaDataDummy;
    }
    
    /**
     * Returns a full version of the availsCalcResults. Note: it is NOT safe to
     *  modify the objects (particularly AvailabilityMetaData)
     *  of these results, as multiple map entries will point to the same object
     *  reference, though the dictionaries themselves are safe to touch
     * @return
     */
    public AvailsCalculationResult getFullAvailsCalcResult() {
        return new AvailsCalculationResult(
            Collections.unmodifiableMap(CollectionsUtil.toMap(
                condensedResults.entrySet(), 
                pmtlkv -> Collections.unmodifiableSet(reversePMTLMap.get(pmtlkv.getKey())), 
                pmtlkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
                    pmtlkv.getValue().entrySet(),
                    rkv -> reverseRightRequestMap.get(rkv.getKey()),
                    rkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
                        rkv.getValue().entrySet(),
                        tkv -> reverseTermMap.get(tkv.getKey()),
                        tkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
                            tkv.getValue().entrySet(),
                            tpkv -> reverseTimePeriodMap.get(tpkv.getKey()),
                            tpkv -> reverseMetaDataMap.get(tpkv.getValue())
                        ))
                    ))
                ))
            )),
            Collections.unmodifiableMap(CollectionsUtil.toMap(
                condensedInfoStrands.entrySet(), 
                kv -> Collections.unmodifiableSet(reversePMTLMap.get(kv.getKey())), 
                kv -> Collections.unmodifiableMap(kv.getValue())
            )),
            Collections.unmodifiableMap(CollectionsUtil.toMap(
                condensedCorpResults.entrySet(),
                kv -> Collections.unmodifiableSet(reversePMTLMap.get(kv.getKey())),
                kv -> kv.getValue()
            ))
        );
    }
}
