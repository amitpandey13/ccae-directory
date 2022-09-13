package com.pdgc.db.structures;

import java.util.Map;

/**
 * Container for query with parameter values. 
 * 
 * @author CLARA HONG
 *
 */
public class QueryParameters {

	private String query; 
	private Map<String, Object> parameters; 
	
	public QueryParameters(String query, Map<String, Object> parameters) {
		this.query = query; 
		this.parameters = parameters; 
	}

	public String getQuery() {
		return query;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "QueryParameters [query=" + query + ", \r\nparameters=" + parameters + "]";
	}

}
