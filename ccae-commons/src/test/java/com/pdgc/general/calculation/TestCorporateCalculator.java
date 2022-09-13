
package com.pdgc.general.calculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.calculation.corporate.AvailsCorpResult;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.calculation.corporate.ConflictCorpResult;
import com.pdgc.general.calculation.corporate.ConflictCorporateCalculatorResult;
import com.pdgc.general.calculation.corporate.CorpResult;
import com.pdgc.general.calculation.corporate.CorporateCalculationRequest;
import com.pdgc.general.calculation.corporate.CorporateCalculator;
import com.pdgc.general.calculation.corporate.CorporateCalculatorParams;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestRestrictionStrand;
import com.pdgc.general.util.CollectionsUtil;

public class TestCorporateCalculator extends CorporateCalculator {
	
	RightTypeImpactMatrix rightTypeImpactMatrix;
	
	public TestCorporateCalculator(
		RightTypeImpactMatrix rightTypeImpactMatrix
	) {
		this.rightTypeImpactMatrix = rightTypeImpactMatrix;
	}
	
	@Override
	public boolean needsToGoThroughCalculator(RightStrand rightStrand) {
		return rightStrand instanceof CorporateRightStrand;
	}

	@Override
	public AvailsCorporateCalculatorResult calculateForAvails(
        CorporateCalculatorParams params
    ) {
	    Map<RightType, Map<TermPeriod, AvailsCorpResult>> results = new HashMap<>();
        
	    for (CorporateCalculationRequest calcRequest : params.calcRequests) {
	        Map<TermPeriod, Set<CorporateRightStrand>> sortedStrands = sortRightStrands(
                CollectionsUtil.select(params.rightStrands, rs -> (CorporateRightStrand)rs),
                calcRequest.requestedTerm,
                calcRequest.requestedTimePeriod
            );
	        
	        for (Entry<TermPeriod, Set<CorporateRightStrand>> termPeriodEntry : sortedStrands.entrySet()) {
	            results.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
	                .put(termPeriodEntry.getKey(), (AvailsCorpResult) generateTermPeriodResult(
                        termPeriodEntry.getValue(),
                        calcRequest.requestedRightType,
                        true
                    ));
	        }
	    }
	    return new TestAvailsCorporateCalculatorResult(results);
	}
    
	@Override
    public ConflictCorporateCalculatorResult calculateForConflictCheck(
        CorporateCalculatorParams params
    ) {
	    Map<RightType, Map<TermPeriod, ConflictCorpResult>> results = new HashMap<>();
        for (CorporateCalculationRequest calcRequest : params.calcRequests) {
            Map<TermPeriod, Set<CorporateRightStrand>> sortedStrands = sortRightStrands(
                CollectionsUtil.select(params.rightStrands, rs -> (CorporateRightStrand)rs),
                calcRequest.requestedTerm,
                calcRequest.requestedTimePeriod
            );
            
            for (Entry<TermPeriod, Set<CorporateRightStrand>> termPeriodEntry : sortedStrands.entrySet()) {
                results.computeIfAbsent(calcRequest.requestedRightType, k -> new HashMap<>())
                    .put(termPeriodEntry.getKey(), (ConflictCorpResult) generateTermPeriodResult(
                        termPeriodEntry.getValue(),
                        calcRequest.requestedRightType,
                        false
                    ));
            }
        }
        return new TestConflictCorporateCalculatorResult(results);
    }
	
	private CorpResult generateTermPeriodResult(
	    Set<CorporateRightStrand> termPeriodStrands,
	    RightType corpType,
	    boolean isAvailsContext //true for avails, false for conflict-check
	) {
	    Collection<CorporateRightStrand> calcRelevantStrands = getCalcRelevantStrands(termPeriodStrands);
	    
	    AvailabilityResult availabilityResult = new AvailabilityResult();
        Map<RightStrand, AvailabilityResult> rightStrandImpacts = new HashMap<>();
        Set<RightStrand> relevantStrands = new HashSet<>();
        
        for (CorporateRightStrand corpStrand : calcRelevantStrands) {
            AvailabilityResult strandAvailabilityResult = new AvailabilityResult( 
                rightTypeImpactMatrix.getAvailabilityImpact(corpType, corpStrand.getRightType()),
                new HashSet<>()
            );
            
            availabilityResult = AvailabilityResult.combine(availabilityResult, strandAvailabilityResult);
            rightStrandImpacts.put(corpStrand, strandAvailabilityResult);
            relevantStrands.add(corpStrand);
        }
        
        if (isAvailsContext) {
            //If the non-max group strands are wanted, insert them into the RightStrandImpacts map here...
            
            return new AvailsCorpResult(availabilityResult, rightStrandImpacts);
        }
        else {
            return new ConflictCorpResult(availabilityResult, relevantStrands);
        }
	}
	
	private Collection<CorporateRightStrand> getCalcRelevantStrands(Collection<CorporateRightStrand> corpStrands) {
		
		if (corpStrands.isEmpty()) {
			return new ArrayList<>();
		}
		
		Map<Integer, Collection<CorporateRightStrand>> rightStrandOrderMap = sortRightStrandsByCalculationOrder(corpStrands);
		int maxCalcOrder = rightStrandOrderMap.keySet().stream().max(Integer::compareTo).get();
		
        Collection<CorporateRightStrand> calcStrands = rightStrandOrderMap.get(maxCalcOrder);
				
		List<CorporateRightStrand> orphanedRestrictions = findOrphanedRestrictions(calcStrands); 
        
        if (orphanedRestrictions.size() > 0){
        	calcStrands.removeIf(rs -> orphanedRestrictions.contains(rs));
        }
        
		return calcStrands;
	}

	private List<CorporateRightStrand> findOrphanedRestrictions(
		Collection<CorporateRightStrand> corpStrands
	) {
		List<CorporateRightStrand> distributionStrands = corpStrands.stream()
    		.filter(rs -> rs.getIsDistribution())
    		.collect(Collectors.toCollection(ArrayList::new));
        List<TestRestrictionStrand> restrictionStrands = corpStrands.stream()
    		.filter(rs -> TestRestrictionStrand.class.isInstance(rs))
    		.map(rs -> (TestRestrictionStrand)rs)
    		.collect(Collectors.toCollection(ArrayList::new));
        
		List<CorporateRightStrand> orphanedRestrictions = new ArrayList<>();
		
		for (TestRestrictionStrand restriction : restrictionStrands) {
			/*
        	 * If the restriction is tied to a specific distribution right, and that distribution right does not exist, then remove the restriction
        	 */
			if (restriction.getParentRightSourceId() != null) {
        		boolean foundParent = CollectionsUtil.any(
        			distributionStrands, 
        			drs -> drs.getRightSource().getSourceId().equals(restriction.getParentRightSourceId())
        		);
        		
        		if (!foundParent) {
        			orphanedRestrictions.add(restriction);
        		}
        		else {
        			if (!distributionStrands.isEmpty()) {
        				orphanedRestrictions.add(restriction);
        			}
        		}
            }
        }
		
		return orphanedRestrictions;
	}
}
