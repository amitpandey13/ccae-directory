package com.pdgc.commons.exceptions;

public class CCAEInvalidConfigurationException extends CCAEBaseException{

	private static final long serialVersionUID = 1L;

	public CCAEInvalidConfigurationException(String message, Throwable cause) {
		super(message, cause);		
	}
	
	public CCAEInvalidConfigurationException(String message) {
		super(message);		
	}
}
