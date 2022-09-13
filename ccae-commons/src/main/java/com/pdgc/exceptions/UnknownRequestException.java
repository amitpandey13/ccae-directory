package com.pdgc.exceptions;

public class UnknownRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownRequestException() {
		super();
	}

	public UnknownRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnknownRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownRequestException(String message) {
		super(message);
	}

	public UnknownRequestException(Throwable cause) {
		super(cause);
	}
	
	
}
