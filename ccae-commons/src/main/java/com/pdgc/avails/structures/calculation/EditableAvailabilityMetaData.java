package com.pdgc.avails.structures.calculation;

import java.util.Map.Entry;

import com.google.common.base.Equivalence;
import com.pdgc.avails.helpers.AvailabilityHelper;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class EditableAvailabilityMetaData extends AvailabilityMetaData {

	private static final long serialVersionUID = 1L;
	
	public EditableAvailabilityMetaData(Equivalence<? super RightStrand> rightStrandEquivalence) {
		super(rightStrandEquivalence);
	}

	public EditableAvailabilityMetaData(AvailabilityMetaData a2) {
		super(a2);
	}

	/**
	 * combines the information from two AvailabilityMeta structures together.
	 * Direction does not matter, so long as left remains not-null. availability
	 * combines according to the Availability class's combine rules. usesTimePeriods
	 * is OR'd together. blockingPeriods are appended together in a list, since they
	 * will be analyzed and compressed later rightStrands are also appended together
	 * in a list
	 * 
	 * @param left
	 * @param right
	 * @return
	 * @throws IELeftSideAvailabilityIsMissingException
	 */
	public static EditableAvailabilityMetaData combineAvailabilityMeta(
		AvailabilityMetaData left,
		AvailabilityMetaData right
	) {
		EditableAvailabilityMetaData newAvailabilityMeta = new EditableAvailabilityMetaData(left);

		if (right != null) {
			newAvailabilityMeta.addNewAvailabilityResult(right.availabilityResult);
			
			for (Entry<RightStrand, AvailabilityResult> rightStrandImpact : right.getRightStrandImpacts().entrySet()) {
				newAvailabilityMeta.addRightStrandImpact(rightStrandImpact.getKey(), rightStrandImpact.getValue());
			}
		}

		return newAvailabilityMeta;
	}

	public void addNewAvailabilityResult(AvailabilityResult newAvailabilityResult) {
		availabilityResult = AvailabilityResult.combine(availabilityResult, newAvailabilityResult);
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
		}
		else {
			rightStrandImpacts.put(rightStrand, availabilityImpact);
		}
		
		//Only merge the availabilities, not the record details
		availabilityResult = new AvailabilityResult(
		    AvailabilityHelper.combineAvailability(availabilityResult.availability, availabilityImpact.availability),
		    availabilityResult.resultDetails
		);
	}
}
