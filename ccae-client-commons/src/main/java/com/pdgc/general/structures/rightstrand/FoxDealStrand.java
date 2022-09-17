package com.pdgc.general.structures.rightstrand;

import java.util.Objects;

import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface FoxDealStrand extends DealRightStrand, FoxRightStrand {

	public Long getStatusId();
	
	public static boolean additionalEquals(FoxDealStrand rs1, FoxDealStrand rs2) {
		return FoxRightStrand.additionalEquals(rs1, rs2)
			&& Objects.equals(rs1.getCustomer(), rs2.getCustomer())
			&& Objects.equals(rs1.getCarveOuts(), rs2.getCarveOuts())
			&& Objects.equals(rs1.getStatusId(), rs2.getStatusId())
		;
	}
	
	@Override
	public default boolean additionalGroupingEquals(RightStrand obj) {
		if (!(obj instanceof FoxDealStrand)) {
			return false;
		}
		
		return additionalEquals(this, (FoxDealStrand)obj);
	}
}
