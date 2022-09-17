package com.pdgc.general.calculation.corporate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.DroType;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.DateTimeUtil;

import lombok.Builder;

/**
 * Aggregating calculator that sits on top of the dro-specific calculators
 * @author Linda Xu
 *
 */
public class AggregateCorporateCalculator extends AbstractFoxDisneyCorporateCalculator {

    private Set<DroType> defaultDros; //defines what dros this calculator should expect
    private FoxCorporateCalculator foxCalculator;
    private DisneyCorporateCalculator disneyCalculator;
    
    @Builder
    public AggregateCorporateCalculator(
        Set<DroType> defaultDros,
        FoxCorporateCalculator foxCalculator,
        DisneyCorporateCalculator disneyCalculator
    ) {
        this.defaultDros = defaultDros;
        this.foxCalculator = foxCalculator;
        this.disneyCalculator = disneyCalculator;
        
        for (DroType dro : defaultDros) {
            if (getAppropriateCalculator(dro) == null) {
                throw new IllegalArgumentException("No calculator provided to support DRO type " + dro);
            }
        }
    }
    
    private AbstractFoxDisneyCorporateCalculator getAppropriateCalculator(DroType dro) {
        switch (dro) {
            case DISNEY:
                return disneyCalculator;
            case FFE:
            case FNG:
            default:
                return foxCalculator;
        }
    }
    
    @Override
    public boolean needsToGoThroughCalculator(RightStrand rightStrand) {
        DroType dro = DroType.byValue(((FoxRightStrand)rightStrand).getDistributionRightsOwner());
        return getAppropriateCalculator(dro).needsToGoThroughCalculator(rightStrand);
    }

    @Override
    public FoxAvailsCorporateCalculatorResult calculateForNonEmptyAvails(CorporateCalculatorParams params) {
        Map<DroType, CorporateCalculatorParams> sortedParams = sortStrandsByDro(params);
        
        Collection<FoxAvailsCorporateCalculatorResult> results = new ArrayList<>();
        for (Entry<DroType, CorporateCalculatorParams> entry : sortedParams.entrySet()) {
            AbstractFoxDisneyCorporateCalculator calculator = getAppropriateCalculator(entry.getKey());
            FoxAvailsCorporateCalculatorResult result = calculator.calculateForAvails(entry.getValue());
            results.add(result);
        }
        
        return createAggregateAvailsResult(params, results);
    }

    @Override
    public FoxConflictCorporateCalculatorResult calculateForNonEmptyConflictCheck(CorporateCalculatorParams params) {
        Map<DroType, CorporateCalculatorParams> sortedParams = sortStrandsByDro(params);
        
        Collection<FoxConflictCorporateCalculatorResult> results = new ArrayList<>();
        for (Entry<DroType, CorporateCalculatorParams> entry : sortedParams.entrySet()) {
            AbstractFoxDisneyCorporateCalculator calculator = getAppropriateCalculator(entry.getKey());
            FoxConflictCorporateCalculatorResult result = calculator.calculateForConflictCheck(entry.getValue());
            results.add(result);
        }
        
        return createAggregateConflictResult(params, results);
    }

    private Map<DroType, CorporateCalculatorParams> sortStrandsByDro(CorporateCalculatorParams params) {
        Map<DroType, Set<RightStrand>> sortedStrands = new HashMap<>();
        if (params.rightStrands.isEmpty()) {
            for (DroType dro : defaultDros) {
                sortedStrands.put(dro, new HashSet<>());
            }
        } else {
            for (RightStrand rs : params.rightStrands) {
                sortedStrands.computeIfAbsent(
                    DroType.byValue(((FoxRightStrand)rs).getDistributionRightsOwner()), 
                    k -> new HashSet<>()
                ).add(rs);
            }
        }
        
        Map<DroType, CorporateCalculatorParams> sortedParameters = new HashMap<>();
        for (Entry<DroType, Set<RightStrand>> entry : sortedStrands.entrySet()) {
            CorporateCalculatorParams newParams = params.toBuilder()
                    .rightStrands(entry.getValue())
                    .build();
            sortedParameters.put(entry.getKey(), newParams);
        }
        
        return sortedParameters;
    }

