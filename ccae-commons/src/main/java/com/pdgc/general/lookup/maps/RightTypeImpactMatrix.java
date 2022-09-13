package com.pdgc.general.lookup.maps;

import java.util.Map;

import org.javatuples.Pair;

import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.classificationEnums.RightTypeType;

public class RightTypeImpactMatrix {

	/**
	 * 1st Long = RequestedRightType
	 * 2nd Long = ExistingRightType
	 * Availability = ExistingRightType's impact on the Availability of RequestedRightType
	 */
	private Map<Pair<Long, Long>, Availability> matrix;
	
	
	public RightTypeImpactMatrix(Map<Pair<Long, Long>, Availability> matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * returns whether {@code impactingType} affects the availability of
	 * {@code request} right type
	 * 
	 * @param request
	 * @param impactingType
	 * @param rightTypeImpactMatrix
	 * @return
	 */
	public boolean impactsAvailability(RightType request, RightType impactingType) {
		return getAvailabilityImpact(request, impactingType) != Availability.UNSET;
	}
	
	/**
	 * Get the availability impact of {@code impactingType} on {@code request}
	 * type.
	 * 
	 * @param request
	 * @param impactingType
	 * @param rightTypeImpactMatrix
	 * @return
	 */
	public Availability getAvailabilityImpact(RightType request, RightType impactingType) {
		if (request.allowsEpisodeLimit()) {
			// A Catchup Block's ep limit MUST be greater than a Catchup grant in order to be available.
			// We check for this case here and bypass consulting the matrix by overriding its availability to UNSET (to default to YES).
			// Otherwise, if the block limit <= grant limit, we proceed as normal and allow the impact matrix to set to NO. 
			if (request.getRightTypeType() == RightTypeType.HOLDBACK) {		
				// Request is Catchup block 
				if (impactingType.allowsEpisodeLimit() && impactingType.getEpisodeLimit() < request.getEpisodeLimit()) {
					return Availability.UNSET;
				}
			} else {
				// Request is Catchup grant 
                if (impactingType.allowsEpisodeLimit() && request.getEpisodeLimit() < impactingType.getEpisodeLimit()) {
                    return Availability.UNSET;
                }
            }
		}

		Pair<Long, Long> rightsTuple = new Pair<Long, Long>(request.getRightTypeId(),
				impactingType.getRightTypeId());
		if (!matrix.containsKey(rightsTuple)) {
			return Availability.UNSET;
		}

		return matrix.get(rightsTuple);
	}
	
	public void setAvailability(Long request, Long impactingType, Long availability) {
		matrix.put(new Pair<Long, Long>(request, impactingType), Availability.byValue((int)(long)availability)); 
	}
	
	
	public RightTypeImpactMatrix getDeepCopy() {
		RightTypeImpactMatrix copiedMatrix = new RightTypeImpactMatrix(matrix); 
		return copiedMatrix; 
	}
}
