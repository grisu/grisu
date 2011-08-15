package grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.ServiceInterfaceException")
public class ServiceInterfaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceInterfaceException(final String msg) {
		super(msg);
	}

	public ServiceInterfaceException(final String string, final Throwable e) {
		super(string, e);
	}

	public ServiceInterfaceException(final Throwable e) {
		super(e);
	}


}
