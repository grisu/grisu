package grisu.frontend.control.jaxws;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CommandLogHandler implements SOAPHandler<SOAPMessageContext> {

	Logger myLogger = LoggerFactory.getLogger(CommandLogHandler.class);
	private final BindingProvider bp;

	public CommandLogHandler(BindingProvider bp) {
		this.bp = bp;
	}

	public void close(MessageContext arg0) {
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleFault(SOAPMessageContext arg0) {
		logToSystemOut(arg0);
		return true;
	}

	public boolean handleMessage(SOAPMessageContext arg0) {
		logToSystemOut(arg0);
		return true;
	}

	private void logToSystemOut(SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty) {
			Map rh = (Map) bp.getRequestContext().get(
					MessageContext.HTTP_REQUEST_HEADERS);
			String cmdid = MDC.get("cmdid");
			if (StringUtils.isBlank(cmdid)) {
				cmdid = "n/a";
			}
			rh.put("X-command-id", Collections.singletonList(cmdid));
		}

		// if (outboundProperty.booleanValue()) {
		// System.out.println("\nOutgoing message:");
		// } else {
		// System.out.println("\nIncoming message:");
		// }
		//
		// SOAPMessage message = smc.getMessage();
		// try {
		// message.writeTo(System.out);
		// } catch (Exception e) {
		// System.out.println("Exception in handler: " + e);
		// }
	}

}
