package com.pdgc.general.util.json;

import java.util.Arrays;
import java.util.Collection;

public class JsonStringHelper {

	public JsonStringHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a bracket inclosed set of values that can be used as a json array.
	 * @param idArray
	 * @return
	 */
	public static String getJsonIdArray(Object[] idArray) {
		return Arrays.toString(idArray).replace('[', '{').replace(']', '}').replaceAll(" ", "");
	}
	
	public static String getJsonIdArray(Collection<? extends Object> idSet) {
		return getJsonIdArray(idSet.toArray());
	}
	
}
