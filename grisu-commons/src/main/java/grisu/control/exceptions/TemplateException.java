package grisu.control.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "grisu.control.jaxws.exceptions.TemplateException")
public class TemplateException extends Exception {

	public TemplateException() {
	}

	public TemplateException(String arg0) {
		super(arg0);
	}

	public TemplateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TemplateException(Throwable arg0) {
		super(arg0);
	}

}
