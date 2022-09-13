package com.pdgc.avails.structures.calculation;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Equivalence;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

/**
 * structure used by AvailsCalculation class to keep track of availability
 * information for any given PMTL+Term+RightType All fields are left public
 * because the object is constantly being modified
 * 
 * @author Vishal Raut
 */
public class AvailabilityMetaData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	//Store all relevant rights strands and their availability impacts
    protected EquivalenceMap<RightStrand, AvailabilityResult> rightStrandImpacts;
    protected AvailabilityResult availabilityResult;

	public AvailabilityMetaData(Equivalence<? super RightStrand> rightStrandEquivalence) {
		availabilityResult = new AvailabilityResult();
		rightStrandImpacts = new EquivalenceMap<>(rightStrandEquivalence);
	}
	
	public AvailabilityMetaData(
		EquivalenceMap<RightStrand, AvailabilityResult> rightStrandImpacts, 
		AvailabilityResult availabilityResult
	) {
		this.rightStrandImpacts = rightStrandImpacts;
		this.availabilityResult = availabilityResult;
	}
	
	public AvailabilityMetaData(AvailabilityMetaData a2) {
		rightStrandImpacts = new EquivalenceMap<>(a2.rightStrandImpacts);
		availabilityResult = new AvailabilityResult(a2.availabilityResult);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }

		if (!(obj instanceof AvailabilityMetaData)) {
			return false;
		}
		
        AvailabilityMetaData obj2 = (AvailabilityMetaData)obj;

        if (!availabilityResult.equals(obj2.availabilityResult)) {
        	return false;
        }

        if (rightStrandImpacts.size() == obj2.rightStrandImpacts.size()) {
            for(Entry<RightStrand, AvailabilityResult> rightStrandImpact : rightStrandImpacts.entrySet()) {
            	if (!obj2.rightStrandImpacts.containsKey(rightStrandImpact.getKey())) {
                	return false;
                }

                if (!rightStrandImpact.getValue().equals(obj2.rightStrandImpacts.get(rightStrandImpact.getKey()))) {
                    return false;
                }
            }
        }
        else {
            return false;
        }

        return true;
	}
	
	@Override
    public final int hashCode() {
        return availabilityResult.hashCode()
            ^ rightStrandImpacts.size();
    }

    public AvailabilityResult getAvailabilityResult() {
    	return availabilityResult;
    }
    
    public Map<RightStrand, AvailabilityResult> getRightStrandImpacts() {
    	return rightStrandImpacts.toMap();
    }
    
	@Override
	public String toString() {
		 StringBuilder displayString = new StringBuilder("Availability Periods:\n");
         displayString.append("\tAvailability:" + availabilityResult.availability);

         displayString.append("RightStrands: \n");
         for (Entry<RightStrand, AvailabilityResult> rightStrandImpact : rightStrandImpacts.entrySet()) {
             displayString.append("\tRightSource:" + rightStrandImpact.getKey().getRightSource().toString() + " Type:" + rightStrandImpact.getKey().getRightType().toString() + " Impact:" + rightStrandImpact.getValue().toString() + " Term: " + rightStrandImpact.getKey().getOrigTerm().toString() + "\n");
         }
         return displayString.toString();
	}
}