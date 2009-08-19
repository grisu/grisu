package org.vpac.grisu.frontend.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

public class Resubmit {

	public static void main(final String[] args) throws Exception {

		ExecutorService executor = Executors.newFixedThreadPool(1);

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				 "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);


		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

//		System.out.println(si.getJsdlDocument("MULTI2_58"));
		
		JobObject jobO = new JobObject(si, "MULTI2_58");
		jobO.setWalltimeInSeconds(2400);
		jobO.setCpus(1);
		
//		System.out.println(jobO.getJobDescriptionDocumentAsString());
		
		jobO.restartJob();
		
		si.restartJob("MULTI4_1", null);
	}

}
