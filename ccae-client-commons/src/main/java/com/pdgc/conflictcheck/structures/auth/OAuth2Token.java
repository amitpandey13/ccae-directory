package com.pdgc.conflictcheck.structures.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * OAuth2Token object
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2Token {

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("instance_url")
	private String instanceUrl;
	
}
