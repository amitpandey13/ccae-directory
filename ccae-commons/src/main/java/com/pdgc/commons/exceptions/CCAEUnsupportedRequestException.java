package com.pdgc.commons.exceptions;

public class CCAEUnsupportedRequestException extends CCAEBaseException{

	private static final long serialVersionUID = 1L;

	public CCAEUnsupportedRequestException(String message, Throwable cause) {
		super(message, cause);		
	}
	
	public CCAEUnsupportedRequestException(String message) {
		super(message);		
	}
}
