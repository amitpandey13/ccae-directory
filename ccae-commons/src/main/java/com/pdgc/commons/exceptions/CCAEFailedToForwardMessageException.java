package com.pdgc.commons.exceptions;

public class CCAEFailedToForwardMessageException extends CCAEBaseException{

	private static final long serialVersionUID = 1L;

	public CCAEFailedToForwardMessageException(String message, Throwable cause) {
		super(message, cause);		
	}
	
	public CCAEFailedToForwardMessageException(String message) {
		super(message);		
	}

}
