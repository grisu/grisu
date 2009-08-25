package org.vpac.grisu.client.control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.ssl.HttpSecureProtocol;
import org.apache.commons.ssl.TrustMaterial;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.settings.CaCertManager;
import org.vpac.grisu.settings.ClientPropertiesManager;

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
		
		String httpProxy = null;
		int httpProxyPort = -1;
		String httpProxyUsername = null;
		char[] httpProxyPassword = null;

		if (otherOptions == null && otherOptions.length == 4) {
			try {
				httpProxy = (String) otherOptions[0];
				httpProxyPort = (Integer) otherOptions[1];
				httpProxyUsername = (String) otherOptions[2];
				httpProxyPassword = (char[]) otherOptions[3];
			} catch (ClassCastException cce) {
				throw new ServiceInterfaceException(
						"Could not create serviceInterface: "
								+ cce.getLocalizedMessage(), cce);
			}
		}

		// Technique similar to
		// http://juliusdavies.ca/commons-ssl/TrustExample.java.html
		HttpSecureProtocol protocolSocketFactory;
		try {
			protocolSocketFactory = new HttpSecureProtocol();

			TrustMaterial trustMaterial = null;

			// "/thecertificate.cer" can be PEM or DER (raw ASN.1). Can even
			// be several PEM certificates in one file.

			String cacertFilename = System.getProperty("grisu.cacert");
			URL cacertURL = null;

			try {
				if (cacertFilename != null && !"".equals(cacertFilename)) {
					cacertURL = XfireServiceInterfaceCreator.class
							.getResource("/" + cacertFilename);
					if (cacertURL != null) {
						myLogger.debug("Using cacert " + cacertFilename
								+ " as configured in the -D option.");
					}
				}
			} catch (Exception e) {
				// doesn't matter
				myLogger
						.debug("Couldn't find specified cacert. Using default one.");
			}

			if (cacertURL == null) {

				cacertFilename = new CaCertManager()
						.getCaCertNameForServiceInterfaceUrl(interfaceUrl);
				if (cacertFilename != null && cacertFilename.length() > 0) {
					myLogger
							.debug("Found url in map. Trying to use this cacert file: "
									+ cacertFilename);
					cacertURL = XfireServiceInterfaceCreator.class
							.getResource("/" + cacertFilename);
					if (cacertURL == null) {
						myLogger
								.debug("Didn't find cacert. Using the default one.");
						// use the default one
						cacertURL = XfireServiceInterfaceCreator.class
								.getResource("/cacert.pem");
					} else {
						myLogger.debug("Found cacert. Using it. Good.");
					}
				} else {
					myLogger
							.debug("Didn't find any configuration for a special cacert. Using the default one.");
					// use the default one
					cacertURL = XfireServiceInterfaceCreator.class
							.getResource("/cacert.pem");
				}

			}

			trustMaterial = new TrustMaterial(cacertURL);

			// We can use setTrustMaterial() instead of addTrustMaterial()
			// if we want to remove
			// HttpSecureProtocol's default trust of TrustMaterial.CACERTS.
			protocolSocketFactory.addTrustMaterial(trustMaterial);

			// Maybe we want to turn off CN validation (not recommended!):
			protocolSocketFactory.setCheckHostname(false);

			Protocol protocol = new Protocol("https",
					(ProtocolSocketFactory) protocolSocketFactory, 443);
			Protocol.registerProtocol("https", protocol);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			throw new ServiceInterfaceException(
					"Unspecified error while trying to establish secure connection.", e1);
		}
		

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
			
			Method getXfireClient = xfireServiceInterface.getClass().getMethod("getXFireClient");
			Object xfireClient = getXfireClient.invoke(xfireServiceInterface);
			
			Method setPropertyMethod = xfireClient.getClass().getMethod("setProperty", String.class, Object.class);
			setPropertyMethod.invoke(xfireClient, "urn:xfire:transport:http:chunking-enabled", "true");
			
			Long timeout = ClientPropertiesManager.getConnectionTimeoutInMS();
			setPropertyMethod.invoke(xfireClient, "http.timeout", timeout.toString());


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
