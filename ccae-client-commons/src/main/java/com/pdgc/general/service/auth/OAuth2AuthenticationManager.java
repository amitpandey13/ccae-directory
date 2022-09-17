package com.pdgc.general.service.auth;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdgc.conflictcheck.structures.auth.OAuth2Token;
import com.pdgc.conflictcheck.structures.auth.OAuth2TokenRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Authentication Manager for OAuth2 protocol.
 * 
 * @author Vishal Raut
 *
 */
@Slf4j
public class OAuth2AuthenticationManager
		implements IClientAuthenticationManager<OAuth2TokenRequest, OAuth2Token> {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public Optional<OAuth2Token> getToken(OAuth2TokenRequest tokenRequest) {
		//TODO Vishal Raut: Check if the token can be cached if it has an expiry
		try {
			Form form = Form.form()
					.add("grant_type", tokenRequest.getGrantType())
					.add("client_id", tokenRequest.getClientId())
					.add("client_secret", tokenRequest.getClientSecret());
			addOptionalValue(form, "username", tokenRequest.getUsername());
			addOptionalValue(form, "password", tokenRequest.getPassword());

			HttpResponse response = Request.Post(tokenRequest.getTokenRequestUrl()).bodyForm(form.build()).execute()
					.returnResponse();
			log.info("Received response: {}", response.getStatusLine());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String responseString = EntityUtils.toString(response.getEntity());
				log.debug("Received token data : {}", responseString);
				return Optional.of(MAPPER.readValue(responseString, OAuth2Token.class));
			}
		} catch (IOException e) {
			log.error("Error occured while fetching access token", e);
		}
		return Optional.empty();
	}

	private void addOptionalValue(Form form, String name, String value) {
		if (StringUtils.isNotBlank(value)) {
			form.add(name, value);
		}
	}

}
