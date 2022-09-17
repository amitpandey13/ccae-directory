package com.pdgc.ccae.dao.intermediateobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Sales Window Product entry populated from database row
 */
@AllArgsConstructor
@Getter
public class ProductSalesWindowEntry implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long salesWindowId;
	private Long windowStatusId;
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
	private String rightStatus;
	private String licenseInfoCodes;
    
}
