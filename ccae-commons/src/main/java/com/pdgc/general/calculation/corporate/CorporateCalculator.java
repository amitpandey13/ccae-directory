package com.pdgc.general.calculation.corporate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

/**
 * How each client wants to interpret their corporate availabilities may differ, 
 * and this module must be shared between conflicts/avails, so this must be left abstract
 * Ex: One client might impose right strand ordering in which the answer is the availability of the last strand, 
 * while another might not have any concept of order and will use the least-available answer.
 * There might also be various exception cases that are handled differently by each client
 */
public abstract class CorporateCalculator {

    public abstract boolean needsToGoThroughCalculator(RightStrand rightStrand);
    
    public abstract AvailsCorporateCalculatorResult calculateForAvails(
        CorporateCalculatorParams params
    );
    
    public abstract ConflictCorporateCalculatorResult calculateForConflictCheck(
        CorporateCalculatorParams params
    );
	
    protected <E extends RightStrand> Map<TermPeriod, Set<E>> sortRightStrands(
        Iterable<E> rightStrands,
        Term requestedTerm,
        TimePeriod requestedTimePeriod
    ) {
        Map<TermPeriod, Set<E>> strandTermPeriodMap = new HashMap<>();
        
        //Initialize with the request termperiod
        TermPeriod requestedTermPeriod = new TermPeriod(requestedTerm, requestedTimePeriod);
        strandTermPeriodMap.put(requestedTermPeriod, new HashSet<>());
        
        for (E rs : rightStrands) {
            Set<E> strandsWithSameTermPeriod = strandTermPeriodMap.get(rs.getTermPeriod());
            if (strandsWithSameTermPeriod == null) {
                strandsWithSameTermPeriod = new HashSet<>();
                strandTermPeriodMap.put(rs.getTermPeriod(), strandsWithSameTermPeriod);
            }
            strandsWithSameTermPeriod.add(rs);
        }
        
        Map<TermPeriod, Set<TermPeriod>> termPeriodMap = DateTimeUtil.createCutTermPeriodMappings(
            DateTimeUtil.createTermPeriodMap(strandTermPeriodMap.keySet()),
            requestedTerm,
            requestedTimePeriod
        );
        
        Map<TermPeriod, Set<E>> sortedStrands = new HashMap<>();
        for (Entry<TermPeriod, Set<TermPeriod>> newToSourceEntry : termPeriodMap.entrySet()) {
            Set<E> sourceStrands = new HashSet<>();
            for (TermPeriod sourceTermPeriod : newToSourceEntry.getValue()) {
                sourceStrands.addAll(strandTermPeriodMap.get(sourceTermPeriod));
            }
            sortedStrands.put(newToSourceEntry.getKey(), sourceStrands);
        }
        
        return sortedStrands;
    }
    
	protected <E extends CorporateRightStrand> Map<Integer, Collection<E>> sortRightStrandsByCalculationOrder(
		Iterable<E> corpStrands
	) {
		Map<Integer, Collection<E>> rightStrandOrderMap = new HashMap<>();
		for (E corpStrand : corpStrands) {
			Collection<E> rightStrandsOfSameOrder = rightStrandOrderMap.get(corpStrand.getCalculationOrder());
			if (rightStrandsOfSameOrder == null) {
				rightStrandsOfSameOrder = new ArrayList<>();
				rightStrandOrderMap.put(corpStrand.getCalculationOrder(), rightStrandsOfSameOrder);
			}
			rightStrandsOfSameOrder.add(corpStrand);
		}
		
		return rightStrandOrderMap;
	}
}
