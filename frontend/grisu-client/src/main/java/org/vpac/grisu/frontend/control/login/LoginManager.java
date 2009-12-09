package org.vpac.grisu.frontend.control.login;

import java.io.IOException;
import java.net.URL;
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
import org.ietf.jgss.GSSException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.GrisuPluginFilenameFilter;
import org.vpac.security.light.CredentialHelpers;
import org.vpac.security.light.control.CertificateFiles;
import org.vpac.security.light.plainProxy.LocalProxy;
import org.vpac.security.light.plainProxy.PlainProxy;

import au.org.arcs.jcommons.configuration.CommonArcsProperties;
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
	 * Simplest way to login.
	 * 
	 * Logs into a local backend using an already existing local proxy.
	 * 
	 * @return the serviceinterface
	 * @throws LoginException  if the login doesn't succeed
	 */
	public static ServiceInterface login() throws LoginException {
		return login((GlobusCredential)null, (char[])null, (String)null, (String)null, (LoginParams)null, false);
	}
	
	/**
	 * 2nd simplest way to login.
	 * 
	 * Logs into a backend using an already existing local proxy.
	 * 
	 * @param url the url of the serviceinterface
	 * 
	 * @return the serviceinterface
	 * @throws LoginException  if the login doesn't succeed
	 */
	public static ServiceInterface login(String url) throws LoginException {
		return login((GlobusCredential)null, (char[])null, (String)null, (String)null, url, false);
	}
	
	/**
	 * Standard shib login to local backend.
	 * 
	 * Logs into a backend using an already existing local proxy.
	 * 
	 * @param username the idp username
	 * @param password the idp password
	 * @param idp the idp name
	 * @param url the url of the serviceinterface
	 * @param saveCredendentialsToLocalProxy whether to save the credentials to a local proxy afterwards
	 * 
	 * @return the serviceinterface
	 * @throws LoginException  if the login doesn't succeed
	 */
	public static ServiceInterface shiblogin(String username, char[] password, String idp, boolean saveCredendentialsToLocalProxy) throws LoginException {
		return login((GlobusCredential)null, password, username, idp, "Local", saveCredendentialsToLocalProxy);
	}
	
	
	/**
	 * Standard shib login.
	 * 
	 * Logs into a backend using an already existing local proxy.
	 * 
	 * @param username the idp username
	 * @param password the idp password
	 * @param idp the idp name
	 * @param url the url of the serviceinterface
	 * @param saveCredendentialsToLocalProxy whether to save the credentials to a local proxy afterwards
	 * 
	 * @return the serviceinterface
	 * @throws LoginException  if the login doesn't succeed
	 */
	public static ServiceInterface shiblogin(String username, char[] password, String idp, String url, boolean saveCredendentialsToLocalProxy) throws LoginException {
		return login((GlobusCredential)null, password, username, idp, url, saveCredendentialsToLocalProxy);
	}
	
	public static ServiceInterface login(GlobusCredential cred,
			char[] password, String username, String idp,
			LoginParams loginParams) throws LoginException {
		return login(cred, password, username, idp, loginParams, false);
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
	 * @param url
	 *            the serviceInterfaceUrl to connect to
	 * @param saveCredentialAsLocalProxy 
	 * 			  whether to save the credential as a local proxy after successful login or not
	 * @return the serviceinterface
	 * @throws LoginException
	 *             if the login doesn't succeed
	 * @throws IOException
	 *             if necessary plugins couldn't be downloaded/stored in the
	 *             .grisu/plugins folder
	 */
	public static ServiceInterface login(GlobusCredential cred,
			char[] password, String username, String idp,
			String url, boolean saveCredentialAsLocalProxy) throws LoginException {
		
		LoginParams params = new LoginParams(url, null, null);
		return login(cred, password, username, idp, params, saveCredentialAsLocalProxy);
		
	}
	
	/**
	 * One-for-all method to login to a local Grisu backend.
	 * 
	 * Specify nothing in order to use a local proxy. If you specify the password in
	 * addition to that the local x509 cert will be used to create a proxy from your local cert
	 * which in turn will be used to login to the Grisu backend.
	 * 
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
	 * @param saveCredentialAsLocalProxy 
	 * 			  whether to save the credential as a local proxy after successful login or not
	 * @return the serviceinterface
	 * @throws LoginException
	 *             if the login doesn't succeed
	 * @throws IOException
	 *             if necessary plugins couldn't be downloaded/stored in the
	 *             .grisu/plugins folder
	 */
	public static ServiceInterface login(GlobusCredential cred,
			char[] password, String username, String idp,
			boolean saveCredentialAsLocalProxy) throws LoginException {
		
		LoginParams params = new LoginParams("Local", null, null);
		return login(cred, password, username, idp, params, saveCredentialAsLocalProxy);
		
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
	 * @param saveCredentialAsLocalProxy 
	 * 			  whether to save the credential as a local proxy after successful login or not
	 * @return the serviceinterface
	 * @throws LoginException
	 *             if the login doesn't succeed
	 * @throws IOException
	 *             if necessary plugins couldn't be downloaded/stored in the
	 *             .grisu/plugins folder
	 */
	public static ServiceInterface login(GlobusCredential cred,
			char[] password, String username, String idp,
			LoginParams loginParams, boolean saveCredentialAsLocalProxy) throws LoginException {

		java.security.Security.addProvider(new ArcsSecurityProvider());

		java.security.Security.setProperty("ssl.TrustManagerFactory.algorithm",
				"TrustAllCertificates");
		
		if ( loginParams == null ) {
			loginParams = new LoginParams("Local", null, null);
		}

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
						//TODO should put that one somewhere else
						if ( saveCredentialAsLocalProxy ) {
						try {
							LocalProxy.gridProxyInit(password, 240);
						} catch (Exception e) {
							throw new ServiceInterfaceException("Could not create local proxy.", e);
						}
						}
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
				
				if ( saveCredentialAsLocalProxy ) {
					CredentialHelpers.writeToDisk(slcsproxy);
				}
				
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

		return SlcsLoginWrapper.slcsMyProxyInit(username, password, idp);

	}
	
	public static void main(String[] args) throws LoginException {
		
		ServiceInterface si = LoginManager.shiblogin("markus", args[0].toCharArray(), "VPAC", true);
		
	}

	public static void addPluginsToClasspath() throws IOException {

		ClasspathHacker.initFolder(Environment.getGrisuPluginDirectory(), new GrisuPluginFilenameFilter());

	}

}
