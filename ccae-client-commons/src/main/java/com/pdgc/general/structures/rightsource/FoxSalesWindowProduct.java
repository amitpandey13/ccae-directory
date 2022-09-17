package com.pdgc.general.structures.rightsource;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * corresponds to the saleswindowproduct table
 * @author Linda Xu
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoxSalesWindowProduct implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
	private Long salesWindowId;
	private Long productId;
	private Integer windowStatusId;
	private String windowStatusDescription;
	private String statusId;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate outsideDate;
	private LocalDate trackingDate;
	private String windowScheduleStatus;
	private String windowScheduleTag;
	private String retiredFlag;
	private String availabilityFlag;
	private String notes;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(salesWindowId, ((FoxSalesWindowProduct)obj).salesWindowId)
			&& Objects.equals(productId, ((FoxSalesWindowProduct)obj).productId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(salesWindowId)
			^ Objects.hashCode(productId);
	}
}
