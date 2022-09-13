package com.pdgc.general.structures.classificationEnums;

import java.util.HashMap;
import java.util.Map;

public enum ProductLevel {
	FEATURE(4), SERIES(1), SEASON(2), EPISODE(3), EPISODE_PART(35), OTHER(9);
	
	private final int id;
	
	private static Map<Integer, ProductLevel> productLevels = new HashMap<>();

	 static {
	        for (ProductLevel productLevel : ProductLevel.values()) {
	        	productLevels.put(productLevel.getValue(), productLevel);
	        }
	    }	
	
	 public static ProductLevel valueOf(int levelId) {
		 return productLevels.get(levelId);
	 }	 
	 
	ProductLevel(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}