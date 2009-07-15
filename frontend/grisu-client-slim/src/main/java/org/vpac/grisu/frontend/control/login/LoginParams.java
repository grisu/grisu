package org.vpac.grisu.frontend.control.login;

import java.util.Arrays;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.settings.MyProxyServerParams;

/**
 * A class that holds all information that is needed to login to a Grisu web
 * service.
 * 
 * There has to be a proxy delegated to a MyProxy server, though.
 * 
 * @author Markus Binsteiner
 * 
 */
public class LoginParams {

	private String serviceInterfaceUrl = null;
	private String myProxyUsername = null;
	private char[] myProxyPassphrase = null;
	private String myProxyServer = null;
	private String myProxyPort = null;
	private String httpProxy = null;
	private int httpProxyPort = -1;
	private String httpProxyUsername = null;
	private char[] httpProxyPassphrase = null;

	/**
	 * This one uses the default myproxy server & port. No http proxy is used.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the grisu backend to connect to
	 * @param myProxyUsername
	 *            your myproxy username
	 * @param myProxyPassphrase
	 *            your myproxy password
	 */
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername,
			char[] myProxyPassphrase) {
		this(serviceInterfaceUrl, myProxyUsername, myProxyPassphrase,
				MyProxyServerParams.DEFAULT_MYPROXY_SERVER, new Integer(
						MyProxyServerParams.DEFAULT_MYPROXY_PORT).toString());
	}

	/**
	 * This one allows you to specify the myproxy server & port to use. No http
	 * proxy is used.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the grisu backend to connect to
	 * @param myProxyUsername
	 *            your myproxy username
	 * @param myProxyPassphrase
	 *            your myproxy password
	 * @param myProxyServer
	 *            the myproxy server to use
	 * @param myProxyPort
	 *            the port of the myproxy server
	 */
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername,
			char[] myProxyPassphrase, String myProxyServer, String myProxyPort) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
		this.myProxyUsername = myProxyUsername;
		this.myProxyPassphrase = myProxyPassphrase;
		this.myProxyServer = myProxyServer;
		this.myProxyPort = myProxyPort;
	}

	/**
	 * This one allows you to specify the myproxy server & port as well as the
	 * http proxy server & port. No http proxy authentication.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the grisu backend to connect to
	 * @param myProxyUsername
	 *            your myproxy username
	 * @param myProxyPassphrase
	 *            your myproxy password
	 * @param myProxyServer
	 *            the myproxy server hostname
	 * @param myProxyPort
	 *            the myproxy server port
	 * @param httpProxy
	 *            the http proxy server hostname
	 * @param httpProxyPort
	 *            the http proxy server port
	 */
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername,
			char[] myProxyPassphrase, String myProxyServer, String myProxyPort,
			String httpProxy, int httpProxyPort) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
		this.myProxyUsername = myProxyUsername;
		this.myProxyPassphrase = myProxyPassphrase;
		this.myProxyServer = myProxyServer;
		this.myProxyPort = myProxyPort;
		this.httpProxy = httpProxy;
		this.httpProxyPort = httpProxyPort;
	}

	/**
	 * This one allows you to specify everything: myproxy server & port, http
	 * proxy server & port & username & password.
	 * 
	 * @param serviceInterfaceUrl
	 *            the url of the grisu backend to connect to
	 * @param myProxyUsername
	 *            your myproxy username
	 * @param myProxyPassphrase
	 *            your myproxy password
	 * @param myProxyServer
	 *            the myproxy server hostname
	 * @param myProxyPort
	 *            the myproxy server port
	 * @param httpProxy
	 *            the http proxy server hostname
	 * @param httpProxyPort
	 *            the http proxy server port
	 * @param httpProxyUsername
	 *            the http proxy server username
	 * @param httpProxyPassphrase
	 *            the http proxy server password
	 */
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername,
			char[] myProxyPassphrase, String myProxyServer, String myProxyPort,
			String httpProxy, int httpProxyPort, String httpProxyUsername,
			char[] httpProxyPassphrase) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
		this.myProxyUsername = myProxyUsername;
		this.myProxyPassphrase = myProxyPassphrase;
		this.myProxyServer = myProxyServer;
		this.myProxyPort = myProxyPort;
		this.httpProxy = httpProxy;
		this.httpProxyPort = httpProxyPort;
		this.httpProxyUsername = httpProxyUsername;
		this.httpProxyPassphrase = httpProxyPassphrase;
	}

	/**
	 * Returns the url of the backend to connect to.
	 * 
	 * @return the serviceInterface url
	 */
	public String getServiceInterfaceUrl() {
		return serviceInterfaceUrl;
	}

	/**
	 * Sets the url of the backend to connect to.
	 * 
	 * @param serviceInterfaceUrl
	 *            the serviceInterface url
	 */
	public void setServiceInterfaceUrl(String serviceInterfaceUrl) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
	}

	/**
	 * Returns the myproxy username.
	 * 
	 * @return the myproxy username
	 */
	public String getMyProxyUsername() {
		return myProxyUsername;
	}

	/**
	 * Sets the myproxy username.
	 * 
	 * @param myProxyUsername
	 *            the myproxy username
	 */
	public void setMyProxyUsername(String myProxyUsername) {
		this.myProxyUsername = myProxyUsername;
	}

	/**
	 * Returns the myproxy password.
	 * 
	 * @return the myproxy password
	 */
	public char[] getMyProxyPassphrase() {
		return myProxyPassphrase;
	}

	/**
	 * Sets the myproxy password.
	 * 
	 * @param myProxyPassphrase
	 *            the myproxy password
	 */
	public void setMyProxyPassphrase(char[] myProxyPassphrase) {
		this.myProxyPassphrase = myProxyPassphrase;
	}

	/**
	 * Returns the http proxy server hostname.
	 * 
	 * @return the http proxy server hostname
	 */
	public String getHttpProxy() {
		return httpProxy;
	}

	/**
	 * Sets the http proxy server port.
	 * 
	 * @param httpProxy
	 *            the http proxy server port
	 */
	public void setHttpProxy(String httpProxy) {
		this.httpProxy = httpProxy;
	}

	/**
	 * Gets the http proxy server port.
	 * 
	 * @return the http proxy server port
	 */
	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	/**
	 * Returns the port of the http proxy server.
	 * 
	 * @param httpProxyPort
	 *            the http proxy server port
	 */
	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	/**
	 * Returns the username for the http proxy server.
	 * 
	 * @return the http proxy server username
	 */
	public String getHttpProxyUsername() {
		return httpProxyUsername;
	}

	/**
	 * Sets the http proxy server username.
	 * 
	 * @param httpProxyUsername
	 *            the username for the http proxy server
	 */
	public void setHttpProxyUsername(String httpProxyUsername) {
		this.httpProxyUsername = httpProxyUsername;
	}

	/**
	 * Returns the password for the http proxy server.
	 * 
	 * @return the http proxy server password
	 */
	public char[] getHttpProxyPassphrase() {
		return httpProxyPassphrase;
	}

	/**
	 * Sets the password for the http proxy server.
	 * 
	 * @param httpProxyPassphrase
	 *            the http proxy server password
	 */
	public void setHttpProxyPassphrase(char[] httpProxyPassphrase) {
		this.httpProxyPassphrase = httpProxyPassphrase;
	}

	/**
	 * Clears the password for the http proxy server in memory. Not the myproxy
	 * one, though, since this is still needed after login.
	 */
	public void clearPasswords() {
		// if ( this.myProxyPassphrase != null ) {
		// Arrays.fill(this.myProxyPassphrase, 'x');
		// }
		if (this.httpProxyPassphrase != null) {
			Arrays.fill(this.httpProxyPassphrase, 'x');
		}

	}

	/**
	 * Returns the hostname of the myproxy server.
	 * 
	 * @return the myproxy server hostname
	 */
	public String getMyProxyServer() {
		return myProxyServer;
	}

	/**
	 * Sets the hostname of the myproxy server to use.
	 * 
	 * @param myProxyServer
	 *            the myproxy server hostname
	 */
	public void setMyProxyServer(String myProxyServer) {
		this.myProxyServer = myProxyServer;
	}

	/**
	 * Returns the port of the myproxy server.
	 * 
	 * @return the myproxy server port
	 */
	public String getMyProxyPort() {
		return myProxyPort;
	}

	/**
	 * Sets the port of the myproxy server.
	 * 
	 * @param myProxyPort
	 *            the myproxy server port to use
	 */
	public void setMyProxyPort(String myProxyPort) {
		this.myProxyPort = myProxyPort;
	}

}
