package grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.JobPropertiesException")
public class JobPropertiesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JobPropertiesException(final String message) {
		super(message);
	}

	public JobPropertiesException(final String message, Exception cause) {
		super(message, cause);
	}

}
