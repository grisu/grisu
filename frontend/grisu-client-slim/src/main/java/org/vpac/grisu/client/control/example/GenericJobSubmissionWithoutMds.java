package org.vpac.grisu.client.control.example;

import java.util.UUID;

import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;

public class GenericJobSubmissionWithoutMds {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				 "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);


		final GrisuRegistry registry = GrisuRegistry.getDefault(si);


		 JobObject job = new JobObject(si);
		 job.setApplication(ServiceInterface.GENERIC_APPLICATION_NAME);
		 job.setJobname("generic"+UUID.randomUUID());
		 job.setCommandline("java -version");
		 job.addInputFileUrl("/home/markus/test.txt");
		 job.setSubmissionLocation("dque@brecca-m:ng2.vpac.monash.edu.au");
		 job.addModule("java");
		 job.createJob("/ARCS/VPAC");
				
		 job.submitJob();
				

		 System.out.println("Main thread finished.");
	}

}
