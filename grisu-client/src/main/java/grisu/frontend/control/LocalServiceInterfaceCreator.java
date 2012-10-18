package grisu.frontend.control;

import grisu.control.ServiceInterface;
import grisu.control.ServiceInterfaceCreator;
import grisu.control.exceptions.ServiceInterfaceException;
import grisu.control.serviceInterfaces.LocalServiceInterface;
import grith.jgrith.cred.Cred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A serviceInterfaceCreator that creates a serviceinterface that uses a plain
 * java object (LocalServiceInterface). This is the most simple grisu backend
 * one can imagine. All you need is to have the grisu-core module in your
 * classpath and you need to use the string "Local" as serviceinterfaceUrl.
 * 
 * @author Markus Binsteiner
 */
public class LocalServiceInterfaceCreator implements ServiceInterfaceCreator {

	static final Logger myLogger = LoggerFactory
			.getLogger(LocalServiceInterfaceCreator.class.getName());

	static final String DEFAULT_LOCAL_URL = "Local";

	public final boolean canHandleUrl(final String url) {

		return DEFAULT_LOCAL_URL.equals(url);
	}


	public ServiceInterface create(String interfaceUrl, Cred cred,
			Object[] otherOptions) throws ServiceInterfaceException {
		
//		Class localServiceInterfaceClass = null;
//
//		try {
//			localServiceInterfaceClass = Class
//					.forName("grisu.control.serviceInterfaces.LocalServiceInterface");
//		} catch (final ClassNotFoundException e) {
//			myLogger.warn("Could not find local service interface class.");
//			throw new ServiceInterfaceException(
//					"Could not find LocalServiceInterface class. Probably local-backend.jar is not in the classpath.",
//					e);
//		}

		LocalServiceInterface localServiceInterface;
		try {
			localServiceInterface = new LocalServiceInterface();
			localServiceInterface.init(cred);
		} catch (final Exception e) {
			throw new ServiceInterfaceException(
					"Could not create LocalServiceInterface: "
							+ e.getLocalizedMessage(), e);
		}

		return localServiceInterface;

	}

}
