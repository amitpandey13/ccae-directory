package com.pdgc.general.service;

/**
 * Just an exception thrown by the JobManager to indicate that memory reservation failed
 * @author Linda Xu
 *
 */
public class MemoryReservationException extends Exception {

	private static final long serialVersionUID = 1L;

    public MemoryReservationException() {
       
    }
	
	public MemoryReservationException(String message) {
        super(message);
    }
}
