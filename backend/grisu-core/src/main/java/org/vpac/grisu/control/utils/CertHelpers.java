package org.vpac.grisu.control.utils;

import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.ptls.PureTLSUtil;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.vpac.grisu.control.FqanHelpers;
import org.vpac.grisu.control.exceptions.VomsException;
import org.vpac.grisu.credential.model.ProxyCredential;
import org.vpac.security.light.CredentialHelpers;
import org.vpac.security.light.voms.VO;
import org.vpac.security.light.vomsProxy.VomsProxyCredential;

/**
 * Helper class that does stuff with certificates
 * 
 * @author Markus Binsteiner
 *
 */
public class CertHelpers {
	
	static final Logger myLogger = Logger
	.getLogger(CertHelpers.class.getName());

	/**
	 * Converts the dn from the /C=AU/O=... to the C=AU,O=.. format
	 * @param cred the credential as {@link GSSCredential}
	 * @return the dn of this certificate in the proper format
	 */
	public static String getDnInProperFormat(GSSCredential cred) {
		String dn = null;
		try {
			dn = PureTLSUtil.getX509Name(cred.getName().toString())
					.getNameString();
		} catch (GSSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dn;
	}

	/**
	 * Converts the dn from the /C=AU/O=... to the C=AU,O=.. format
	 * @param cred the credential as {@link GlobusCredential}
	 * @return the dn of this certificate in the proper format
	 */
	public static String getDnInProperFromat(GlobusCredential cred) {
		String dn = null;

		try {
			dn = PureTLSUtil.getX509Name(cred.getSubject()).getNameString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dn;

	}
	
	/**
	 * This one uses the proxy_light library to create a voms proxy using a plain proxy. 
	 * 
	 * @param vo the vo to connect to to get the voms proxy
	 * @param fqan the fqan which the newly created proxy should have 
	 * @param credToConnect the plain proxy
	 * @return the voms proxy
	 * @throws VomsException if the communication with the voms server fails for some reason
	 */
	public static ProxyCredential getVOProxyCredential(VO vo, String fqan, ProxyCredential credToConnect) throws VomsException {
		
		VomsProxyCredential vomsGssCred = null;
		try {
			String group = FqanHelpers.getGroupPart(fqan);
			String role = FqanHelpers.getRolePart(fqan);
			if ( role == null || "NULL".equals(role) ) {
				vomsGssCred = new VomsProxyCredential(CredentialHelpers.unwrapGlobusCredential(credToConnect.getGssCredential()), vo, "G"+group, null);
			} else {
				vomsGssCred = new VomsProxyCredential(CredentialHelpers.unwrapGlobusCredential(credToConnect.getGssCredential()), vo, "B"+group+":"+role, null);
			}
			myLogger.debug("Created voms proxy for fqan: "+fqan+" with lifetime: "+vomsGssCred.getVomsProxy().getTimeLeft());
			
		} catch (Exception e) {
			throw new VomsException("Could not retrieve VomsProxyCredential for fqan \""+fqan+"\": "+e.getMessage());
		}
		
		ProxyCredential vomsProxyCred = null;
		try {
			vomsProxyCred = new ProxyCredential(CredentialHelpers.wrapGlobusCredential(vomsGssCred.getVomsProxy()), fqan);
		} catch (Exception e) {
			throw new VomsException("Could not retrieve VomsProxyCredential for fqan \""+fqan+"\": "+e.getMessage());
		}
		
		return vomsProxyCred;
	}

}
