package org.vpac.grisu.examples;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;


public class Login {
	
	public static ServiceInterface getServiceInterfaceForUrl(String url, String username, char[] password) throws LoginException, ServiceInterfaceException {

		LoginParams params = new LoginParams(url, username, password, "myproxy2.arcs.org.au", "443");
		
		return ServiceInterfaceFactoryOld.createInterface(params);
		
	}
	
	public static ServiceInterface getServiceInterfaceForDevelopmentServer(String username, char[] password) throws LoginException, ServiceInterfaceException {

		LoginParams params = new LoginParams("https://ngportaldev.vpac.org/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
		
		return ServiceInterfaceFactoryOld.createInterface(params);
	}

	
	public static ServiceInterface getServiceInterfaceForDevelopmentServer2(String username, char[] password) throws LoginException, ServiceInterfaceException {

		LoginParams params = new LoginParams("http://localhost:8080/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
		
		return ServiceInterfaceFactoryOld.createInterface(params);
	}

	
	public static ServiceInterface getServiceInterfaceForProductionServer(String username, char[] password) throws LoginException, ServiceInterfaceException {

		LoginParams params = new LoginParams("https://grisu.vpac.org/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
		
		return ServiceInterfaceFactoryOld.createInterface(params);
	}

	public static EnvironmentManager loginDevelopmentServer2(String username, char[] password) throws LoginException, ServiceInterfaceException {

		   EnvironmentManager em = null;

		   // creating an object which holds all the login information. For this example we assume we always use the specified grisu service url and 
		   // myproxy server/port. It's possible to also set a httproxy here.
		   LoginParams loginparams = new LoginParams("http://localhost:8080/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
					
	     // do the login
	     em = ServiceInterfaceFactoryOld.login(loginparams);

	     return em;
	}
	
	public static EnvironmentManager loginDevelopmentServer(String username, char[] password) throws LoginException, ServiceInterfaceException {

		   EnvironmentManager em = null;

		   // creating an object which holds all the login information. For this example we assume we always use the specified grisu service url and 
		   // myproxy server/port. It's possible to also set a httproxy here.
		   LoginParams loginparams = new LoginParams("https://ngportaldev.vpac.org/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
					
	     // do the login
	     em = ServiceInterfaceFactoryOld.login(loginparams);

	     return em;
	}
	
	public static EnvironmentManager login(String username, char[] password) throws LoginException, ServiceInterfaceException {

		   EnvironmentManager em = null;

		   // creating an object which holds all the login information. For this example we assume we always use the specified grisu service url and 
		   // myproxy server/port. It's possible to also set a httproxy here.
		   LoginParams loginparams = new LoginParams("https://grisu.vpac.org/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
					
	     // do the login
	     em = ServiceInterfaceFactoryOld.login(loginparams);

	     return em;
	}
	
	public static EnvironmentManager loginLocalhost(String username, char[] password) throws LoginException, ServiceInterfaceException {

		   EnvironmentManager em = null;

		   // creating an object which holds all the login information. For this example we assume we always use the specified grisu service url and 
		   // myproxy server/port. It's possible to also set a httproxy here.
		   LoginParams loginparams = new LoginParams("http://localhost:8080/grisu-ws/services/grisu", username, password, "myproxy2.arcs.org.au", "443");
					
	     // do the login
	     em = ServiceInterfaceFactoryOld.login(loginparams);

	     return em;
	}

}
