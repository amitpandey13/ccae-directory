package com.pdgc.general.structures.rightsource.impl;

import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;
import com.pdgc.general.structures.rightsource.FoxRightSource;

/**
 * FoxSalesPlanSource
 */
public class FoxSalesPlanSource extends FoxRightSource {

private static final long serialVersionUID = 1L;
	
	public FoxSalesPlanSource(
		FoxRightSourceType rightSourceType,
		Long sourceId,
		String sourceDetailId,
		FoxBusinessUnit businessUnit
	) {
		super(rightSourceType, sourceId, sourceDetailId, businessUnit);
	}
}
