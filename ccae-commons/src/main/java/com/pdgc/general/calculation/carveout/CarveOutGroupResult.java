package com.pdgc.general.calculation.carveout;

import java.util.Map;

import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class CarveOutGroupResult {

	public Map<Term, Map<TimePeriod, CarveOutGroupAction>> impactMap;
    public RightStrandCarveOutAction gapAction; 

    public CarveOutGroupResult(
    	Map<Term, Map<TimePeriod, CarveOutGroupAction>> impactMap, 
        RightStrandCarveOutAction gapAction
    ) {
        this.impactMap = impactMap;
        this.gapAction = gapAction;
    }
}
