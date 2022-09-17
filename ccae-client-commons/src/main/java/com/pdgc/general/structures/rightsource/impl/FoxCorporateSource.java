package com.pdgc.general.structures.rightsource.impl;

import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.FoxRightSource;

/**
 * FoxCorporateSource
 */
public class FoxCorporateSource extends FoxRightSource {

	private static final long serialVersionUID = 1L;
	
	public FoxCorporateSource(
		FoxRightSourceType rightSourceType,
		Long sourceId,
		FoxBusinessUnit businessUnit
	) {
		super(rightSourceType, sourceId, null, businessUnit);
	}
}
