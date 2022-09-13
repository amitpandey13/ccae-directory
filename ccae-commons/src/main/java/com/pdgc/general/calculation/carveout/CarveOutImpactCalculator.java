package com.pdgc.general.calculation.carveout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pdgc.general.lookup.maps.RightTypeCarveOutActionMap;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;

public class CarveOutImpactCalculator {

	private RightTypeCarveOutActionMap rightTypeCarveOutActionMap;
	
	public CarveOutImpactCalculator(
		RightTypeCarveOutActionMap rightTypeCarveOutActionMap
	) {
		this.rightTypeCarveOutActionMap = rightTypeCarveOutActionMap;
	}
	
	public Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> getCarveOutImpact(
		CarveOutImpactRequest left,
		CarveOutImpactRequest right
	) {
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> leftCarveOutImpact;
        if (left.carveOutContainer != null && !CollectionsUtil.isNullOrEmpty(left.carveOutContainer.getAllCarveOuts())) {
        	leftCarveOutImpact = left.carveOutContainer.getCarveOutImpact(right);
        }
        else {
        	leftCarveOutImpact = new HashMap<>();
	    	Term intersectionTerm = Term.getIntersectionTerm(left.term, right.term);
	        TimePeriod intersectionPeriod = TimePeriod.intersectPeriods(left.timePeriod, right.timePeriod);
	
			if (intersectionTerm != null && !intersectionPeriod.isEmpty()) {
				Map<TimePeriod, RightStrandCarveOutAction> termImpactMap = new HashMap<>();
				termImpactMap.put(intersectionPeriod, RightStrandCarveOutAction.APPLY_RIGHT_STRAND);
				leftCarveOutImpact.put(intersectionTerm, termImpactMap);
			}
        }
        
        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> rightCarveOutImpact;
        if (right.carveOutContainer != null && !CollectionsUtil.isNullOrEmpty(right.carveOutContainer.getAllCarveOuts())) {
        	rightCarveOutImpact = right.carveOutContainer.getCarveOutImpact(left);          	
        }
        else {
        	rightCarveOutImpact = new HashMap<>();
    
        	Term intersectionTerm = Term.getIntersectionTerm(left.term, right.term);
            TimePeriod intersectionPeriod = TimePeriod.intersectPeriods(left.timePeriod, right.timePeriod);

            if (intersectionTerm != null && !intersectionPeriod.isEmpty()) {
            	Map<TimePeriod, RightStrandCarveOutAction> termImpactMap = new HashMap<>();
	            termImpactMap.put(intersectionPeriod, RightStrandCarveOutAction.APPLY_RIGHT_STRAND);
	            rightCarveOutImpact.put(intersectionTerm, termImpactMap);       
            }
        }
	
        //populate rightStrandCarveOutImpact 
        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> rightStrandCarveOutImpact = mergeCarveOutImpactMaps(
        	left.rightType,
        	leftCarveOutImpact,
        	right.rightType,
        	rightCarveOutImpact
        );
        
        //Build a reversed map so that we can find adjacent terms and glue them together to reduce the number of date cuts
        rightStrandCarveOutImpact = DateTimeUtil.condenseTermPeriodValueMap(rightStrandCarveOutImpact);
        
        return rightStrandCarveOutImpact;   
	}
	
	private Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> mergeCarveOutImpactMaps(
		RightType leftRightType,
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> leftCarveOutImpact,
		RightType rightRightType,
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> rightCarveOutImpact
	) {
		Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> rightStrandCarveOutImpact = new HashMap<>();
		 
		for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : leftCarveOutImpact.entrySet()) {
			rightStrandCarveOutImpact.put(termEntry.getKey(), new HashMap<>(termEntry.getValue()));
		}

		Supplier<RightStrandCarveOutAction> defaultValueProducer = new Supplier<RightStrandCarveOutAction>() {
			@Override
			public RightStrandCarveOutAction get() {
				return RightStrandCarveOutAction.UNSET;
			}
		};
		
		Function<RightStrandCarveOutAction, RightStrandCarveOutAction> valueDeepCopy = new Function<RightStrandCarveOutAction, RightStrandCarveOutAction>() {
			@Override
			public RightStrandCarveOutAction apply(RightStrandCarveOutAction t) {
				return t;
			}
		};
		
		for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : rightCarveOutImpact.entrySet()) {
			for (Entry<TimePeriod, RightStrandCarveOutAction> periodEntry : termEntry.getValue().entrySet()) {
				Function<RightStrandCarveOutAction, RightStrandCarveOutAction> valueUpdater = new Function<RightStrandCarveOutAction, RightStrandCarveOutAction>() {
					@Override
					public RightStrandCarveOutAction apply(RightStrandCarveOutAction leftAction) {
						RightStrandCarveOutAction finalCarveOutAction;
						
						if (leftAction == RightStrandCarveOutAction.UNSET) {
							finalCarveOutAction = periodEntry.getValue();
						}
						else {
							RightStrandCarveOutAction revisedLeftAction = leftAction;
							RightStrandCarveOutAction revisedRightAction = periodEntry.getValue();
							
							if (rightTypeCarveOutActionMap.transferCarveOutIgnore(rightRightType, leftRightType)) {
								revisedLeftAction = getTransferredCarveOutAction(periodEntry.getValue(), leftAction);
							}

							if (rightTypeCarveOutActionMap.transferCarveOutIgnore(leftRightType, rightRightType)) {
								revisedRightAction = getTransferredCarveOutAction(leftAction, periodEntry.getValue());
							}
							
							finalCarveOutAction = getGreaterAction(revisedLeftAction, revisedRightAction);
						}
						
						if (finalCarveOutAction == RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND) {
							finalCarveOutAction = RightStrandCarveOutAction.IGNORE_RIGHT_STRAND;
						}
						
						return finalCarveOutAction;
					}
				};
				
				DateTimeUtil.updateTermPeriodValueMap(
					rightStrandCarveOutImpact, 
					termEntry.getKey(), 
					periodEntry.getKey(), 
					defaultValueProducer, 
					valueDeepCopy, 
					valueUpdater
				);
			}
		}

		return rightStrandCarveOutImpact;
	}

	private static RightStrandCarveOutAction getTransferredCarveOutAction(
		RightStrandCarveOutAction transferOwnerAction, 
		RightStrandCarveOutAction transferRecipientAction
    ) {
    	if (transferOwnerAction == RightStrandCarveOutAction.TRANSFERRABLE_IGNORE_RIGHT_STRAND) {
    		if (transferRecipientAction == RightStrandCarveOutAction.APPLY_RIGHT_STRAND || transferRecipientAction == RightStrandCarveOutAction.CONDITIONAL) {
    			return transferOwnerAction;
    		}
    		else {
    			return getGreaterAction(transferOwnerAction, transferRecipientAction);
    		}
    	}
		
    	if (transferOwnerAction == RightStrandCarveOutAction.CONDITIONAL) {
    		if (transferRecipientAction == RightStrandCarveOutAction.APPLY_RIGHT_STRAND) {
    			return transferOwnerAction;
    		}
    		else {
    			return getGreaterAction(transferOwnerAction, transferRecipientAction);
    		}
    	}
		
		return getGreaterAction(transferOwnerAction, transferRecipientAction);
    }

	private static RightStrandCarveOutAction getGreaterAction(RightStrandCarveOutAction left, RightStrandCarveOutAction right) {
		if (left.getValue() > right.getValue()) {
			return left;
		}
		else {
			return right;
		}
	}
}
