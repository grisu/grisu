package org.vpac.grisu.control;

import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

public interface ServiceInterfaceCreator {
	
	public ServiceInterface create(String interfaceUrl,
			String username, char[] password, String myProxyServer, String myProxyPort, Object[] otherOptions) throws ServiceInterfaceException;

	public boolean canHandleUrl(String url);
	
}
