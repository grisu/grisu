

package org.vpac.grisu.view.ws.login;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.globus.myproxy.MyProxy;
import org.ietf.jgss.GSSCredential;

/**
 * Not needed anymore.
 * 
 * This one takes the username & password from the XML WS-Security context
 * and tries to get a myproxy credential with it. If it succeeds it puts the 
 * credential into the context (with the key "gsscredential")
 * 
 * @author Markus Binsteiner
 *
 */
public class ValidateMyProxyHandler extends AbstractHandler {

	public void invoke(MessageContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
//	public static Logger myLogger = Logger.getLogger(ValidateMyProxyHandler.class.getName());
//	
//	//TODO check for security implications
//	private static Map<String, Date> cache = new HashMap<String, Date>();
//	
//	public void invoke(MessageContext context) throws Exception {
//		
//		// get the security handler instance WSHandlerResult
//		Vector result = (Vector) context
//				.getProperty(WSHandlerConstants.RECV_RESULTS);
//		if ( result == null || result.size() == 0 )
//			throw new RuntimeException("No security information in the header.");
//		
//		// get the WSSecurityEngineResult's
//		for (int i = 0; i < result.size(); i++) {
//			WSHandlerResult res = (WSHandlerResult) result.get(i);
//			// there should be only one result
//			for (int j = 0; j < res.getResults().size(); j++) {
//				WSSecurityEngineResult secRes = (WSSecurityEngineResult) res.getResults().get(j);
//				int action = secRes.getAction();
//				// USER TOKEN
//				if ((action & WSConstants.UT) > 0) {
//					WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) secRes
//							.getPrincipal();
//					
//					// check whether proxy is already downloaded...
//					if ( cache.get(principal.getName()) != null && cache.get(principal.getName()).before(new Date())) {
//						return;
//					}
//					
//					// Set user property to user from UT to allow response
//					// encryption
//					context.setProperty(WSHandlerConstants.ENCRYPTION_USER,
//							principal.getName());
//					myLogger.debug("User : " + principal.getName());
//					
//					// now, try to get the proxy
//					MyProxy myproxy = new MyProxy("myproxy.apac.edu.au", 7512);
//					GSSCredential proxy = null;
//
//					proxy = myproxy.get(principal.getName(), principal
//							.getPassword(), 3600);
//
//					int remaining = proxy.getRemainingLifetime();
//					
//					if ( remaining <= 0 )
//						throw new RuntimeException("Proxy not valid anymore.");
//					
//					// put the credential into the proxy
//					context.setProperty("gsscredential", proxy);
//					
//					//put the context in a ThreadLocal so you can access
//					//it everytime in the session without having to change
//					//the interface of the webservice
//					MessageContextHelper.setContext(context);
//					
//					cache.put(principal.getName(), new Date());
//					
//				}
//			}
//		}
//	}
}
