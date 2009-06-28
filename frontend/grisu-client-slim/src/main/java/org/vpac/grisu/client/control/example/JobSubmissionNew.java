package org.vpac.grisu.client.control.example;

import java.util.Set;
import java.util.UUID;

import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.ApplicationInformation;

public class JobSubmissionNew {
	
	public static void main(String[] args) throws Exception {
		
		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password);

		final ServiceInterface si = ServiceInterfaceFactory.createInterface(loginParams);
		
//		GrisuRegistry registry = GrisuRegistry.getDefault(si);
//		
//		ApplicationInformation javaInfo = registry.getApplicationInformation("java");
//		
//		Set<String> submissionLocations = javaInfo.getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin");
		
		JobObject job = new JobObject(si);
		job.setCommandline("java -version");
		
		job.createJob("/ARCS/VPAC");
		
		job.submitJob();
	
//		for ( final String subLoc : submissionLocations ) {
//		
//			new Thread() {
//				public void run() {
//
//					JobObject jo = new JobObject(si);
//				jo.setJobname("java_"+UUID.randomUUID());
////				jo.setApplication("java");
//				jo.setCommandline("java -version");  
//				jo.setSubmissionLocation(subLoc);
//				jo.addInputFileUrl("/home/markus/test.txt");
//			
//				try {
//					jo.createJob("/ARCS/NGAdmin");
//					jo.submitJob();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				}
//			}.start();
//		}
		
	}

}
