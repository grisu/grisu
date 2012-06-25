package grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.StatusException")
public class StatusException extends Exception {

	public StatusException() {
		// TODO Auto-generated constructor stub
	}

	public StatusException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public StatusException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public StatusException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
