/**
 * 
 */
package com.pdgc.exceptions;

/**
 * @author Daavid Bigelow
 *
 */
public class AvailsMissingFromListException extends Exception {

	private static final long serialVersionUID = 1L;

	public AvailsMissingFromListException() {
        // TODO Auto-generated constructor stub
    }

    public AvailsMissingFromListException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public AvailsMissingFromListException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public AvailsMissingFromListException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
