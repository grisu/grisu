package grisu.frontend.control.login;

import grith.gsindl.SLCS;
import grith.jgrith.plainProxy.PlainProxy;

import org.apache.commons.lang.StringUtils;
import org.ietf.jgss.GSSCredential;

import au.org.arcs.auth.shibboleth.CredentialManager;
import au.org.arcs.auth.shibboleth.IdpObject;
import au.org.arcs.auth.shibboleth.Shibboleth;
import au.org.arcs.auth.shibboleth.StaticCredentialManager;
import au.org.arcs.auth.shibboleth.StaticIdpObject;
import au.org.arcs.jcommons.configuration.CommonArcsProperties;

public class SlcsLoginWrapper {

	public static GSSCredential slcsMyProxyInit(String username,
			char[] password, String idp, LoginParams params) throws Exception {

		try {

			if (params != null) {
				final String httproxy = params.getHttpProxy();
				final int httpProxyPort = params.getHttpProxyPort();

				if (StringUtils.isNotBlank(httproxy)) {
					Shibboleth.setHttpProxy(httproxy, httpProxyPort,
							params.getHttpProxyUsername(),
							params.getMyProxyPassphrase());
				}
			}

			final IdpObject idpO = new StaticIdpObject(idp);
			final CredentialManager cm = new StaticCredentialManager(username,
					password);
			final SLCS slcs = new SLCS(SLCS.DEFAULT_SLCS_URL, idpO, cm);

			final GSSCredential cred = PlainProxy.init(slcs.getCertificate(),
					slcs.getPrivateKey(), 24 * 10);

			CommonArcsProperties.getDefault().setLastShibUsername(username);
			CommonArcsProperties.getDefault().setLastShibIdp(idp);
			return cred;

		} catch (final Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
