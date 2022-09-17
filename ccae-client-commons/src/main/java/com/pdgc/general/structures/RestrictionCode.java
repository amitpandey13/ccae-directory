package com.pdgc.general.structures;

/**
 * Fox's restriction categories 
 * 
 * @author Clara Hong
 *
 */
public enum RestrictionCode {
	NONE(140),
	MOT_INFORMATIONAL(141),
	LICENSING_INFORMATIONAL(142),
	GENERAL_INFORMATIONAL(143),
	CONTENT_OWNER(144);
	
	private int id; 
	
	private RestrictionCode(int id) {
		this.id = id; 
	}
	
	public static RestrictionCode byValue(int id) {
		for (RestrictionCode r : RestrictionCode.values()) {
			if (id == r.id) {
				return r; 
			}
		}
		throw new IllegalArgumentException(String.format("Invalid RestrictionCode: %d", id));
	}
	
}
