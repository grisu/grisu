package org.vpac.grisu.frontend.control.login;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.ssl.HttpSecureProtocol;
import org.apache.commons.ssl.TrustMaterial;
import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.ietf.jgss.GSSCredential;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.GrisuPluginFilenameFilter;
import org.vpac.security.light.control.CertificateFiles;
import org.vpac.security.light.plainProxy.PlainProxy;

import au.org.arcs.jcommons.constants.ArcsEnvironment;
import au.org.arcs.jcommons.dependencies.ClasspathHacker;
import au.org.arcs.jcommons.dependencies.Dependency;
import au.org.arcs.jcommons.dependencies.DependencyManager;
import au.org.arcs.jcommons.utils.ArcsSecurityProvider;

public class LoginManager {

	static final Logger myLogger = Logger.getLogger(LoginManager.class
			.getName());
	
	static final Map<String, String> SERVICEALIASES = new HashMap<String, String>();
	static {
		
		SERVICEALIASES.put("ARCS", "");
		SERVICEALIASES.put("ARCS_DEV", "https://ngportal.vpac.org/grisu-ws/soap/GrisuService");
		SERVICEALIASES.put("LOCAL_WS", "http://localhost:8080/soap/GrisuService");

	}

	/**
	 * One-for-all method to login to a Grisu backend.
	 * 
	 * Specify nothing except the loginParams (without the myproxy username &
	 * password) in order to use a local proxy. If you specify the password in
	 * addition to that the local x509 cert will be used to create a local proxy
	 * which in turn will be used to login to the Grisu backend.
	 * 
	 * If you specify the myproxy username & password in the login params those
	 * will be used for a simple myproxy login to the backend.
	 * 
	 * In order to use shibboleth login, you need to specify the password, the
	 * idp-username and the name of the idp.
	 * 
	 * @param password
	 *            the password or null
	 * @param username
	 *            the shib-username or null
	 * @param idp
	 *            the name of the idp or null
	 * @param loginParams
	 *            the login parameters
	 * @return the serviceinterface
	 * @throws LoginException
	 *             if the login doesn't succeed
	 * @throws IOException
	 *             if necessary plugins couldn't be downloaded/stored in the
	 *             .grisu/plugins folder
	 */
	public static ServiceInterface login(GlobusCredential cred,
			char[] password, String username, String idp,
			LoginParams loginParams) throws LoginException {

		java.security.Security.addProvider(new ArcsSecurityProvider());

		java.security.Security.setProperty("ssl.TrustManagerFactory.algorithm",
				"TrustAllCertificates");

		try {
			addPluginsToClasspath();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			myLogger.warn(e2);
			throw new RuntimeException(e2);
		}
		
		Map<Dependency, String> dependencies = new HashMap<Dependency, String>();

		dependencies.put(Dependency.BOUNCYCASTLE, "jdk15-143");

		DependencyManager.addDependencies(dependencies, ArcsEnvironment.getArcsCommonJavaLibDirectory());

		try {
			CertificateFiles.copyCACerts();
		} catch (Exception e1) {
			// e1.printStackTrace();
			myLogger.warn(e1);
		}

		// do the cacert thingy
		try {
			URL cacertURL = LoginManager.class.getResource("/ipsca.pem");
			HttpSecureProtocol protocolSocketFactory = new HttpSecureProtocol();

			TrustMaterial trustMaterial = null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}


		String serviceInterfaceUrl = loginParams.getServiceInterfaceUrl();

		if ("Local".equals(serviceInterfaceUrl)
				|| "Dummy".equals(serviceInterfaceUrl)) {
			
			dependencies = new HashMap<Dependency, String>();

			dependencies.put(Dependency.GRISU_LOCAL_BACKEND, ServiceInterface.INTERFACE_VERSION);
			
			DependencyManager.addDependencies(dependencies, Environment.getGrisuPluginDirectory());

		} else if (serviceInterfaceUrl.startsWith("http")) {

			// assume xfire -- that needs to get smarter later on
			
			dependencies = new HashMap<Dependency, String>();

			dependencies.put(Dependency.GRISU_XFIRE_CLIENT_LIBS, ServiceInterface.INTERFACE_VERSION);
			// also try to use client side mds
			dependencies.put(Dependency.CLIENT_SIDE_MDS, ServiceInterface.INTERFACE_VERSION);
			
			DependencyManager.addDependencies(dependencies, Environment.getGrisuPluginDirectory());
			
		}

		if (StringUtils.isBlank(username)) {

			if (StringUtils.isBlank(loginParams.getMyProxyUsername())) {

				if (cred != null) {
					try {
						return LoginHelpers.globusCredentialLogin(loginParams,
								cred);
					} catch (Exception e) {
						throw new LoginException("Could not login: "
								+ e.getLocalizedMessage(), e);
					}
				} else if (password == null || password.length == 0) {
					// means certificate auth
					try {
						// means try to load local proxy
						if (loginParams == null) {
							return LoginHelpers.defaultLocalProxyLogin();
						} else {
							return LoginHelpers
									.defaultLocalProxyLogin(loginParams);
						}
					} catch (Exception e) {
						throw new LoginException("Could not login: "
								+ e.getLocalizedMessage(), e);
					}
				} else {
					// means to create local proxy
					try {
						return LoginHelpers.localProxyLogin(password,
								loginParams);
					} catch (ServiceInterfaceException e) {
						throw new LoginException("Could not login: "
								+ e.getLocalizedMessage(), e);
					}
				}

			} else {
				// means myproxy login
				try {
					return LoginHelpers.myProxyLogin(loginParams);
				} catch (ServiceInterfaceException e) {
					throw new LoginException("Could not login: "
							+ e.getLocalizedMessage(), e);
				}
			}
		} else {
			try {
				// means shib login
				dependencies = new HashMap<Dependency, String>();

				dependencies.put(Dependency.ARCSGSI, "1.2-SNAPSHOT");
				
				DependencyManager.addDependencies(dependencies, Environment.getGrisuPluginDirectory());

				GSSCredential slcsproxy = slcsMyProxyInit(username, password,
						idp);
				return LoginHelpers.gssCredentialLogin(loginParams, slcsproxy);
			} catch (Exception e) {
				e.printStackTrace();
				throw new LoginException("Could not do slcs login: "
						+ e.getLocalizedMessage(), e);
			}

		}

	}

	public static GSSCredential slcsMyProxyInit(String username,
			char[] password, String idp) throws Exception {

		try {

			Class slcsClass = Class.forName("au.org.arcs.auth.slcs.SLCS");
			Object slcsObject = slcsClass.newInstance();

			Method initMethod = slcsClass.getMethod("init", String.class,
					char[].class, String.class);
			initMethod.invoke(slcsObject, username, password, idp);

			Method getCredMethod = slcsClass.getMethod("getCertificate");
			X509Certificate cert = (X509Certificate) getCredMethod
					.invoke(slcsObject);

			Method getKeyMethod = slcsClass.getMethod("getPrivateKey");
			PrivateKey privateKey = (PrivateKey) getKeyMethod
					.invoke(slcsObject);

			GSSCredential cred = PlainProxy.init(cert, privateKey, 24 * 7);

			return cred;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static void addPluginsToClasspath() throws IOException {

		ClasspathHacker.initFolder(Environment.getGrisuPluginDirectory(), new GrisuPluginFilenameFilter());

	}

}
