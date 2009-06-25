package org.vpac.grisu.client.control;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

public class LocalServiceInterfaceCreator implements ServiceInterfaceCreator {
	
	static final Logger myLogger = Logger
	.getLogger(LocalServiceInterfaceCreator.class.getName());
	
	static final String DEFAULT_LOCAL_URL = "Local";

	public ServiceInterface create(String interfaceUrl, String username,
			char[] password, String myProxyServer, String myProxyPort,
			Object[] otherOptions) throws ServiceInterfaceException {

		
		Class localServiceInterfaceClass = null;

		try {
			localServiceInterfaceClass = Class.forName("org.vpac.grisu.control.serviceInterfaces.LocalServiceInterface");
		} catch (ClassNotFoundException e) {
			myLogger.warn("Could not find local service interface class.");
			e.printStackTrace();
			throw new ServiceInterfaceException("Could not find LocalServiceInterface class. Probably grisu-local-backend.jar is not in the classpath.", e);
		}
		
		ServiceInterface localServiceInterface;
		try {
			localServiceInterface = (ServiceInterface)localServiceInterfaceClass.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceInterfaceException("Could not create LocalServiceInterface: "+e.getLocalizedMessage(), e);
		}
		
		return localServiceInterface;
		
		
	}

	public boolean canHandleUrl(String url) {

		return DEFAULT_LOCAL_URL.equals(url);
	}

}
