package com.pdgc.avails.structures.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;
import com.pdgc.general.util.CollectionsUtil;

public class RightRequest implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
	private RightType rightType;
	private CarveOutContainer carveOuts;
	private TimePeriod timePeriod;
    private Set<TimePeriodPart> timePeriodParts; //pretty much here solely for display purposes

	public RightRequest(
        RightType rightType, 
        CarveOutContainer carveOuts,
        Iterable<TimePeriodPart> timePeriodParts 
    ) {
		this.rightType = rightType;
		this.carveOuts = carveOuts;
		if (!CollectionsUtil.isNullOrEmpty(timePeriodParts)) {
		    this.timePeriodParts = ImmutableSet.of();
		    this.timePeriod = TimePeriod.FULL_WEEK;
		} else {
		    this.timePeriodParts = ImmutableSet.copyOf(timePeriodParts);
		    this.timePeriod = new TimePeriod(timePeriodParts);
		}
	}
	
	public RightRequest(
        RightType rightType, 
        CarveOutContainer carveOuts,
        TimePeriod timePeriod
	) {
	    this.rightType = rightType;
        this.carveOuts = carveOuts;
        this.timePeriodParts = ImmutableSet.of();
        this.timePeriod = timePeriod == null ? TimePeriod.FULL_WEEK : timePeriod;
	}

	public RightRequest(RightType rightType) {
		this(
		    rightType,
		    null,
		    (TimePeriod)null
		);
	}

	public RightType getRightType() {
		return rightType;
	}

	public CarveOutContainer getCarveOuts() {
		return carveOuts;
	}
	
	public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public Set<TimePeriodPart> getTimePeriodParts() {
        return timePeriodParts;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(rightType, ((RightRequest)obj).rightType) 
			&& Objects.equals(timePeriod, ((RightRequest)obj).timePeriod) 
			&& Objects.equals(carveOuts, ((RightRequest)obj).carveOuts);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(rightType) 
			^ Objects.hashCode(timePeriod) 
			^ Objects.hashCode(carveOuts);
	}

	@Override
	public String toString() {
		return "{RightType:" + rightType + ", CarveOuts:" + carveOuts + ", TimePeriod:" + timePeriod + "}";
	}

}
