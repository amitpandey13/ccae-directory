package com.pdgc.general.calculation.carveout;

import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;

public class CarveOutGroupAction {
	private RightStrandCarveOutAction action;
	private Integer carveOutGroupId;
	private CarveOutCombineRule carveOutCombineRule;
	private Integer carveOutGroupOrder;
	
	public CarveOutGroupAction(
			Integer carveOutGroupId,
			CarveOutCombineRule carveOutCombineRule,
			Integer carveOutGroupOrder,
			RightStrandCarveOutAction action
			) {
		this.carveOutGroupId = carveOutGroupId;
		this.carveOutCombineRule = carveOutCombineRule;
		this.carveOutGroupOrder = carveOutGroupOrder;
		this.action = action;
	}

	public RightStrandCarveOutAction getAction() {
		return action;
	}

	public Integer getCarveOutGroupId() {
		return carveOutGroupId;
	}

	public CarveOutCombineRule getCarveOutCombineRule() {
		return carveOutCombineRule;
	}

	public Integer getCarveOutGroupOrder() {
		return carveOutGroupOrder;
	}
}
