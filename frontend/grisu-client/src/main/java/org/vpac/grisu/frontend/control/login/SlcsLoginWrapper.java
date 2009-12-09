package org.vpac.grisu.frontend.control.login;

import org.ietf.jgss.GSSCredential;
import org.vpac.security.light.plainProxy.PlainProxy;

import au.org.arcs.auth.shibboleth.CredentialManager;
import au.org.arcs.auth.shibboleth.IdpObject;
import au.org.arcs.auth.shibboleth.StaticCredentialManager;
import au.org.arcs.auth.shibboleth.StaticIdpObject;
import au.org.arcs.auth.slcs.SLCS;
import au.org.arcs.jcommons.configuration.CommonArcsProperties;

public class SlcsLoginWrapper {
	
	public static GSSCredential slcsMyProxyInit(String username,
			char[] password, String idp) throws Exception {

		try {

			IdpObject idpO = new StaticIdpObject(idp);
			CredentialManager cm = new StaticCredentialManager(username, password);
			SLCS slcs = new SLCS(SLCS.DEFAULT_SLCS_URL, idpO, cm);
			
			GSSCredential cred = PlainProxy.init(slcs.getCertificate(), slcs.getPrivateKey(), 24 * 10);
			
			CommonArcsProperties.getDefault().setLastShibUsername(username);
			CommonArcsProperties.getDefault().setLastShibIdp(idp);
			return cred;


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
