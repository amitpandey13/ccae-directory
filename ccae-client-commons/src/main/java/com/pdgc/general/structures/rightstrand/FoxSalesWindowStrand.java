package com.pdgc.general.structures.rightstrand;

import java.util.Objects;

import com.pdgc.general.structures.FoxSalesWindow;
import com.pdgc.general.structures.SalesWindowLifecycle;
import com.pdgc.general.structures.rightsource.FoxSalesWindowProduct;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.SalesPlanRightStrand;

public interface FoxSalesWindowStrand extends FoxRightStrand, SalesPlanRightStrand {

	public FoxSalesWindow getSalesWindow();
	
	public FoxSalesWindowProduct getSalesWindowProduct();
	
	public default SalesWindowLifecycle getLifecycle() {
        return getSalesWindow().getLifecycle();
    }
	
	public static boolean additionalEquals(FoxSalesWindowStrand rs1, FoxSalesWindowStrand rs2) {
		return FoxRightStrand.additionalEquals(rs1, rs2)
			&& Objects.equals(rs1.getSalesWindowId(), rs1.getSalesWindowId())
			&& Objects.equals(rs1.getCustomer(), rs2.getCustomer())
			&& Objects.equals(rs1.getSalesWindow(), rs2.getSalesWindow())
			&& Objects.equals(rs1.getSalesWindowProduct(), rs2.getSalesWindowProduct())
		;
	}
	
	@Override
	public default boolean additionalGroupingEquals(RightStrand obj) {
		if (!(obj instanceof FoxSalesWindowStrand)) {
			return false;
		}
		
		return additionalEquals(this, (FoxSalesWindowStrand)obj);
	}
}
