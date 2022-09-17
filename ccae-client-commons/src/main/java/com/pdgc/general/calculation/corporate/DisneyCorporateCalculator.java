package com.pdgc.general.calculation.corporate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.classificationEnums.RightTypeType;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.FoxRestrictionStrand;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.IntegerConversionUtil;

import lombok.Builder;

/**
 * Corporate calculator for disney corporate rights, which follow different rules
 * than the ones created by Fox. 
 * Ex: music distribution is calculated using its own distribution rights separate from the 
 *  normal distribution rights
 * @author Linda Xu
 *
 */
public class DisneyCorporateCalculator extends AbstractFoxDisneyCorporateCalculator {
    
    private RightTypeImpactMatrix rightTypeImpactMatrix;
    private NearestStrandsCalculator nearestStrandsCalculator;
    
    private RightType musicAvailRequest;
    
    @Builder
    public DisneyCorporateCalculator(
        RightTypeImpactMatrix rightTypeImpactMatrix,
        NearestStrandsCalculator nearestStrandsCalculator
    ) {
        this.rightTypeImpactMatrix = rightTypeImpactMatrix;
        this.nearestStrandsCalculator = nearestStrandsCalculator;
        
        this.musicAvailRequest = new RightType(
                Constants.MUSIC_AVAILS_RIGHT_TYPE_ID, 
                "Music Availability", 
                "Music Avail", 
                RightTypeType.CORP_AVAIL, 
                false,
                0
        );
    }

    @Override
    public boolean needsToGoThroughCalculator(RightStrand rightStrand) {
        return rightStrand instanceof CorporateRightStrand;
    }