    private FoxAvailsCorporateCalculatorResult createAggregateAvailsResult(
        CorporateCalculatorParams params,
        Collection<FoxAvailsCorporateCalculatorResult> childResults
    ) {
        Map<RightType, Map<TermPeriod, AvailsCorpResult>> mergedAvailabilityResults = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            Multimap<TermPeriod, AvailsCorpResult> mappedSourceTermPeriods = ArrayListMultimap.create();
            for (FoxAvailsCorporateCalculatorResult childResult : childResults) {
                for (Entry<TermPeriod, AvailsCorpResult> termEntry : childResult.getAvailabilityResults().get(calcRequest.requestedRightType).entrySet()) {
                    mappedSourceTermPeriods.put(termEntry.getKey(), termEntry.getValue());
                }
            }
            
            Map<TermPeriod, Set<TermPeriod>> cutTermPeriods = DateTimeUtil.createCutTermPeriodMappings(
                DateTimeUtil.createTermPeriodMap(mappedSourceTermPeriods.keySet())
            );
            
            Map<TermPeriod, AvailsCorpResult> condensedResults = new HashMap<>();
            for (Entry<TermPeriod, Set<TermPeriod>> entry : cutTermPeriods.entrySet()) {
                Collection<AvailsCorpResult> mostAvailableResults = new ArrayList<>();
                Collection<AvailsCorpResult> otherResults = new ArrayList<>();
                
                for (TermPeriod sourcePeriod : entry.getValue()) {
                    for (AvailsCorpResult corpResult : mappedSourceTermPeriods.get(sourcePeriod)) {
                        if (mostAvailableResults.isEmpty()) {
                            mostAvailableResults.add(corpResult);
                        } else {
                            AvailsCorpResult templateResult = mostAvailableResults.iterator().next();
                            if (corpResult.getAvailabilityResult().availability.getSeverity() < templateResult.getAvailabilityResult().availability.getSeverity()) {
                                otherResults.addAll(mostAvailableResults);
                                mostAvailableResults.clear();
                                mostAvailableResults.add(corpResult);
                            }
                        }
                    }
                }
                
                AvailsCorpResult mergedCorpResult = 
                        createMergedAvailsCorpResult(mostAvailableResults, otherResults);
                condensedResults.put(entry.getKey(), mergedCorpResult);
            }
            
            mergedAvailabilityResults.put(calcRequest.requestedRightType, condensedResults);
        }
    
        Map<RightStrand, Set<FoxRecordReasonDetail>> mergedNonCuttingRights = new HashMap<>();
        Set<Term> mergedDistributionTerms = new HashSet<>();
        boolean tbaStartPresent = false;
        boolean tbaEndPresent = false;
        for (FoxAvailsCorporateCalculatorResult childResult : childResults) {
            mergedNonCuttingRights.putAll(childResult.getNonCuttingRights());
            mergedDistributionTerms.addAll(childResult.getDistributionTerms());
            tbaStartPresent |= childResult.isTbaStartPresent();
            tbaEndPresent |= childResult.isTbaEndPresent();
        }
        
