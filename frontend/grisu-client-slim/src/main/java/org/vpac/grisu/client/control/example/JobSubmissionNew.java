package org.vpac.grisu.client.control.example;

import java.util.Set;
import java.util.UUID;

import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.client.model.JobStatusChangeListener;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.js.model.utils.SubmissionLocationHelpers;
import org.vpac.grisu.model.ApplicationInformation;

public class JobSubmissionNew implements JobStatusChangeListener {

	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
				"http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
//				 "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		// si.cp("gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/MyEclipse_6.0GA_E3.3_Installer.exe",
		// "gsiftp://ng2.vpac.org/home/grid-mongeo/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/testEclipse",
		// true, false);

		// si.cp("gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grix_splash_v1.1.jpg",
		// "gsiftp://ng2.vpac.org/home/grid-mongeo/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/test",
		// true, false);

		System.out.println("Main thread finished.");

		final GrisuRegistry registry = GrisuRegistry.getDefault(si);

		ApplicationInformation javaInfo = registry
				.getApplicationInformation("java");

		Set<String> submissionLocations = javaInfo
				.getAvailableSubmissionLocationsForFqan("/ARCS/StartUp");

		// JobObject job = new JobObject(si);
		// job.setJobname("marksssddus2");
		// job.setCommandline("java -version");
		//		
		// job.createJob("/ARCS/VPAC");
		//		
		// job.submitJob();
		//		
		// job.adjustSleepTime(2);
		//		
		// job.addValueListener(new JobSubmissionNew());
		// job.waitForJobToFinish();
		//		
		// System.out.println("Status for job "+job.getJobname()+": "+job.getStatusString(false));
		final JobStatusChangeListener jsl = new JobSubmissionNew();
		for (final String subLoc : submissionLocations) {

//			new Thread() {
//				public void run() {

					JobObject jo = new JobObject(si);
					jo.setJobname("java_" + UUID.randomUUID());
					// jo.setApplication("java");
					jo.setCommandline("java -version");
					jo.setSubmissionLocation(subLoc);
					jo.addInputFileUrl("/home/markus/test.txt");
					jo.addValueListener(jsl);
					
					String site = registry.getResourceInformation().getSite(subLoc);
					System.out.println("Site is: "+site);
					if ( "tpac".equals(site.toLowerCase()) || "ac3".equals(site.toLowerCase()) || site.toLowerCase().contains("rses") ) {
						continue;
					}
					try {
						jo.createJob("/ARCS/StartUp");
						jo.submitJob();
					} catch (Exception e) {
						System.err.println("Job to "+jo.getSubmissionLocation()+": "+e.getLocalizedMessage());
					}
					//jo.waitForJobToFinish();
				}

//			}.start();
//		}

	}

	public void jobStatusChanged(JobObject job, int oldStatus, int newStatus) {
		System.out.println("\n\n\nJobSubmissionNew got job statusEvent: "
				+ job.getJobname() + " " + job.getSubmissionLocation() + ": "
				+ JobConstants.translateStatus(newStatus) + "\n\n\n");
	}

}
