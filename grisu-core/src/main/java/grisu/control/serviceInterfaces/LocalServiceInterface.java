package grisu.control.serviceInterfaces;

import grisu.backend.hibernate.HibernateSessionFactory;
import grisu.backend.model.ProxyCredential;
import grisu.backend.model.User;
import grisu.backend.utils.CertHelpers;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchTemplateException;
import grisu.control.exceptions.NoValidCredentialException;
import grisu.settings.MyProxyServerParams;
import grisu.settings.ServerPropertiesManager;
import grisu.settings.ServiceTemplateManagement;
import grith.jgrith.myProxy.MyProxy_light;
import grith.jgrith.plainProxy.LocalProxy;
import grith.jgrith.voms.VO;
import grith.jgrith.voms.VOManagement.VOManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.globus.myproxy.CredentialInfo;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.ietf.jgss.GSSException;

public class LocalServiceInterface extends AbstractServiceInterface implements
ServiceInterface {

	private ProxyCredential credential = null;
	private String myproxy_username = null;
	private char[] passphrase = null;

	private User user;

	private static String hostname = null;

	// @Override
	protected final ProxyCredential getCredential() {

		long oldLifetime = -1;
		try {
			if (credential != null) {
				oldLifetime = credential.getGssCredential()
						.getRemainingLifetime();
			}
		} catch (final GSSException e2) {
			myLogger.debug("Problem getting lifetime of old certificate: " + e2);
			credential = null;
		}
		if (oldLifetime < ServerPropertiesManager
				.getMinProxyLifetimeBeforeGettingNewProxy()) {
			myLogger.debug("Credential reached minimum lifetime. Getting new one from myproxy. Old lifetime: "
					+ oldLifetime);
			this.credential = null;
			// user.cleanCache();
		}

		if ((credential == null) || !credential.isValid()) {

			if ((myproxy_username == null) || (myproxy_username.length() == 0)) {
				if ((passphrase == null) || (passphrase.length == 0)) {
					// try local proxy
					try {
						credential = new ProxyCredential(
								LocalProxy.loadGSSCredential());

						long newLifeTime = credential.getGssCredential()
								.getRemainingLifetime();
						if (oldLifetime < ServerPropertiesManager
								.getMinProxyLifetimeBeforeGettingNewProxy()) {
							throw new NoValidCredentialException(
									"Proxy lifetime smaller than minimum allowed lifetime.");
						}

					} catch (final Exception e) {
						throw new NoValidCredentialException(
								"Could not load credential/no valid login data.");
					}
					if (!credential.isValid()) {
						throw new NoValidCredentialException(
								"Local proxy is not valid anymore.");
					}
				}
			} else {
				// get credential from myproxy
				String myProxyServer = MyProxyServerParams.getMyProxyServer();
				final int myProxyPort = MyProxyServerParams.getMyProxyPort();

				try {
					// this is needed because of a possible round-robin myproxy
					// server
					myProxyServer = InetAddress.getByName(myProxyServer)
							.getHostAddress();
				} catch (final UnknownHostException e1) {
					myLogger.error(e1);
					throw new NoValidCredentialException(
							"Could not download myproxy credential: "
									+ e1.getLocalizedMessage());
				}

				try {
					credential = new ProxyCredential(
							MyProxy_light.getDelegation(myProxyServer,
									myProxyPort, myproxy_username, passphrase,
									ServerPropertiesManager.getMyProxyLifetime()));

					long newLifeTime = credential.getGssCredential()
							.getRemainingLifetime();
					if (newLifeTime < ServerPropertiesManager
							.getMinProxyLifetimeBeforeGettingNewProxy()) {
						throw new NoValidCredentialException(
								"Proxy lifetime smaller than minimum allowed lifetime.");
					}

					if (getUser() != null) {
						getUser().cleanCache();
					}
				} catch (RuntimeException re) {
					throw re;
				} catch (final Throwable e) {
					myLogger.error(e);
					throw new NoValidCredentialException(
							"Could not get myproxy credential: "
									+ e.getLocalizedMessage());
				}
				if (!credential.isValid()) {
					throw new NoValidCredentialException(
							"MyProxy credential is not valid.");
				}
			}
		}

		return credential;

	}

	// @Override
	protected final ProxyCredential getCredential(String fqan,
			int lifetimeInSeconds) {

		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		final int myProxyPort = MyProxyServerParams.getMyProxyPort();

		ProxyCredential temp;
		try {
			temp = new ProxyCredential(MyProxy_light.getDelegation(
					myProxyServer, myProxyPort, myproxy_username, passphrase,
					lifetimeInSeconds));
			if (StringUtils.isNotBlank(fqan)) {

				final VO vo = VOManagement
						.getVO(getUser().getFqans().get(fqan));
				ProxyCredential credToUse = CertHelpers.getVOProxyCredential(
						vo, fqan, temp);

				myLogger.debug("Created proxy with lifetime: "
						+ credToUse.getExpiryDate().toString());
				return credToUse;
			} else {
				myLogger.debug("Created proxy with lifetime: "
						+ temp.getExpiryDate().toString());
				return temp;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final long getCredentialEndTime() {

		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		final int myProxyPort = MyProxyServerParams.getMyProxyPort();

		try {
			// this is needed because of a possible round-robin myproxy server
			myProxyServer = InetAddress.getByName(myProxyServer)
					.getHostAddress();
		} catch (final UnknownHostException e1) {
			myLogger.error(e1);
			throw new NoValidCredentialException(
					"Could not download myproxy credential: "
							+ e1.getLocalizedMessage());
		}

		final MyProxy myproxy = new MyProxy(myProxyServer, myProxyPort);
		CredentialInfo info = null;
		try {
			info = myproxy.info(getCredential().getGssCredential(),
					myproxy_username, new String(passphrase));
		} catch (final MyProxyException e) {
			myLogger.error(e);
		}

		return info.getEndTime();

	}

	@Override
	public String getInterfaceInfo(String key) {
		if (hostname == null) {
			try {
				final InetAddress addr = InetAddress.getLocalHost();
				final byte[] ipAddr = addr.getAddress();
				hostname = addr.getHostName();
			} catch (final UnknownHostException e) {
				hostname = "Unavailable";
			}
		} else if ("VERSION".equalsIgnoreCase(key)) {
			return Integer.toString(ServiceInterface.API_VERSION);
		} else if ("NAME".equalsIgnoreCase(key)) {
			return "Local serviceinterface";
		} else if ("BACKEND_VERSION".equalsIgnoreCase(key)) {
			return BACKEND_VERSION;
		}

		return null;
	}

	public final String getTemplate(final String application)
			throws NoSuchTemplateException {
		final String temp = ServiceTemplateManagement.getTemplate(application);

		if (StringUtils.isBlank(temp)) {
			throw new NoSuchTemplateException(
					"Could not find template for application: " + application
					+ ".");
		}
		return temp;
	}

	@Override
	protected final synchronized User getUser() {

		if (user == null) {
			this.user = User.createUser(getCredential(), this);
		}

		user.setCred(getCredential());

		return user;
	}

	public final String[] listHostedApplicationTemplates() {
		return ServiceTemplateManagement.getAllAvailableApplications();
	}

	public final void login(final String username, final String password) {

		this.myproxy_username = username;
		this.passphrase = password.toCharArray();

		try {
			// init database and make sure everything is all right
			HibernateSessionFactory.getSessionFactory();
		} catch (final Throwable e) {
			throw new RuntimeException("Could not initialize database.", e);
		}

		try {
			getCredential();
		} catch (final RuntimeException re) {
			throw re;
		} catch (final Exception e) {
			// e.printStackTrace();
			throw new NoValidCredentialException("No valid credential: "
					+ e.getLocalizedMessage());
		}

	}

	public final String logout() {
		Arrays.fill(passphrase, 'x');
		return null;
	}

}
