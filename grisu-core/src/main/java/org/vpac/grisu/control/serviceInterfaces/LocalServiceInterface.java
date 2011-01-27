package org.vpac.grisu.control.serviceInterfaces;

import grith.jgrith.myProxy.MyProxy_light;
import grith.jgrith.plainProxy.LocalProxy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.globus.myproxy.CredentialInfo;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.ietf.jgss.GSSException;
import org.vpac.grisu.backend.hibernate.HibernateSessionFactory;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.grisu.settings.ServerPropertiesManager;
import org.vpac.grisu.settings.ServiceTemplateManagement;

public class LocalServiceInterface extends AbstractServiceInterface implements
		ServiceInterface {

	private ProxyCredential credential = null;
	private String myproxy_username = null;
	private char[] passphrase = null;

	private User user;

	@Override
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
					throw new NoValidCredentialException(
							"Could not download myproxy credential: "
									+ e1.getLocalizedMessage());
				}

				try {
					credential = new ProxyCredential(
							MyProxy_light.getDelegation(myProxyServer,
									myProxyPort, myproxy_username, passphrase,
									3600));
					if (getUser() != null) {
						getUser().cleanCache();
					}
				} catch (RuntimeException re) {
					throw re;
				} catch (final Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public final long getCredentialEndTime() {

		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		final int myProxyPort = MyProxyServerParams.getMyProxyPort();

		try {
			// this is needed because of a possible round-robin myproxy server
			myProxyServer = InetAddress.getByName(myProxyServer)
					.getHostAddress();
		} catch (final UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info.getEndTime();

	}

	@Override
	public String getInterfaceInfo(String key) {
		if ("HOSTNAME".equalsIgnoreCase(key)) {
			return "localhost";
		} else if ("VERSION".equalsIgnoreCase(key)) {
			return ServiceInterface.INTERFACE_VERSION;
		} else if ("NAME".equalsIgnoreCase(key)) {
			return "Local serviceinterface";
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
