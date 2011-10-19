package grisu.frontend.control.login;

import grisu.jcommons.configuration.CommonGridProperties;
import grisu.settings.ClientPropertiesManager;
import grith.gsindl.SLCS;
import grith.jgrith.plainProxy.PlainProxy;
import grith.sibboleth.CredentialManager;
import grith.sibboleth.DummyCredentialManager;
import grith.sibboleth.DummyIdpObject;
import grith.sibboleth.IdpObject;
import grith.sibboleth.Shibboleth;
import grith.sibboleth.StaticCredentialManager;
import grith.sibboleth.StaticIdpObject;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSCredential;

import com.google.common.collect.ImmutableList;

public class SlcsLoginWrapper {

	static final Logger myLogger = Logger.getLogger(SlcsLoginWrapper.class
			.getName());

	private static List<String> cachedIdps = null;

	public static List<String> getAllIdps() throws Throwable {
		if (cachedIdps == null) {
			String id = UUID.randomUUID().toString();
			final IdpObject idpObj = new DummyIdpObject();
			final CredentialManager cm = new DummyCredentialManager();

			myLogger.debug("Login: starting to get list of idps... (id: " + id
					+ ")");

			try {
				final Shibboleth shib = new Shibboleth(idpObj, cm);
				shib.openurl(SLCS.DEFAULT_SLCS_URL);
				myLogger.debug("Login: list of idps loaded (id: " + id + ")");

				cachedIdps = ImmutableList.copyOf(idpObj.getIdps());

			} catch (Throwable e) {
				myLogger.debug("Login: error loading list of idps (id:" + id
						+ ")");
				throw e;
			}

		}
		return cachedIdps;
	}

	public static GSSCredential slcsMyProxyInit(String username,
			char[] password, String idp, LoginParams params) throws Exception {

		myLogger.debug("SLCS login: starting slcs/myproxy login...");
		String id = UUID.randomUUID().toString();
		try {

			if (params != null) {
				myLogger.debug("SLCS login: Setting http proxy...");
				final String httproxy = params.getHttpProxy();
				final int httpProxyPort = params.getHttpProxyPort();

				if (StringUtils.isNotBlank(httproxy)) {
					Shibboleth.setHttpProxy(httproxy, httpProxyPort,
							params.getHttpProxyUsername(),
							params.getMyProxyPassphrase());
				}
			}

			myLogger.debug("SLCS login: setting idpObject and credentialManager...");
			final IdpObject idpO = new StaticIdpObject(idp);
			final CredentialManager cm = new StaticCredentialManager(username,
					password);

			String url = ClientPropertiesManager.getShibbolethUrl();

			myLogger.debug("SLCS login: starting actual login... (id: " + id
					+ ")");
			final SLCS slcs = new SLCS(url, idpO, cm);
			if ((slcs.getCertificate() == null)
					|| (slcs.getPrivateKey() == null)) {
				// myLogger.debug("SLCS login: Could not get SLCS certificate and/or SLCS key... (id: "
				// + id + ")");
				throw new Exception(
						"Could not get SLCS certificate and/or SLCS key...");
			}

			myLogger.debug("SLCS login: Login finished (id: " + id + ")");
			myLogger.debug("SLCS login: Creating local proxy...");

			final GSSCredential cred = PlainProxy.init(slcs.getCertificate(),
					slcs.getPrivateKey(), 24 * 10);

			CommonGridProperties.getDefault().setLastShibUsername(username);
			CommonGridProperties.getDefault().setLastShibIdp(idp);
			return cred;

		} catch (final Exception e) {
			myLogger.debug("SLCS login: login failed: "
					+ e.getLocalizedMessage() + " - (id: " + id + ")");

			throw e;
		}

	}

}
