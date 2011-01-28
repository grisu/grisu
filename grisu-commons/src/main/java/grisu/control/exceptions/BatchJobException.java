package grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.BatchJobException")
public class BatchJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BatchJobException(Exception e) {
		super(e);
	}

	public BatchJobException(String message) {
		super(message);
	}

}
