package com.pdgc.avails.structures.exception;

/**
 * Indicates that a failure occurred at the remote job-level for a product batch.  
 * 
 * Note that this is at a more general level exception than {@code FailedProductCalculationException}.   
 * 
 * @author Clara Hong
 *
 */
public class FailedRemoteJobException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FailedRemoteJobException(String message) {
		super(message);
	}
	
	public FailedRemoteJobException(String message, Throwable e) {
		super(message, e);
	}

}
