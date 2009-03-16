

package org.vpac.grisu.view.ws.login;


import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.vpac.grisu.credential.model.ProxyCredential;

/**
 * Not needed anymore.
 * 
 * @author Markus Binsteiner
 *
 */
public class Login {
	
//	/**
//	 * Checks whether 
//	 * @param proxy
//	 */
//	public static void checkProxy(){
//		
//		GSSCredential proxy = ((GSSCredential)MessageContextHelper.getContext().getContextualProperty("gsscredential"));
//		try {
//			if ( proxy == null || proxy.getRemainingLifetime() <= 0 )
//				throw new RuntimeException("No valid proxy for this request!");
//		} catch (GSSException e1) {
//			throw new RuntimeException("No valid proxy for this request!");
//		}		
//	}
//	
//	public static String getDN() throws Exception {
//		return getCredential().getDn();	
//	}
//	
//	public static String getCaDN() throws Exception {
//		
//		GSSCredential cred = ((GSSCredential)MessageContextHelper.getContext().getContextualProperty("gsscredential"));
//		//String cadn = cred.getName(mech);
//		
//		return null;
//	}
//	
//	public static ProxyCredential getCredential(){
//		GSSCredential cred = ((GSSCredential)MessageContextHelper.getContext().getContextualProperty("gsscredential"));
//		ProxyCredential proxyCred = null;
//		try {
//			proxyCred = new ProxyCredential(cred);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return proxyCred;
//	}
	


}
