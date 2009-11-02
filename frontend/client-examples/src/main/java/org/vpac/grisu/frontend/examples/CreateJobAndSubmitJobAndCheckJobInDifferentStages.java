package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.utils.SeveralXMLHelpers;

import au.org.arcs.jcommons.constants.Constants;

public final class CreateJobAndSubmitJobAndCheckJobInDifferentStages {

	private CreateJobAndSubmitJobAndCheckJobInDifferentStages() {
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		
		LoginParams loginParams = new LoginParams(
				// "http://localhost:8080/grisu-ws/services/grisu",
//						 "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
//				"http://localhost:8080/enunciate-backend/soap/GrisuService",
				"http://localhost:8080/soap/GrisuService",
//				"Local", 
				args[0], args[1].toCharArray());

		ServiceInterface si = LoginManager.login(null, null, null, null, loginParams);

		JobObject createJobObject = new JobObject(si);

		createJobObject.setApplication("Java");
		createJobObject.setCommandline("java -version");
		createJobObject.setWalltimeInSeconds(3600 * 24 * 40);
		createJobObject.setCpus(1);
		createJobObject.addInputFileUrl("/home/markus/test.txt");
		createJobObject
				.addInputFileUrl("gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grix_splash_v1.1.jpg");

		// GrisuRegistry registry = GrisuRegistry.getDefault(si);
		// System.out.println(StringUtils.join(registry.getApplicationInformation("java").getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin"),"\n"));

		System.out.println(SeveralXMLHelpers
				.toStringWithoutAnnoyingExceptions(createJobObject
						.getJobDescriptionDocument()));

		String newJobname = createJobObject.createJob("/ARCS/NGAdmin",
				Constants.TIMESTAMP_METHOD);

		JobObject submitJobObject = new JobObject(si, newJobname);

		System.out.println("Application: " + submitJobObject.getApplication());

		submitJobObject.submitJob();

		final JobObject checkJobObject = new JobObject(si, newJobname);

		new Thread() {
			public void run() {
				try {
					Thread.sleep(20000);
					System.out.println("Sleeping over.");
					checkJobObject.stopWaitingForJobToFinish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		boolean finished = checkJobObject.waitForJobToFinish(3);

		if (!finished) {
			System.out.println("not finished yet.");
			checkJobObject.kill(true);
		} else {
			System.out.println("Stdout: " + checkJobObject.getStdOutContent());
			System.out.println("Stderr: " + checkJobObject.getStdErrContent());
			checkJobObject.kill(true);
		}

	}

}
