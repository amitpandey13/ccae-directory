package com.pdgc.general.structures;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FoxSalesWindow implements RightStrandSource, Serializable {

	private static final long serialVersionUID = 1L;
    
	private Long salesWindowId;
	private String name;
	private String shortName;
	private Long customerId;
	private Long businessUnitId;
	private SalesWindowLifecycle lifecycle;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return salesWindowId.equals(((FoxSalesWindow)obj).salesWindowId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(salesWindowId);
	}
	
	@Override
	public String getDisplaySource() {
		return "";
	}
	
	@Override
	public String getDisplaySourceType() {
		return "Sales Window";
	}

	@Override
	public Long getReservationTypeId() {
		return 0L;
	}
}
