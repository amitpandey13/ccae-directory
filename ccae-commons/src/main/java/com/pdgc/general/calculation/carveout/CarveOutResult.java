package com.pdgc.general.calculation.carveout;

import java.util.Map;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class CarveOutResult {

	public Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> impactMap;
    public RightStrandCarveOutAction gapAction; 

    public CarveOutResult(
    	Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> impactMap, 
        RightStrandCarveOutAction gapAction
    ) {
        this.impactMap = impactMap;
        this.gapAction = gapAction;
    }
}
