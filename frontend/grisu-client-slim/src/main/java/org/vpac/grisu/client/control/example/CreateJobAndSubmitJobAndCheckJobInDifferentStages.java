package org.vpac.grisu.client.control.example;

import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.ServiceInterface;

public class CreateJobAndSubmitJobAndCheckJobInDifferentStages {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginHelpers.login();
		
		JobObject createJobObject = new JobObject(si);
		
		createJobObject.setCommandline("java -version");
		
		String newJobname = createJobObject.createJob("/ARCS/NGAdmin", ServiceInterface.TIMESTAMP_METHOD);
		
		
		
		JobObject submitJobObject = new JobObject(si, newJobname);
		
		System.out.println("Application: "+submitJobObject.getApplication());
		
		submitJobObject.submitJob();
		
		Thread.sleep(5000);
		
		JobObject checkJobObject = new JobObject(si, newJobname);
		
		System.out.println("Status: "+checkJobObject.getStatus(true));
		
		
		
		
	}

}
