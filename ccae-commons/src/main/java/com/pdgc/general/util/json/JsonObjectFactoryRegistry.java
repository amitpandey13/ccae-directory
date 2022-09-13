package com.pdgc.general.util.json;

public class JsonObjectFactoryRegistry {
	
	private static JsonObjectFactory sJsonObjectFactory;

	public static JsonObjectFactory getJsonObjecttFactory() {
		if (sJsonObjectFactory == null) {
			sJsonObjectFactory = new JsonOrgJsonObjectFactory();
		}
		return sJsonObjectFactory;
	}
	

}