    @Override
    public FoxAvailsCorporateCalculatorResult calculateForNonEmptyAvails(CorporateCalculatorParams params) {
        boolean needsMusicRights = needsMusicRights(params);
        MusicAnalysisResults musicResults = null;
        Map<RightStrand, Set<FoxRecordReasonDetail>> ignoredRights = new HashMap<>();
        if (needsMusicRights) {
            musicResults = calculateMusicAvailability(params);
            for (RightStrand rs : musicResults.nonNearestStrands) {
                ignoredRights.put(rs, Sets.newHashSet(FoxRecordReasonDetail.NOT_NEAREST));
            }
        }
        
        DistributionResults distributionResults = analyzeDistributionRights(params.rightStrands);
        
        Map<RightType, Map<TermPeriod, AvailsCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            TermPeriodAnalysisResults corpResult = calculateCorpAvailabilities(calcRequest, params.rightStrands);
            
            if (needsDistributionRights(calcRequest.requestedRightType)) {
                TermPeriodAnalysisResults mergedResult = 
                        mergeTermPeriodResults(calcRequest, corpResult, musicResults);
                for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : mergedResult.availabilityMap.entrySet()) {
                    TermPeriod termPeriod = termPeriodEntry.getKey();
                    AvailabilityResult availabilityResult = termPeriodEntry.getValue();
                    
                    boolean hasDistributionRights = CollectionsUtil.any(
                        distributionResults.orderedDistributionTerms, 
                        t -> Term.hasIntersection(t, termPeriod.getTerm())
                    );
                    
                    //add the correct detail (pre-/post-/no-distribution window details to all windows missing distribution rights)
                    if (!hasDistributionRights) {
                        Set<Object> availabilityDetails = new HashSet<>(termPeriodEntry.getValue().resultDetails);
                        if (distributionResults.orderedDistributionTerms.isEmpty()) {
                            availabilityDetails.add(FoxWindowReasonDetail.NO_VALID_DISTRIBUTION);
                        } else if (termPeriod.getStartDate().isAfter(distributionResults.orderedDistributionTerms.last().getEndDate())) {
                            availabilityDetails.add(FoxWindowReasonDetail.POST_DISTRIBUTION);
                        } else {
                            availabilityDetails.add(FoxWindowReasonDetail.PRE_DISTRIBUTION);
                        }
                        
                        availabilityResult = new AvailabilityResult(termPeriodEntry.getValue().availability, availabilityDetails);
                    }
                    
                    Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>(mergedResult.rightStrandImpactMap.get(termPeriod));
                    
                    resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                        .put(termPeriodEntry.getKey(), new AvailsCorpResult(availabilityResult, rightStrandImpacts));
                }
            } else {
                for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : corpResult.availabilityMap.entrySet()) {
                    TermPeriod termPeriod = termPeriodEntry.getKey();
                    AvailabilityResult availabilityResult = termPeriodEntry.getValue();
                    
                    Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>(corpResult.rightStrandImpactMap.get(termPeriod));
                    
                    resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                        .put(termPeriodEntry.getKey(), new AvailsCorpResult(availabilityResult, rightStrandImpacts));
                }
            }
        }
        
        return FoxAvailsCorporateCalculatorResult.builder()
            .availabilityResults(resultMap)
            .nonCuttingRights(ignoredRights)
            .distributionTerms(distributionResults.orderedDistributionTerms)
            .tbaStartPresent(distributionResults.hasTBAStart)
            .tbaEndPresent(distributionResults.hasTBAEnd)
            .build();
    }

    @Override
    public FoxConflictCorporateCalculatorResult calculateForNonEmptyConflictCheck(CorporateCalculatorParams params) {
        boolean needsMusicRights = needsMusicRights(params);
        MusicAnalysisResults musicResults = null;
        Set<RightStrand> ignoredRights = new HashSet<>();
        if (needsMusicRights) {
            musicResults = calculateMusicAvailability(params);
            ignoredRights = musicResults.nonNearestStrands;
        }
        
        Map<RightType, Map<TermPeriod, ConflictCorpResult>> resultMap = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            TermPeriodAnalysisResults corpResult = calculateCorpAvailabilities(calcRequest, params.rightStrands);
            
            if (needsDistributionRights(calcRequest.requestedRightType)) {
                TermPeriodAnalysisResults mergedResult = 
                        mergeTermPeriodResults(calcRequest, corpResult, musicResults);
                for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : mergedResult.availabilityMap.entrySet()) {
                    TermPeriod termPeriod = termPeriodEntry.getKey();
                    AvailabilityResult availabilityResult = termPeriodEntry.getValue();
                    
                    Set<RightStrand> conflictStrands = 
                            new HashSet<>(mergedResult.rightStrandImpactMap.get(termPeriod).keySet());
                    conflictStrands.removeAll(ignoredRights);
                    
                    resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                        .put(termPeriodEntry.getKey(), new ConflictCorpResult(availabilityResult, conflictStrands));
                }
            } else {
                for (Entry<TermPeriod, AvailabilityResult> termPeriodEntry : corpResult.availabilityMap.entrySet()) {
                    TermPeriod termPeriod = termPeriodEntry.getKey();
                    AvailabilityResult availabilityResult = termPeriodEntry.getValue();
                    
                    Set<RightStrand> conflictStrands = 
                            new HashSet<>(corpResult.rightStrandImpactMap.get(termPeriod).keySet());
                    conflictStrands.removeAll(ignoredRights);
                    
                    resultMap.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                        .put(termPeriodEntry.getKey(), new ConflictCorpResult(availabilityResult, conflictStrands));
                }
            }
        }
        
        return new FoxConflictCorporateCalculatorResult(resultMap);
    }
    
    private DistributionResults analyzeDistributionRights(Set<RightStrand> rightStrands) {
        DistributionResults result = new DistributionResults();
        
        Set<Term> distributionTerms = new HashSet<>();
        for (RightStrand rs : rightStrands) {
            if (isCorporateDistribution(rs)) {
                distributionTerms.add(rs.getTerm());
            }
        }
        
        Map<Term, Set<Term>> cutDistributionTerms = DateTimeUtil.createCutTermMappings(distributionTerms);
        result.orderedDistributionTerms = new TreeSet<>(cutDistributionTerms.keySet());
        
        return result;
    }
    
    private boolean isCorporateDistribution(RightStrand rs) {
        return IntegerConversionUtil.longEquals(rs.getRightType().getRightTypeId(), Constants.RIGHT_TYPE_ID_DISNEY_DISTRIBUTION);
    }
    
    private TermPeriodAnalysisResults calculateCorpAvailabilities(
        CorporateCalculationRequest calcRequest,
        Set<RightStrand> rightStrands
    ) {
        Set<RightStrand> relevantStrands = getImpactingStrands(
            calcRequest.requestedRightType, 
            rightStrands
        );
        Map<TermPeriod, Set<RightStrand>> sortedStrands = sortRightStrands(
            relevantStrands,
            calcRequest.requestedTerm,
            calcRequest.requestedTimePeriod
        );
        
        boolean needsDistribution = needsDistributionRights(calcRequest.requestedRightType);
        
        TermPeriodAnalysisResults result = new TermPeriodAnalysisResults();
        result.availabilityMap = new HashMap<>();
        result.rightStrandImpactMap = new HashMap<>();
        
        for (Entry<TermPeriod, Set<RightStrand>> entry : sortedStrands.entrySet()) {
            TermPeriod termPeriod = entry.getKey();
            Availability windowAvailability = getDefaultAvailability(calcRequest.requestedRightType);
            Set<FoxWindowReasonDetail> windowDetails = new HashSet<>();
            Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>();
            
            for (RightStrand rs : entry.getValue()) {
                Availability strandAvailability = rightTypeImpactMatrix.getAvailabilityImpact(calcRequest.requestedRightType, rs.getRightType());
                Set<FoxRecordReasonDetail> strandResultDetails = new HashSet<>();
                
                if (rs instanceof FoxRestrictionStrand && strandAvailability == Availability.NO) {
                    strandResultDetails.add(FoxRecordReasonDetail.RESTRICTS_AVAILABILITY);
                } else if (!needsDistribution) {
                    // Guarantees that the restriction does not get displayed as an impacting Restriction Code. 
                    // Marked as No Impact if the requested right is an Exclusivity and the restriction does not impact availability
                    strandResultDetails.add(FoxRecordReasonDetail.NO_IMPACT_RESTRICTION);
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
                
                rightStrandImpacts.put(rs, strandAvailabilityResult);
            }
            
            result.availabilityMap.put(termPeriod, new AvailabilityResult(windowAvailability, windowDetails));
            result.rightStrandImpactMap.put(termPeriod, rightStrandImpacts);
        }
        
        return result;
    }
    
    private boolean needsMusicRights(CorporateCalculatorParams params) {
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            if (needsDistributionRights(calcRequest.requestedRightType)) {
                return true;
            }
        }
        return false;
    }
    
    private MusicAnalysisResults calculateMusicAvailability(
        CorporateCalculatorParams params
    ) {
        Set<RightStrand> musicStrands = getImpactingStrands(musicAvailRequest, params.rightStrands);
        Set<RightStrand> relevantStrands = nearestStrandsCalculator.findNearestMusicStrands(params.requestedPMTL, musicStrands);
        
        MusicAnalysisResults result = new MusicAnalysisResults();
        result.availabilityMap = new HashMap<>();
        result.rightStrandImpactMap = new HashMap<>();
        result.nonNearestStrands = Sets.difference(musicStrands, relevantStrands);
        
        Term requestedTerm = Term.getUnion(CollectionsUtil.select(
            params.calcRequests, 
            r -> r.requestedTerm
        ));
        TimePeriod requestedTimePeriod = TimePeriod.unionPeriods(CollectionsUtil.select(
            params.calcRequests, 
            r -> r.requestedTimePeriod
        ));
        
        Map<TermPeriod, Set<RightStrand>> sortedStrands = sortRightStrands(
            relevantStrands,
            requestedTerm,
            requestedTimePeriod
        );
        
        for (Entry<TermPeriod, Set<RightStrand>> entry : sortedStrands.entrySet()) {
            TermPeriod termPeriod = entry.getKey();
            Availability windowAvailability = getDefaultAvailability(musicAvailRequest);
            Set<FoxWindowReasonDetail> windowDetails = new HashSet<>();
            Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>();
            
            for (RightStrand rs : entry.getValue()) {
                Availability strandAvailability = rightTypeImpactMatrix.getAvailabilityImpact(musicAvailRequest, rs.getRightType());
                AvailabilityResult strandAvailabilityResult = new AvailabilityResult(strandAvailability, new HashSet<>());
                
                windowAvailability = AvailabilityHelper.combineAvailability(windowAvailability, strandAvailabilityResult.availability);
                rightStrandImpacts.put(rs, strandAvailabilityResult);
            }
            
            if (windowAvailability == Availability.NO || windowAvailability == Availability.UNSET) {
                windowDetails.add(FoxWindowReasonDetail.NO_MUSIC_RIGHTS);
            }
            
            result.availabilityMap.put(termPeriod, new AvailabilityResult(windowAvailability, windowDetails));
            result.rightStrandImpactMap.put(termPeriod, rightStrandImpacts);
        }
        
        return result;
    }
    
    private Set<RightStrand> getImpactingStrands(
        RightType corpType, 
        Collection<RightStrand> rightStrands
    ) {
        Set<RightStrand> impactingStrands = new HashSet<>();
        for (RightStrand rs : rightStrands) {
            if (rightTypeImpactMatrix.impactsAvailability(corpType, rs.getRightType())) {
                impactingStrands.add(rs);
            }
        }
        return impactingStrands;
    }
    
    private TermPeriodAnalysisResults mergeTermPeriodResults(
        CorporateCalculationRequest calcRequest,
        TermPeriodAnalysisResults corpResults,
        TermPeriodAnalysisResults musicResults
    ) {
        Multimap<TermPeriod, Integer> mappedSourceTermPeriods = ArrayListMultimap.create();
        for (TermPeriod tp : corpResults.availabilityMap.keySet()) {
            mappedSourceTermPeriods.put(tp, 1);
        }
        for (TermPeriod tp : musicResults.availabilityMap.keySet()) {
            mappedSourceTermPeriods.put(tp, 2);
        }
        
        Map<TermPeriod, Set<TermPeriod>> cutTermPeriods = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(mappedSourceTermPeriods.keySet())
        );
        
        TermPeriodAnalysisResults mergedResult = new TermPeriodAnalysisResults();
        mergedResult.availabilityMap = new HashMap<>();
        mergedResult.rightStrandImpactMap = new HashMap<>();
        
        TermPeriod requestedTermPeriod = new TermPeriod(calcRequest.requestedTerm, calcRequest.requestedTimePeriod);
        for (Entry<TermPeriod, Set<TermPeriod>> entry : cutTermPeriods.entrySet()) {
            if (!TermPeriod.hasIntersection(entry.getKey(), requestedTermPeriod)) {
                continue;
            }
            
            AvailabilityResult availabilityResult = null;
            Map<RightStrand, AvailabilityResult> rightStrandImpact = new HashMap<>();
            
            for (TermPeriod sourcePeriod : entry.getValue()) {
                for (Integer source : mappedSourceTermPeriods.get(sourcePeriod)) {
                    if (source == 1) {
                        availabilityResult = AvailabilityResult.combine(
                            availabilityResult, 
                            sanitizeUnsets(corpResults.availabilityMap.get(sourcePeriod))
                        );
                        for (Entry<RightStrand, AvailabilityResult> rsEntry : corpResults.rightStrandImpactMap.get(sourcePeriod).entrySet()) {
                            rightStrandImpact.merge(
                                rsEntry.getKey(), 
                                rsEntry.getValue(), 
                                AvailabilityResult::combine
                            );
                        }
                    } else {
                        availabilityResult = AvailabilityResult.combine(
                            availabilityResult, 
                            sanitizeUnsets(musicResults.availabilityMap.get(sourcePeriod))
                        );
                        for (Entry<RightStrand, AvailabilityResult> rsEntry : musicResults.rightStrandImpactMap.get(sourcePeriod).entrySet()) {
                            rightStrandImpact.merge(
                                rsEntry.getKey(), 
                                rsEntry.getValue(), 
                                AvailabilityResult::combine
                            );
                        }
                    }
                }
            }
        
            mergedResult.availabilityMap.put(entry.getKey(), availabilityResult);
            mergedResult.rightStrandImpactMap.put(entry.getKey(), rightStrandImpact);
        }
        
        return mergedResult;
    }
    
    /**
     * Converts all UNSET availabilities to NO before merging music and corp
     * @return
     */
    private AvailabilityResult sanitizeUnsets(AvailabilityResult origAvailabilityResult) {
        if (origAvailabilityResult.availability != Availability.UNSET) {
            return origAvailabilityResult;
        }
        
        return new AvailabilityResult(Availability.NO, origAvailabilityResult.resultDetails);
    }
    
    /**
     * Stores the meta info needed for the avails result after analyzing the 
     * non-music distribution rights 
     * @author Linda Xu
     */
    private class DistributionResults {
        SortedSet<Term> orderedDistributionTerms;
        boolean hasTBAStart = false;
        boolean hasTBAEnd = false;
    }
    
    /**
     * Separates the normal distribution from the music distribution results.
     * Since music distribution undergoes its own 'nearest match' logic, this means
     * it also produces its own set of info-only strands
     * @author Linda Xu
     */
    private class MusicAnalysisResults extends TermPeriodAnalysisResults {
        Set<RightStrand> nonNearestStrands;
    }
}
