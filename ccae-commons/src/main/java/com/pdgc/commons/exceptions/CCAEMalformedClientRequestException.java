package com.pdgc.commons.exceptions;

public class CCAEMalformedClientRequestException extends CCAEBaseException{

	private static final long serialVersionUID = 1L;

	public CCAEMalformedClientRequestException(String message, Throwable cause) {
		super(message, cause);		
	}
	
	public CCAEMalformedClientRequestException(String message) {
		super(message);		
	}
}
