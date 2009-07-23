package org.vpac.grisu.client.control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

public class XfireServiceInterfaceCreator implements ServiceInterfaceCreator {

	static final Logger myLogger = Logger
			.getLogger(XfireServiceInterfaceCreator.class.getName());

	public boolean canHandleUrl(String url) {
		if (StringUtils.isNotBlank(url) && url.startsWith("http")) {
			return true;
		} else {
			return false;
		}
	}

	public ServiceInterface create(String interfaceUrl, String username,
			char[] password, String myProxyServer, String myProxyPort,
			Object[] otherOptions) throws ServiceInterfaceException {

		Class xfireServiceInterfaceClass = null;

		try {
			xfireServiceInterfaceClass = Class
					.forName("org.vpac.grisu.control.impl.EnunciateServiceInterfaceImpl");
		} catch (ClassNotFoundException e) {
			myLogger.warn("Could not find xfire service interface class.");
			e.printStackTrace();
			throw new ServiceInterfaceException(
					"Could not find XfireServiceInterface class. Probably grisu-client-xfire is not in the classpath.",
					e);
		}

		Object xfireServiceInterface = null;
		Class interfaceClass = null;
		try {
			Constructor xfireServiceInterfaceConstructor;
			interfaceClass = Class.forName("org.vpac.grisu.control.EnunciateServiceInterface");

			
			xfireServiceInterfaceConstructor = xfireServiceInterfaceClass
					.getConstructor(String.class);

			xfireServiceInterface = xfireServiceInterfaceConstructor
					.newInstance(interfaceUrl);

			Method setAuthMethod = xfireServiceInterface.getClass().getMethod(
					"setHttpAuthCredentials", String.class, String.class);
			
			setAuthMethod.invoke(xfireServiceInterface, username, new String(password));
			
			Method setMTOMEnabled = xfireServiceInterface.getClass().getMethod(
					"setMTOMEnabled", boolean.class);
			
			setMTOMEnabled.invoke(xfireServiceInterface, true);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceInterfaceException(
					"Could not create XfireServiceInterface: "
							+ e.getLocalizedMessage(), e);
		}
		
		return new ProxyServiceInterface(xfireServiceInterface);

	}

}
