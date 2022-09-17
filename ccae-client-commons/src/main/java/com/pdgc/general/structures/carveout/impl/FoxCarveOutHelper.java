package com.pdgc.general.structures.carveout.impl;

import com.pdgc.general.structures.carveout.attributes.CarveOutImpactType;
import com.pdgc.general.structures.carveout.attributes.FoxCarveOutType;

/**
*
* @author Anant Singh
*/

public final class FoxCarveOutHelper {

	private FoxCarveOutHelper() {}

	public static String getCarveOutCommentLabel(FoxCarveOutType carveOutType, CarveOutImpactType carveOutImpactType) {
		switch (carveOutType) {
		case CUSTOMERS:
			return carveOutImpactType.toString();
		case CUSTOMER_TYPES:
			return carveOutImpactType + " Types";
		case CUSTOMER_GENRES:
			return carveOutImpactType + " Genres";
		case MAX_CUSTOMERS:
			return "Customer Limit";
		default:
			return "General";
		}
	}
}
