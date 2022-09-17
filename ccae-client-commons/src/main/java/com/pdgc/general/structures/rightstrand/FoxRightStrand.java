package com.pdgc.general.structures.rightstrand;

import java.util.Objects;

import com.pdgc.general.structures.rightsource.FoxRightSource;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface FoxRightStrand extends RightStrand {

	@Override
	public FoxRightSource getRightSource();
	
	public Long getProductHierarchyId();
	
	public Long getDistributionRightsOwner();
	
	public static boolean additionalEquals(FoxRightStrand rs1, FoxRightStrand rs2) {
		return Objects.equals(rs1.getProductHierarchyId(), rs2.getProductHierarchyId())
			&& Objects.equals(rs1.getDistributionRightsOwner(), rs2.getDistributionRightsOwner())
		;
	}
}
