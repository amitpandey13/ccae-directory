package com.pdgc.general.structures.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * Generic Performance Metric (e.g. Box Office, Local Admissions, etc) per product/territory
 * 
 * @author CLARA HONG
 *
 */
@AllArgsConstructor
@Getter
@Builder
public class TerritoryMetric implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long productId;
	private Long territoryId;
	private String metricTypeId;
	private String value;
	private Long localCurrencyValue;
	private String currencyCode;
	private String currencyDescription;
	private Long statusId;
	
	@Override
	public String toString() {
		return metricTypeId + ": " + value;
	}

}
