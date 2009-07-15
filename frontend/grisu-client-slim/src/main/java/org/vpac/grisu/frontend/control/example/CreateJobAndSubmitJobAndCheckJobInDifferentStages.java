package org.vpac.grisu.frontend.control.example;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginHelpers;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.utils.SeveralXMLHelpers;

public class CreateJobAndSubmitJobAndCheckJobInDifferentStages {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginHelpers.login();
		
		JobObject createJobObject = new JobObject(si);
		
		createJobObject.setApplication("Java");
		createJobObject.setCommandline("java -version");
		createJobObject.setWalltimeInSeconds(3600*24*40);
		createJobObject.setCpus(1);
		createJobObject.addInputFileUrl("/home/markus/test.txt");
		createJobObject.addInputFileUrl("gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grix_splash_v1.1.jpg");
		
//		GrisuRegistry registry = GrisuRegistry.getDefault(si);
//		System.out.println(StringUtils.join(registry.getApplicationInformation("java").getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin"),"\n"));
		
		System.out.println(SeveralXMLHelpers.toStringWithoutAnnoyingExceptions(createJobObject.getJobDescriptionDocument()));
		
		String newJobname = createJobObject.createJob("/ARCS/NGAdmin", ServiceInterface.TIMESTAMP_METHOD);
		
		JobObject submitJobObject = new JobObject(si, newJobname);
		
		System.out.println("Application: "+submitJobObject.getApplication());
		
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
		
		if ( ! finished ) {
			System.out.println("not finished yet.");
			checkJobObject.kill(true);
		} else {
			System.out.println("Stdout: "+checkJobObject.getStdOutContent());
			System.out.println("Stderr: "+checkJobObject.getStdErrContent());
			checkJobObject.kill(true);
		}		
		
		
	}

}
