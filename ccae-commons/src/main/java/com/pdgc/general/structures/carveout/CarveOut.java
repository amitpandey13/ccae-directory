package com.pdgc.general.structures.carveout;

import java.io.Serializable;
import java.time.LocalDate;

import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.CarveOutResult;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.timeperiod.TimePeriod;

import lombok.Getter;
import lombok.Setter;

/**
 * A carveOut works much like an override. If a conflict can fit the parameters that the
 * carveOut has specified, then no conflict will be generated due to it being "carvedOut" for
 * that conflicting license
 * 
 * @author Vishal Raut
 */
@Getter
@Setter
public abstract class CarveOut implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long carveOutId;
	
	private Object carveOutType;
	
	private Term carveOutTerm;
	private TimePeriod carveOutTimePeriod;
	
	private String carveOutComment;
	
	private CarveOutImpactType carveOutImpactType;
	private CarveOutCombineRule carveOutCombineRule;
	private Integer carveOutOrder;
	
	private RightStrandCarveOutAction carveOutAction;

	// For Grouping purposes
	private Integer carveOutGroupId;
	private CarveOutCombineRule carveOutGroupCombineRule;
	private Integer carveOutGroupOrder;
	
	protected CarveOut(
		Long carveOutId,
		Object carveOutType,
		Term origTerm,
		TimePeriod timePeriod,
		String carveOutComment, 
		CarveOutImpactType carveOutImpactType,
		CarveOutCombineRule carveOutCombineRule,
		Integer carveOutOrder, 
		Integer carveOutGroupId,
		CarveOutCombineRule carveOutGroupCombineRule,
		Integer carveOutGroupOrder
	) {
		this.carveOutId = carveOutId;
		this.carveOutType = carveOutType;
		this.carveOutTerm = origTerm;
		if (timePeriod != null) {
			this.carveOutTimePeriod = timePeriod;
		}
		else {
			this.carveOutTimePeriod = TimePeriod.FULL_WEEK;
		}

		this.carveOutComment = carveOutComment;
		
		this.carveOutImpactType = carveOutImpactType;
		this.carveOutCombineRule = carveOutCombineRule;
		this.carveOutOrder = carveOutOrder;
		
		// Group specifics
		this.carveOutGroupId = carveOutGroupId;
		this.carveOutGroupCombineRule = carveOutGroupCombineRule;
		this.carveOutGroupOrder = carveOutGroupOrder;
	}
	
	@Override
	public String toString() {
		return "CarveOut [carveOutId=" + carveOutId + ", carveOutType=" + carveOutType + ", carveOutTerm="
				+ carveOutTerm + ", carveOutComment=" + carveOutComment + ", carveOutImpactType=" + carveOutImpactType
				+ ", carveOutCombineRule=" + carveOutCombineRule + ", carveOutOrder=" + carveOutOrder
				+ ", carveOutAction=" + carveOutAction + "]";
	}

	/**
	 * Analyzes the carveout and get its impact to a new license with the given parameters
	 * @param request
	 * @return
	 */
    public abstract CarveOutResult getCarveOutImpact(
        CarveOutImpactRequest request
    );
	
    public Long getCarveOutId() {
		return carveOutId;
	}

	public Object getCarveOutType() {
		return carveOutType;
	}

	public Term getOrigTerm() {
		return carveOutTerm;
	}

	public LocalDate getOrigStartDate() {
		return carveOutTerm.getStartDate();
	}

	public LocalDate getOrigEndDate() {
		return carveOutTerm.getEndDate();
	}

	public TimePeriod getTimePeriod() {
		return carveOutTimePeriod;
	}

	public String getCarveOutComment() {
		return carveOutComment;
	}
	
	public RightStrandCarveOutAction getCarveOutAction() {
		return carveOutAction;
	}

	public CarveOutImpactType getCarveOutImpactType() {
		return carveOutImpactType;
	}

	public CarveOutCombineRule getCarveOutCombineRule() {
		return carveOutCombineRule;
	}

	public Integer getCarveOutOrder() {
		return carveOutOrder;
	}

	public void setCarveOutAction(RightStrandCarveOutAction carveOutAction) {
		this.carveOutAction = carveOutAction;
	}

	public Integer getCarveOutGroupId() {
		return carveOutGroupId;
	}

	public CarveOutCombineRule getCarveOutGroupCombineRule() {
		return carveOutGroupCombineRule;
	}

	public Integer getCarveOutGroupOrder() {
		return carveOutGroupOrder;
	}

}
