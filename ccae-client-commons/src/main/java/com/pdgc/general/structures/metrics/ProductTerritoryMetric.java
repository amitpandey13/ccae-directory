package com.pdgc.general.structures.metrics;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Container class for various performance metrics per product/territory
 * 
 * @author CLARA HONG
 *
 */
public class ProductTerritoryMetric implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Map<String, TerritoryMetric> metrics;
	
	public ProductTerritoryMetric() {
		metrics = new HashMap<String, TerritoryMetric>();
	}
	
	public ProductTerritoryMetric(Map<String, TerritoryMetric> metrics) {
		this.metrics = metrics;  
	}
	
	public TerritoryMetric getMetric(String typeId) {
		return metrics.get(typeId); 
	}

	public void addMetric(TerritoryMetric metric) {
		metrics.put(metric.getMetricTypeId(), metric);
	}
	
	public Collection<TerritoryMetric> getAllMetrics() {
		return metrics.values();
	}
	
	@Override
	public String toString() {
		return metrics.values().toString(); 
	}
}
