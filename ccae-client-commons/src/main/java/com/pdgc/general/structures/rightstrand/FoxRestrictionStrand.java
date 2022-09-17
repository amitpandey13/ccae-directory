package com.pdgc.general.structures.rightstrand;

import java.util.Objects;

import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface FoxRestrictionStrand extends CorporateRightStrand, FoxRightStrand {

	@Override
	public default boolean getIsDistribution() {
		return false;
	}
	
	public Long getParentRightSourceId();
	
	public static boolean additionalEquals(FoxRestrictionStrand rs1, FoxRestrictionStrand rs2) {
		return FoxRightStrand.additionalEquals(rs1, rs2)
			&& Objects.equals(rs1.getParentRightSourceId(), rs2.getParentRightSourceId())
		;
	}
	
	@Override
	public default boolean additionalGroupingEquals(RightStrand obj) {
		if (!(obj instanceof FoxRestrictionStrand)) {
			return false;
		}
		
		return additionalEquals(this, (FoxRestrictionStrand)obj);
	}
}
