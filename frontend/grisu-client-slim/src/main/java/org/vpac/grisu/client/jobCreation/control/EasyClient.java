package org.vpac.grisu.client.jobCreation.control;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.js.model.JobPropertiesException;
import org.vpac.grisu.js.model.JobSubmissionObjectImpl;

public class EasyClient {

	static final Logger myLogger = Logger
	.getLogger(EasyClient.class.getName());
	
	/**
	 * @param args
	 * @throws LoginException 
	 */
	public static void main(String[] args) throws LoginException {

		String username = args[0];

		char[] password = args[1].toCharArray();

		myLogger.info("Logging in...");
		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password, "myproxy2.arcs.org.au", "7512");

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
			si.login(username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LoginException(e.getLocalizedMessage());
		}
		
		try {
			si.kill("testJob", true);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		
//		try {
//			si.createJob("testJob", JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME);
//		} catch (JobCreationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		JobSubmissionObjectImpl jso = new JobSubmissionObjectImpl();
		jso.setJobname("testJob");
		jso.setApplication("Java");
		jso.setApplicationVersion("jdk-1.6.0_04-fcs");
		jso.setCommandline("java -version");
		jso.setCpus(1);
		jso.setWalltime(400);
		jso.setEmail_address("testEmailAddress");
		jso.setEmail_on_job_start(false);
		jso.setEmail_on_job_finish(false);
		jso.setForce_mpi(false);
		jso.setForce_single(true);
//		jso.setInputFileUrls(new String[]{ "file:///temp/test.txt", "gsiftp://ng2.vpac.org/tmp/test"});
		jso.setMemory(0);

		try {
			si.createJob(jso.getJobDescriptionDocument(), "/ARCS/NGAdmin", "force-name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			si.submitJob("testJob");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
