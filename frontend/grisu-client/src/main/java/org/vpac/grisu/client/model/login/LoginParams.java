

package org.vpac.grisu.client.model.login;

import java.util.Arrays;

/**
 * A class that holds all information that is needed to login to a Grisu web service.
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
	
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername, char[] myProxyPassphrase, String myProxyServer, String myProxyPort) { this.serviceInterfaceUrl = serviceInterfaceUrl;
		this.myProxyUsername = myProxyUsername;
		this.myProxyPassphrase = myProxyPassphrase;
		this.myProxyServer = myProxyServer;
		this.myProxyPort = myProxyPort;
	}
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername, char[] myProxyPassphrase, String myProxyServer, String myProxyPort, String httpProxy, int httpProxyPort) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
		this.myProxyUsername = myProxyUsername;
		this.myProxyPassphrase = myProxyPassphrase;
		this.myProxyServer = myProxyServer;
		this.myProxyPort = myProxyPort;
		this.httpProxy = httpProxy;
		this.httpProxyPort = httpProxyPort;
	}
	public LoginParams(String serviceInterfaceUrl, String myProxyUsername, char[] myProxyPassphrase, String myProxyServer, String myProxyPort, String httpProxy, int httpProxyPort, String httpProxyUsername, char[] httpProxyPassphrase) {
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
	public String getServiceInterfaceUrl() {
		return serviceInterfaceUrl;
	}
	public void setServiceInterfaceUrl(String serviceInterfaceUrl) {
		this.serviceInterfaceUrl = serviceInterfaceUrl;
	}
	public String getMyProxyUsername() {
		return myProxyUsername;
	}
	public void setMyProxyUsername(String myProxyUsername) {
		this.myProxyUsername = myProxyUsername;
	}
	public char[] getMyProxyPassphrase() {
		return myProxyPassphrase;
	}
	public void setMyProxyPassphrase(char[] myProxyPassphrase) {
		this.myProxyPassphrase = myProxyPassphrase;
	}
	public String getHttpProxy() {
		return httpProxy;
	}
	public void setHttpProxy(String httpProxy) {
		this.httpProxy = httpProxy;
	}
	public int getHttpProxyPort() {
		return httpProxyPort;
	}
	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}
	public String getHttpProxyUsername() {
		return httpProxyUsername;
	}
	public void setHttpProxyUsername(String httpProxyUsername) {
		this.httpProxyUsername = httpProxyUsername;
	}
	public char[] getHttpProxyPassphrase() {
		return httpProxyPassphrase;
	}
	public void setHttpProxyPassphrase(char[] httpProxyPassphrase) {
		this.httpProxyPassphrase = httpProxyPassphrase;
	}
	public void clearPasswords() {
//		if ( this.myProxyPassphrase != null ) {
//			Arrays.fill(this.myProxyPassphrase, 'x');
//		}
		if ( this.httpProxyPassphrase != null ) {
			Arrays.fill(this.httpProxyPassphrase, 'x');
		}
		
	}
	public String getMyProxyServer() {
		return myProxyServer;
	}
	public void setMyProxyServer(String myProxyServer) {
		this.myProxyServer = myProxyServer;
	}
	public String getMyProxyPort() {
		return myProxyPort;
	}
	public void setMyProxyPort(String myProxyPort) {
		this.myProxyPort = myProxyPort;
	}

}
