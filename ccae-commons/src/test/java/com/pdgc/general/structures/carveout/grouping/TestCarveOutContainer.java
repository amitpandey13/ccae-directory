package com.pdgc.general.structures.carveout.grouping;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.DateTimeUtil;

public class TestCarveOutContainer extends CarveOutContainer {

	private static final long serialVersionUID = 1L;
	
	private CarveOut carveOut;
	
	public TestCarveOutContainer(
		Term parentTerm, 
		TimePeriod parentTimePeriod
	) {
		super(parentTerm, parentTimePeriod);
	}

	@Override
	public Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> getCarveOutImpact(CarveOutImpactRequest request) {
		CarveOutResult carveOutResult = carveOut.getCarveOutImpact(request);
		
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> impactMap = new HashMap<>();
		
		for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : impactMap.entrySet()) {
			Map<TimePeriod, RightStrandCarveOutAction> periodMap = new HashMap<>();
			for (Entry<TimePeriod, RightStrandCarveOutAction> periodEntry : termEntry.getValue().entrySet()) {
				if (periodEntry.getValue() == RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND) {
					periodMap.put(periodEntry.getKey(), RightStrandCarveOutAction.IGNORE_RIGHT_STRAND);
				}
				else {
					periodMap.put(periodEntry.getKey(), periodEntry.getValue());
				}
			}
		}
		
		//Find and fill the gaps of cleanedCarveOutTermsMap with overallGapAction
        {
        	for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : impactMap.entrySet()) {
        		TimePeriod gapPeriod = request.timePeriod;
        		for (TimePeriod period : termEntry.getValue().keySet()) {
        			gapPeriod = TimePeriod.subtractPeriods(gapPeriod, period);
        		}
        		
        		if (!gapPeriod.isEmpty()) {
        			termEntry.getValue().put(gapPeriod, carveOutResult.gapAction);
        		}
        	}
        	
        	Collection<Term> gapTerms = DateTimeUtil.findGapTerms(impactMap.keySet(), request.term);
        	for (Term gapTerm : gapTerms) {
        		Map<TimePeriod, RightStrandCarveOutAction> gapPeriodMap = new HashMap<>();
        		gapPeriodMap.put(request.timePeriod, carveOutResult.gapAction);
        		impactMap.put(gapTerm, gapPeriodMap);
        	}
        }
	
        return impactMap;
	}

	@Override
	public Set<CarveOut> getAllCarveOuts() {
		return Collections.singleton(carveOut);
	}
}
