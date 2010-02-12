package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.JobListDialog;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class SimpleJobSubmission {

	public static void main(String[] args) throws Exception {

		//		ServiceInterface si = LoginManager.loginCommandline();
		ServiceInterface si = LoginManager.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		for ( String subLoc : registry.getResourceInformation().getAllSubmissionLocations() ) {
			System.out.println(subLoc);
		}

		JobListDialog.open(si, null);

		JobObject job = new JobObject(si);
		job.setApplication("java");
		job.setApplicationVersion("1.6.0-06");
		job.setTimestampJobname("java_job2");
		job.setCommandline("java -version");

		job.setWalltimeInSeconds(60);

		job.createJob("/ARCS/NGAdmin");
		job.submitJob();


		System.out.println("Main thread finished.");
	}

}
