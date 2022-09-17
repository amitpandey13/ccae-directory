package com.pdgc.general.service.auth;

import java.util.Optional;

/**
 * Interface to represent client authentication managers to help with authentication for client
 * applications.
 * 
 * @author Vishal Raut
 *
 * @param <R> the type of token request
 * @param <T> the type of token
 */
public interface IClientAuthenticationManager<R, T> {

	/**
	 * It should return the token obtained based on the submitted request.
	 */
	Optional<T> getToken(R tokenRequest);

}
