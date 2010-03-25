package org.vpac.grisu.frontend.control.login;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

/**
 * Creates {@link ServiceInterface}s using the url of a ServiceInterface.
 * 
 * At the moment it supports: Local, xfire & cxf serviceInterfaces.
 * 
 * The serviceInterface you want to create needs to be in the classpath.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class ServiceInterfaceFactory {

	static final Logger myLogger = Logger
	.getLogger(ServiceInterfaceFactory.class.getName());

	public static final String DEFAULT_SERVICE_INTERFACE = "ARCS";

	public static final String[] KNOWN_SERVICE_INTERFACE_CREATORS = new String[] {
		"LocalServiceInterfaceCreator",
		//			"DummyServiceInterfaceCreator",
		"JaxWsServiceInterfaceCreator",
		//			"XfireServiceInterfaceCreator",
		// the old xfire client...
		//			"XFireServiceInterfaceCreator"
	};

	/**
	 * Creates a serviceInterface from a {@link LoginParams} object.
	 * 
	 * @param params
	 *            the login parameters
	 * @return the serviceInterface
	 * @throws ServiceInterfaceException
	 *             if the serviceInterface couldn't be created
	 */
	public static ServiceInterface createInterface(final LoginParams params)
	throws ServiceInterfaceException {
		return createInterface(params.getServiceInterfaceUrl(), params
				.getMyProxyUsername(), params.getMyProxyPassphrase(), params
				.getMyProxyServer(), params.getMyProxyPort(), params
				.getHttpProxy(), params.getHttpProxyPort(), params
				.getHttpProxyUsername(), params.getMyProxyPassphrase());
	}

	/**
	 * Creates a ServiceInterface using the url that is provided. Most likely
	 * you want a ServiceInterface to a webservice. But it's also possible to
	 * create ServiceInterface that's on your local computer (not at the moment,
	 * though - if you are interested in this contact me via email:
	 * markus@vpac.org)
	 * 
	 * @param interfaceUrl
	 *            the url of the ServiceInterface
	 * @param username
	 *            the username of the MyProxy credential
	 * @param password
	 *            the password of the MyProxy credential
	 * @param myProxyServer
	 *            the url of the MyProxy server
	 * @param myProxyPort
	 *            the port of the MyProxy server
	 * @param httpProxy
	 *            the httproxy hostname to use if you want to connect to a web
	 *            service ServiceInterface or null if you don't
	 * @param httpProxyPort
	 *            the httproxy port to use if you want to connect to a web
	 *            service ServiceInterface or null if you don't
	 * @param httpProxyUsername
	 *            the httpproxy username to use if you need to authenticate
	 *            yourself to the httpproxy server or null if you don't need to
	 * @param httpProxyPassword
	 *            the httpproxy password to use if you need to authenticate
	 *            yourself to the httpproxy server or null if you don't need to
	 * @return the ServiceInterface to use to stage files/submit jobs
	 * @throws ServiceInterfaceException
	 */
	public static ServiceInterface createInterface(final String interfaceUrl,
			final String username, final char[] password,
			final String myProxyServer, final String myProxyPort,
			final String httpProxy, final int httpProxyPort,
			final String httpProxyUsername, final char[] httpProxyPassword)
	throws ServiceInterfaceException {

		Object[] otherOptions = new Object[4];
		otherOptions[0] = httpProxy;
		otherOptions[1] = httpProxyPort;
		otherOptions[2] = httpProxyUsername;
		otherOptions[3] = httpProxyPassword;

		Map<String, Exception> failedCreators = new HashMap<String, Exception>();

		for (String className : KNOWN_SERVICE_INTERFACE_CREATORS) {

			Class serviceInterfaceCreatorClass = null;

			try {
				serviceInterfaceCreatorClass = Class
				.forName("org.vpac.grisu.frontend.control." + className);
			} catch (ClassNotFoundException e) {
                            	myLogger.warn("Could not find serviceInterfaceCreator class: "	
                                         + className); 
				myLogger.warn("Probably not in classpath. No worries, trying next one...");
				continue;
			}

			ServiceInterfaceCreator serviceInterfaceCreator;
			try {
				serviceInterfaceCreator = (ServiceInterfaceCreator) serviceInterfaceCreatorClass
				.newInstance();
			} catch (Exception e) {
				// shouldn't really happen
				continue;
			}

			if (!serviceInterfaceCreator.canHandleUrl(interfaceUrl)) {
                            myLogger.debug(className + " doesn't handle url: "
                              + interfaceUrl
                              + ". Trying next serviceInterfaceCreator...");
				continue;
			}

			ServiceInterface serviceInterface = null;
			try {
				serviceInterface = serviceInterfaceCreator.create(interfaceUrl,
						username, password, myProxyServer, myProxyPort,
						otherOptions);
			} catch (ServiceInterfaceException e) {
				// e.printStackTrace();
				myLogger.debug("Couldn't connect to url " + interfaceUrl
						+ " using serviceInterfaceCreator " + className + ": "
						+ e.getLocalizedMessage());
				failedCreators.put(className, e);
				continue;
			}


			myLogger.info("Successfully created serviceInterface using creator: "+className);

			try {
				serviceInterface.login(username, new String(password));
			} catch (Exception e) {
				// e.printStackTrace();
				myLogger.debug("Couldn't login to grisu service on: "
						+ interfaceUrl + ": " + e.getLocalizedMessage());
				failedCreators.put(className, e);
				continue;
			}

			myLogger.info("Successfully logged in.");
			return serviceInterface;
		}

		StringBuffer failedOnes = new StringBuffer();
		for (String name : failedCreators.keySet()) {
			failedOnes.append(name + ": "
					+ failedCreators.get(name).getLocalizedMessage() + "\n");
		}

		if (failedCreators.size() == 1) {
			String key = failedCreators.keySet().iterator().next();
			throw new ServiceInterfaceException(failedCreators.get(key)
					.getLocalizedMessage(), failedCreators.get(key));
		} else if (failedCreators.size() == 0) {
			throw new ServiceInterfaceException(
					"Could not find a ServiceInterfaceCreator for this kind of URL.",
					null);
		} else {
			throw new ServiceInterfaceException(
					"Could not find a single ServiceInterfaceCreator that worked. Tried these:\n"
					+ failedOnes.toString(), null);
		}

	}

	private ServiceInterfaceFactory() {
	}

}
