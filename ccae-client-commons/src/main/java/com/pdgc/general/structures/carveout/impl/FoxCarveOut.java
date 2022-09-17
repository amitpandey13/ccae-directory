package com.pdgc.general.structures.carveout.impl;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.CarveOut;
import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;
import com.pdgc.general.structures.carveout.grouping.CarveOutCombineRule;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.structures.timeperiod.TimePeriodPart;

import lombok.Getter;
import lombok.Setter;

/**
 * Fox specific structure for carveouts.
 * 
 * @author thomas
 * @see CarveOut
 */
@Getter
@Setter
@SuppressWarnings("PMD.AbstractNaming")
public abstract class FoxCarveOut extends CarveOut {

	private static final long serialVersionUID = 1L;
	
	private Collection<TimePeriodPart> timePeriodParts; //Stored for the sole purpose of making pretty strings...?
	private String carveOutString;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	protected FoxCarveOut(
		Long carveOutId, 
		FoxCarveOutType carveOutType, // TODO: This doesn't do anything at the moment. Was intended to give different flavors to carveOuts but not implemented
		Term origTerm, 
		Collection<TimePeriodPart> timePeriodParts, 
		String carveOutComment, 
		CarveOutImpactType carveOutImpactType,
		CarveOutCombineRule carveOutCombineRule,
		Integer carveOutOrder, 
		Integer carveOutGroupId,
		CarveOutCombineRule carveOutGroupCombineRule,
		Integer carveOutGroupOrder
	) {
		super(carveOutId, carveOutType, origTerm, TimePeriod.FULL_WEEK, carveOutComment, carveOutImpactType, carveOutCombineRule, carveOutOrder, carveOutGroupId, carveOutGroupCombineRule, carveOutGroupOrder);
		
		if (timePeriodParts != null) {
			this.timePeriodParts = ImmutableSet.copyOf(timePeriodParts);
			super.setCarveOutTimePeriod(new TimePeriod(this.timePeriodParts));
		}
		if (StringUtils.isNotBlank(carveOutComment) && !carveOutComment.contains("Only Against Fox Owned:")
				&& !carveOutComment.contains("Allow Fox Owned:")) {
			StringBuilder carveOutCommentBuilder = new StringBuilder();
			carveOutCommentBuilder.append(FoxCarveOutHelper.getCarveOutCommentLabel(carveOutType, carveOutImpactType));
			carveOutCommentBuilder.append(": ");
			carveOutCommentBuilder.append(carveOutComment);
			super.setCarveOutComment(carveOutCommentBuilder.toString());
		}
	}
	
	@Override
	public String toString() {
		if (carveOutString != null) {
			return carveOutString;
		}
		StringBuilder carveOutString = new StringBuilder("General Carve-Out ");
		if (!StringUtils.isBlank(super.getCarveOutComment())) {
			carveOutString.append('\"').append(super.getCarveOutComment()).append('\"');
		}
		return carveOutString.toString();
	}
	
	@Override
	public FoxCarveOutType getCarveOutType() {
		return (FoxCarveOutType) super.getCarveOutType();
	}
	
	public Collection<TimePeriodPart> getTimePeriodParts() {
		return timePeriodParts;
	}
}
