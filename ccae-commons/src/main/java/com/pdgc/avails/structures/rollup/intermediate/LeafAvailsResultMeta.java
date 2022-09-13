package com.pdgc.avails.structures.rollup.intermediate;

import java.io.Serializable;
import java.util.Map.Entry;

import com.google.common.base.Equivalence;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

public class LeafAvailsResultMeta implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public EquivalenceMap<RightStrand, AvailabilityResult> rightStrandImpacts;
	public AvailabilityResultStruct availabilities;

	public LeafAvailsResultMeta(
		Equivalence<? super RightStrand> rightStrandEquivalence,
		AvailabilityResultStruct availabilities
	) {
		this.rightStrandImpacts = new EquivalenceMap<>(rightStrandEquivalence);
		this.availabilities = availabilities;
	}

	public LeafAvailsResultMeta(LeafAvailsResultMeta a2) {
		rightStrandImpacts = new EquivalenceMap<>(a2.rightStrandImpacts.getEquivalence());
		for (Entry<RightStrand, AvailabilityResult> entry : a2.rightStrandImpacts.entrySet()) {
			rightStrandImpacts.put(entry.getKey(), entry.getValue());
		}

		availabilities = new AvailabilityResultStruct(a2.availabilities);
	}
	
	public void addRightStrandImpact(
		RightStrand rightStrand,
		AvailabilityResult availabilityImpact
	) {
		if (rightStrandImpacts.containsKey(rightStrand)) {
			rightStrandImpacts.put(
				rightStrand, 
				AvailabilityResult.combine(rightStrandImpacts.get(rightStrand), availabilityImpact)
			);
		} else {
			rightStrandImpacts.put(rightStrand, availabilityImpact);
		}
	}
	
	public void mergeWithResultMeta(LeafAvailsResultMeta a2) {
		availabilities = AvailabilityResultStruct.combine(availabilities, a2.availabilities);
		for (Entry<RightStrand, AvailabilityResult> rightStrandImpact : a2.rightStrandImpacts.entrySet()) {
			addRightStrandImpact(rightStrandImpact.getKey(), rightStrandImpact.getValue());
		}
	}
	
	public String toString() {
		return availabilities.toString();
	}
}
