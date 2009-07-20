package org.vpac.grisu.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.enunciate.modules.spring_app.HTTPRequestContext;
import org.globus.myproxy.MyProxy;
import org.ietf.jgss.GSSCredential;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.grisu.settings.ServerPropertiesManager;

public class GrisuUserDetailsImpl implements UserDetailsService {
	
	static final Logger myLogger = Logger.getLogger(GrisuUserDetailsImpl.class
			.getName());

	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException, DataAccessException {

		System.out.println("AUTH!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		HttpServletRequest req = HTTPRequestContext.get().getRequest();
		String auth_head    = req.getHeader("authorization");
        if (auth_head != null && auth_head.startsWith("Basic")) {
            String usernpass    = new String(org.apache.commons.codec.binary.Base64.decodeBase64((auth_head.substring(6).getBytes())));
            String user         = usernpass.substring(0, usernpass.indexOf(":"));
            String password     = usernpass.substring(usernpass.indexOf(":") + 1);

            ProxyCredential proxy = createProxyCredential(user, password, 
            		MyProxyServerParams.DEFAULT_MYPROXY_SERVER, MyProxyServerParams.DEFAULT_MYPROXY_PORT, 
            		ServerPropertiesManager.getMyProxyLifetime());
            
            boolean success = true;

            if ( proxy == null || ! proxy.isValid() ) {
            	success = false;
            }
            
            req.getSession().setAttribute("credential", proxy);
            
            System.out.println("AUTH successful!!!!!!!!!!!!!!!!!!!!!");
    		
    		return new GrisuUserDetails(user, password, success);
    	} else {
    		return null;
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
	
}
