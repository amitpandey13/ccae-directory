package com.pdgc.conflictcheck.structures.auth;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2TokenRequest {

	private String tokenRequestUrl;
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;

	/**
	 * Token request is said to be valid if it atleast contains values for tokenRequestUrl, grantType,
	 * clientId, clientSecret
	 * 
	 * @return
	 */
	public boolean isValid() {
		return StringUtils.isNoneBlank(tokenRequestUrl, grantType, clientId, clientSecret);
	}

}
