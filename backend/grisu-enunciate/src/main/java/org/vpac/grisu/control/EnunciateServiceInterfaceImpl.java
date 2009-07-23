package org.vpac.grisu.control;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;
import org.codehaus.enunciate.modules.spring_app.HTTPRequestContext;
import org.globus.myproxy.CredentialInfo;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.ietf.jgss.GSSCredential;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.info.CachedMdsInformationManager;
import org.vpac.grisu.control.serviceInterfaces.AbstractServiceInterface;
import org.vpac.grisu.control.serviceInterfaces.LocalServiceInterface;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.grisu.settings.ServerPropertiesManager;
import org.vpac.grisu.settings.ServiceTemplateManagement;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import au.org.arcs.mds.InformationManager;

/**
 * This abstract class implements most of the methods of the
 * {@link ServiceInterface} interface. This way developers don't have to waste
 * time to implement the whole interface just for some things that are site/grid
 * specific. Currently there are two classes that extend this abstract class:
 * {@link LocalServiceInterface} and WsServiceInterface (which can be found in
 * the grisu-ws module).
 * 
 * The {@link LocalServiceInterface} is used to work with a small local database
 * like hsqldb so a user has got the whole grisu framework on his desktop. Of
 * course, all required ports have to be open from the desktop to the grid. On
 * the other hand no web service server is required.
 * 
 * The WsServiceInterface is the main one and it is used to set up a web service
 * somewhere. So light-weight clients can talk to it.
 * 
 * @author Markus Binsteiner
 * 
 */
@Path("/grisu")
@WebService(endpointInterface = "org.vpac.grisu.control.EnunciateServiceInterface")
@MTOM(enabled=true)
public class EnunciateServiceInterfaceImpl extends AbstractServiceInterface implements EnunciateServiceInterface, ServiceInterface {

	static final Logger myLogger = Logger
			.getLogger(EnunciateServiceInterfaceImpl.class.getName());

	private InformationManager informationManager = CachedMdsInformationManager
			.getDefaultCachedMdsInformationManager(Environment
					.getGrisuDirectory().toString());

	/**
	 * This method has to be implemented by the endpoint specific
	 * ServiceInterface. Since there are a few different ways to get a proxy
	 * credential (myproxy, just use the one in /tmp/x509..., shibb,...) this
	 * needs to be implemented differently for every single situation.
	 * 
	 * @return the proxy credential that is used to contact the grid
	 */
	protected ProxyCredential getCredential() {

		System.out.println("AUTH!!!!!!!!!!!!!!!!!!!!!!!!!");

		HttpServletRequest req = HTTPRequestContext.get().getRequest();

		ProxyCredential sessionProxy = (ProxyCredential) (req.getSession()
				.getAttribute("credential"));

		if (sessionProxy != null && sessionProxy.isValid()) {

			System.out.println("Using old proxy!!");
			return sessionProxy;

		} else {

			System.out.println("No Proxy in session. Creating new one.");
			String auth_head = req.getHeader("authorization");

			if (auth_head != null && auth_head.startsWith("Basic")) {
				String usernpass = new String(
						org.apache.commons.codec.binary.Base64
								.decodeBase64((auth_head.substring(6)
										.getBytes())));
				String user = usernpass.substring(0, usernpass.indexOf(":"));
				String password = usernpass
						.substring(usernpass.indexOf(":") + 1);

				ProxyCredential proxy = createProxyCredential(user, password,
						MyProxyServerParams.getMyProxyServer(),
						MyProxyServerParams.getMyProxyPort(),
						ServerPropertiesManager.getMyProxyLifetime());

				boolean success = true;

				if (proxy == null || !proxy.isValid()) {
					success = false;
					System.out.println("AUTH not successful!!!!!!!!!!!!!!!!!1");
					return null;
				}

				req.getSession().setAttribute("credential", proxy);

				System.out.println("AUTH successful!!!!!!!!!!!!!!!!!!!!!");

				return proxy;
			} else {
				return null;
			}
		}

	}

	private ProxyCredential createProxyCredential(String username,
			String password, String myProxyServer, int port, int lifetime) {
		MyProxy myproxy = new MyProxy(myProxyServer, port);
		GSSCredential proxy = null;
		try {
			proxy = myproxy.get(username, password, lifetime);

			int remaining = proxy.getRemainingLifetime();

			if (remaining <= 0)
				throw new RuntimeException("Proxy not valid anymore.");

			return new ProxyCredential(proxy);
		} catch (Exception e) {
			e.printStackTrace();
			myLogger.error("Could not create myproxy credential: "
					+ e.getLocalizedMessage());
			return null;
		}

	}

	public long getCredentialEndTime() {

		MyProxy myproxy = new MyProxy(MyProxyServerParams.getMyProxyServer(),
				MyProxyServerParams.getMyProxyPort());
		CredentialInfo info = null;
		try {
			HttpServletRequest req = HTTPRequestContext.get().getRequest();
			String auth_head = req.getHeader("authorization");
			String usernpass = new String(
					org.apache.commons.codec.binary.Base64
							.decodeBase64((auth_head.substring(6).getBytes())));
			String user = usernpass.substring(0, usernpass.indexOf(":"));
			String password = usernpass.substring(usernpass.indexOf(":") + 1);
			info = myproxy.info(getCredential().getGssCredential(), user,
					password);
		} catch (MyProxyException e) {
			myLogger.error(e);
			return -1;
		}

		return info.getEndTime();
	}

	public String getTemplate(String application)
			throws NoSuchTemplateException {

		Document doc = ServiceTemplateManagement
				.getAvailableTemplate(application);

		String result;
		if (doc == null) {
			throw new NoSuchTemplateException(
					"Could not find template for application: " + application
							+ ".");
		} else {
			try {
				result = SeveralXMLHelpers.toString(doc);
			} catch (Exception e) {
				throw new NoSuchTemplateException(
						"Could not find valid xml template for application: "
								+ application + ".");
			}
		}

		return result;

	}

	public String[] listHostedApplicationTemplates() {
		return ServiceTemplateManagement.getAllAvailableApplications();
	}

	public void login(String username, String password) {

		getCredential();

	}

	public String logout() {
		
		myLogger.debug("Logging out user: "+getDN());
		
		HttpServletRequest req = HTTPRequestContext.get().getRequest();
		req.getSession().setAttribute("credential", null);
		
		return "Logged out.";
		
	}

}
