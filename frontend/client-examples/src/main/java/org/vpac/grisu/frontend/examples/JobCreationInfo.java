package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

public final class JobCreationInfo {
	
	private JobCreationInfo() {
	}

	public static void main(final String[] args) throws ServiceInterfaceException, LoginException {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", null, null);
		
		
		ServiceInterface si = null;
		si = LoginManager.login(null, password, username, "VPAC", loginParams);

//		DtoJobs test = si.ps(true);

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		
		for ( String subLoc : registry.getUserEnvironmentManager().getAllAvailableSubmissionLocations() ) {
			System.out.println(subLoc);
		}

		ApplicationInformation appInfo = registry
				.getApplicationInformation("java");
		for (String version : appInfo
				.getAllAvailableVersionsForFqan("/ARCS/NGAdmin")) {
			System.out.println(version);
		}

	}

}
