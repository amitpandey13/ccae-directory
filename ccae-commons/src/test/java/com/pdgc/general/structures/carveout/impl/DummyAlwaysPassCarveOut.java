package com.pdgc.general.structures.carveout.impl;

import java.util.HashMap;
import java.util.Map;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class DummyAlwaysPassCarveOut extends CarveOut {

	private static final long serialVersionUID = 1L;

	public DummyAlwaysPassCarveOut(
		Long carveOutId,
		Object carveOutType,
		Term origTerm,
		TimePeriod timePeriod,
		String carveOutComment,
		CarveOutImpactType impactType, 
		CarveOutCombineRule combineRule,
		Long carveOutOrder
	) {
    	 super(carveOutId, carveOutType, origTerm, timePeriod, carveOutComment, impactType, combineRule, carveOutOrder.intValue(), 1, CarveOutCombineRule.AND, 1);
    }

    @Override
    public CarveOutResult getCarveOutImpact(
        CarveOutImpactRequest request
    ) {
    	RightStrandCarveOutAction gapAction = super.getCarveOutImpactType() == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.IGNORE_RIGHT_STRAND : RightStrandCarveOutAction.APPLY_RIGHT_STRAND;
		Term relevantTerm = request.term != null ? Term.getIntersectionTerm(request.term, super.getCarveOutTerm()) : super.getCarveOutTerm();
        TimePeriod relevantPeriod = request.timePeriod != null ? TimePeriod.intersectPeriods(request.timePeriod, super.getCarveOutTimePeriod()) : super.getCarveOutTimePeriod();

        if (relevantTerm == null || relevantPeriod.isEmpty()) {
        	return new CarveOutResult(new HashMap<>(), gapAction);
        }
        
        RightStrandCarveOutAction rsAction = super.getCarveOutImpactType() == CarveOutImpactType.ONLY_AGAINST ? RightStrandCarveOutAction.APPLY_RIGHT_STRAND : RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND;

        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> carveOutImpact = new HashMap<>();
        carveOutImpact.put(relevantTerm, new HashMap<>());
        carveOutImpact.get(relevantTerm).put(relevantPeriod, rsAction);
        
        return new CarveOutResult(carveOutImpact, gapAction);
    }
}
