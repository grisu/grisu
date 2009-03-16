

package org.vpac.grisu.control;

public class JobCreationException extends Exception {
	
	public JobCreationException (String message) {
		super(message);
	}
	
	public JobCreationException(String message, Exception e) {
		super(message, e);
	}

}