        return new FoxAvailsCorporateCalculatorResult(
            mergedAvailabilityResults,
            mergedNonCuttingRights,
            mergedDistributionTerms, 
            tbaStartPresent, 
            tbaEndPresent
        );
    }
    
    private AvailsCorpResult createMergedAvailsCorpResult(
        Collection<AvailsCorpResult> impactingSourceResults,
        Collection<AvailsCorpResult> ignoredSourceResults
    ) {
        if (impactingSourceResults.size() == 1 && ignoredSourceResults.isEmpty()) {
            return impactingSourceResults.iterator().next();
        }
        
        AvailabilityResult mergedAvailabilityResult = null;
        Map<RightStrand, AvailabilityResult> mergedRightStrandImpact = new HashMap<>();
        
        for (AvailsCorpResult corpResult : impactingSourceResults) {
            mergedAvailabilityResult = AvailabilityResult.combine(
                mergedAvailabilityResult, 
                corpResult.getAvailabilityResult()
            );
            
            //Normally we'd use a merge() call here, 
            //but b/c right strands don't exist across dros, there should be no repeats
            mergedRightStrandImpact.putAll(corpResult.getRightStrandImpacts());
        }
        
        for (AvailsCorpResult corpResult : ignoredSourceResults) {
            for (Entry<RightStrand, AvailabilityResult> rsEntry : corpResult.getRightStrandImpacts().entrySet()) {
                Set<Object> revisedDetails = new HashSet<>(rsEntry.getValue().resultDetails);
                revisedDetails.add(FoxRecordReasonDetail.UNSELECTED_DRO);
                mergedRightStrandImpact.put(
                    rsEntry.getKey(), 
                    new AvailabilityResult(Availability.UNSET, revisedDetails)
                );
            }
        }
        
        return new AvailsCorpResult(mergedAvailabilityResult, mergedRightStrandImpact);
    }
    
    private FoxConflictCorporateCalculatorResult createAggregateConflictResult(
        CorporateCalculatorParams params,
        Collection<FoxConflictCorporateCalculatorResult> childResults
    ) {
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> mergedAvailabilityResults = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            Multimap<TermPeriod, ConflictCorpResult> mappedSourceTermPeriods = ArrayListMultimap.create();
            for (FoxConflictCorporateCalculatorResult childResult : childResults) {
                for (Entry<TermPeriod, ConflictCorpResult> termEntry : childResult.getAvailabilityResults().get(calcRequest.requestedRightType).entrySet()) {
                    mappedSourceTermPeriods.put(termEntry.getKey(), termEntry.getValue());
                }
            }
            
            Map<TermPeriod, Set<TermPeriod>> cutTermPeriods = DateTimeUtil.createCutTermPeriodMappings(
                DateTimeUtil.createTermPeriodMap(mappedSourceTermPeriods.keySet())
            );
            
            Map<TermPeriod, ConflictCorpResult> condensedResults = new HashMap<>();
            for (Entry<TermPeriod, Set<TermPeriod>> entry : cutTermPeriods.entrySet()) {
                Collection<ConflictCorpResult> mostAvailableResults = new ArrayList<>();
                Collection<ConflictCorpResult> otherResults = new ArrayList<>();
                
                for (TermPeriod sourcePeriod : entry.getValue()) {
                    for (ConflictCorpResult corpResult : mappedSourceTermPeriods.get(sourcePeriod)) {
                        if (mostAvailableResults.isEmpty()) {
                            mostAvailableResults.add(corpResult);
                        } else {
                            ConflictCorpResult templateResult = mostAvailableResults.iterator().next();
                            if (corpResult.getAvailabilityResult().availability.getSeverity() < templateResult.getAvailabilityResult().availability.getSeverity()) {
                                otherResults.addAll(mostAvailableResults);
                                mostAvailableResults.clear();
                                mostAvailableResults.add(corpResult);
                            }
                        }
                    }
                }
                
                ConflictCorpResult mergedCorpResult = createMergedConflictCorpResult(mostAvailableResults);
                condensedResults.put(entry.getKey(), mergedCorpResult);
            }
            
            mergedAvailabilityResults.put(calcRequest.requestedRightType, condensedResults);
        }
        
        return new FoxConflictCorporateCalculatorResult(
            mergedAvailabilityResults
        );
    }
    
    private ConflictCorpResult createMergedConflictCorpResult(
        Collection<ConflictCorpResult> impactingSourceResults
    ) {
        if (impactingSourceResults.size() == 1) {
            return impactingSourceResults.iterator().next();
        }
        
        AvailabilityResult mergedAvailabilityResult = null;
        Set<RightStrand> conflictRightStrands = new HashSet<>();
        
        for (ConflictCorpResult corpResult : impactingSourceResults) {
            mergedAvailabilityResult = AvailabilityResult.combine(
                mergedAvailabilityResult, 
                corpResult.getAvailabilityResult()
            );
            
            conflictRightStrands.addAll(corpResult.getConflictRightStrands());
        }
        
        return new ConflictCorpResult(mergedAvailabilityResult, conflictRightStrands);
    }
}
