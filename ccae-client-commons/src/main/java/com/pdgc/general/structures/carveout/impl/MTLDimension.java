package com.pdgc.general.structures.carveout.impl;

/**
 * Enum that is connected to CustomerCountCarveOut but needed methods to determine
 * what kind of dimension by character
 * 
 * @author thomas
 *
 */
public enum MTLDimension {
	
	MEDIA('m'), TERRITORY('t'), LANGUAGE('l');
	
	private final char id;
	
	private MTLDimension(char c) {
		this.id = c;
	}
	
	public char getMTLDimensionCharacter() {
		return this.id;
	}
	
	public static MTLDimension byId(char id) {
		for (MTLDimension c : MTLDimension.values()) {
			if (c.getMTLDimensionCharacter() == Character.toLowerCase(id)) {
				return c;
			}
		}
		
		return null;
	}
}
