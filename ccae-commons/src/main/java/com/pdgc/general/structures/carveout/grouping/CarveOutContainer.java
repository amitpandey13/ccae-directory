package com.pdgc.general.structures.carveout.grouping;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.timeperiod.TimePeriod;

/**
 * An object that contains all the carveOutGroups that are associated with
 * a single rightStrand.
 * 
 * This will contain the piece of logic that will calculate the relationship
 * between carveOutGroups. This can be things like <p>
 * 
 * Group 1 OR Group 2 AND Group 3
 * @author thomas
 *
 */
public abstract class CarveOutContainer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//term and time period of the carveout owner (ie. the right strand)
    protected Term parentTerm;
    protected TimePeriod parentTimePeriod;
    
    public CarveOutContainer(
		Term parentTerm, 
		TimePeriod parentTimePeriod
	) {
        this.parentTerm = parentTerm;
        this.parentTimePeriod = parentTimePeriod == null ? TimePeriod.FULL_WEEK : parentTimePeriod;
    }

    public abstract Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> getCarveOutImpact(
        CarveOutImpactRequest request
	);
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(this.getClass() != obj.getClass())) {
        	return false;
        }

        return parentTerm.equals(((CarveOutContainer)obj).parentTerm)
            && parentTimePeriod.equals(((CarveOutContainer)obj).parentTimePeriod);
    }

    @Override
    public int hashCode() {
        return parentTerm.hashCode()
            ^ parentTimePeriod.hashCode();
    }
    
    public Term getParentTerm() {
    	return parentTerm;
    }
    
    public TimePeriod getParentTimePeriod() {
    	return parentTimePeriod;
    }
    
    public abstract Set<CarveOut> getAllCarveOuts();
}

