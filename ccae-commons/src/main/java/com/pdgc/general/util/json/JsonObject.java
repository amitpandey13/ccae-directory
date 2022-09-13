package com.pdgc.general.util.json;

/**
 * Wrapper for actual implementation of the JSON Object. So that we can easily replace the json processing library.
 * 
 * @author Vishal Raut
 */
public interface JsonObject {

	Boolean getBoolean(String key);

	Integer getInt(String key);

	Long getLong(String key);

	Double getDouble(String key);

	String getString(String key);

	JsonObject getJsonObject(String key);

	JsonArray getJsonArray(String key);
	
	boolean has(String key);
	

}
