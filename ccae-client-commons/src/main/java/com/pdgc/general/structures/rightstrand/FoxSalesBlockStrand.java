package com.pdgc.general.structures.rightstrand;

import java.util.Objects;

import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.SalesPlanRightStrand;

/**
 * sales block strand
 * @author Linda Xu
 *
 */
public interface FoxSalesBlockStrand extends FoxRightStrand, SalesPlanRightStrand {

	public static boolean additionalEquals(FoxSalesBlockStrand rs1, FoxSalesBlockStrand rs2) {
		return FoxRightStrand.additionalEquals(rs1, rs2)
			&& Objects.equals(rs1.getSalesWindowId(), rs1.getSalesWindowId())
			&& Objects.equals(rs1.getCustomer(), rs2.getCustomer())
		;
	}
	
	@Override
	public default boolean additionalGroupingEquals(RightStrand obj) {
		if (!(obj instanceof FoxSalesBlockStrand)) {
			return false;
		}
		
		return additionalEquals(this, (FoxSalesBlockStrand)obj);
	}
}
