package org.vpac.grisu.client.control.example;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;

public class CreateJobAndSubmitJobAndCheckJobInDifferentStages {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginHelpers.login();
		
		JobObject createJobObject = new JobObject(si);
		
		createJobObject.setCommandline("java -version");
		createJobObject.setSubmissionLocation("sque@tango-m:ng2.vpac.org");
		createJobObject.setWalltimeInSeconds(10);
		
		GrisuRegistry registry = GrisuRegistry.getDefault(si);
		System.out.println(StringUtils.join(registry.getApplicationInformation("java").getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin"),"\n"));
		
		String newJobname = createJobObject.createJob("/ARCS/NGAdmin", ServiceInterface.TIMESTAMP_METHOD);
		
		JobObject submitJobObject = new JobObject(si, newJobname);
		
		System.out.println("Application: "+submitJobObject.getApplication());
		
		submitJobObject.submitJob();
		
		
		JobObject checkJobObject = new JobObject(si, newJobname);
		
		while ( ! checkJobObject.isFinished() ) {
			System.out.println("Status: "+checkJobObject.getStatusString(false));
			Thread.sleep(6000);
		}

		
		System.out.println("Stdout: "+checkJobObject.getStdOutContent());
		System.out.println("Stderr: "+checkJobObject.getStdErrContent());
		
		
		
		
	}

}
