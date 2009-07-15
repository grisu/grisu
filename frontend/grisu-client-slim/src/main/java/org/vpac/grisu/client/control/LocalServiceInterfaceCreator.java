package org.vpac.grisu.client.control;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

/**
 * A serviceInterfaceCreator that creates a serviceinterface that uses a plain
 * java object (LocalServiceInterface). This is the most simple grisu backend
 * one can imagine. All you need is to have the grisu-core module in your
 * classpath and you need to use the string "Local" as serviceinterfaceUrl.
 * 
 * @author Markus Binsteiner
 */
public class LocalServiceInterfaceCreator implements ServiceInterfaceCreator {

	static final Logger myLogger = Logger
			.getLogger(LocalServiceInterfaceCreator.class.getName());

	static final String DEFAULT_LOCAL_URL = "Local";

	public final ServiceInterface create(final String interfaceUrl, final String username,
			final char[] password, final String myProxyServer, final String myProxyPort,
			final Object[] otherOptions) throws ServiceInterfaceException {

		Class localServiceInterfaceClass = null;

		try {
			localServiceInterfaceClass = Class
					.forName("org.vpac.grisu.control.serviceInterfaces.LocalServiceInterface");
		} catch (ClassNotFoundException e) {
			myLogger.warn("Could not find local service interface class.");
			e.printStackTrace();
			throw new ServiceInterfaceException(
					"Could not find LocalServiceInterface class. Probably grisu-local-backend.jar is not in the classpath.",
					e);
		}

		ServiceInterface localServiceInterface;
		try {
			localServiceInterface = (ServiceInterface) localServiceInterfaceClass
					.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceInterfaceException(
					"Could not create LocalServiceInterface: "
							+ e.getLocalizedMessage(), e);
		}

		return localServiceInterface;

	}

	public final boolean canHandleUrl(final String url) {

		return DEFAULT_LOCAL_URL.equals(url);
	}

}
