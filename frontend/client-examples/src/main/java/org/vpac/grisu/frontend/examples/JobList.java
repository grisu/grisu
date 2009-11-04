package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.dto.DtoMultiPartJob;

public final class JobList {
	
	private JobList() {
	}

	public static void main(final String[] args) throws ServiceInterfaceException, LoginException, NoSuchJobException {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
//				 "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
//				"http://localhost:8080/enunciate-backend/soap/GrisuService",
		"Local", 
		username, password);
		
		
		ServiceInterface si = null;
//		si = LoginManager.login(null, password, username, "VPAC", loginParams);
		si = LoginManager.login(null, null, null, null, loginParams);

		DtoJobs test = si.ps(null, true);
		
		System.out.println("ps");
		for ( DtoJob job : test.getAllJobs() ) {
			System.out.println(job.jobname());
		}
		
		System.out.println("alljobnames");
		for ( String name : si.getAllJobnames(null).asArray() ) {
			System.out.println(name);
		}
		
		System.out.println("all multipartjobnames");
		for ( String name : si.getAllMultiPartJobIds(null).asArray() ) {
			System.out.println(name);
		}
	}

}
