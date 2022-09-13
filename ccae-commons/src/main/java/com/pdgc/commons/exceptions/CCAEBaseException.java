package com.pdgc.commons.exceptions;

public class CCAEBaseException extends Exception {
 
	private static final long serialVersionUID = 1L;

	public CCAEBaseException(String message, Throwable cause) {
        super(message, cause);
    }

	public CCAEBaseException(String message) {
		super(message);
	}

}
