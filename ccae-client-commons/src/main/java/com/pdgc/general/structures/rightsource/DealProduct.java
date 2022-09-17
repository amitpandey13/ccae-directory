package com.pdgc.general.structures.rightsource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DealProduct POJO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	private long dealProductId; // Fox titleListMapId
	private Long sourceId;
	private long businessUnitId;
	private Long productId;
	private Integer windowNumber;
	private String categoryDescription;
	private String subCategoryDescription;
	private String seriesCommitmentType; 
	private String seriesCommitmentYear; 
	private String seriesCommitmentAnnualIncrease; 
	private String seriesCommitmentComments; 
	private String allocationMedia;
	private String rightsTemplate;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean tentative;

	public DealProduct(long dealProductId) {
		this.dealProductId = dealProductId;
	}
}
