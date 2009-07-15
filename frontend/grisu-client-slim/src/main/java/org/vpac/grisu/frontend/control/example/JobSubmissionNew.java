package org.vpac.grisu.frontend.control.example;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobStatusChangeListener;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.info.ApplicationInformation;

public class JobSubmissionNew implements JobStatusChangeListener {

	public static void main(String[] args) throws Exception {

		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
				"http://localhost:8080/grisu-cxf/services/grisu",
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

		final GrisuRegistry registry = GrisuRegistry.getDefault(si);

		ApplicationInformation javaInfo = registry
				.getApplicationInformation("java");

		Set<String> submissionLocations = javaInfo
				.getAvailableSubmissionLocationsForFqan("/ARCS/StartUp");

//		 JobObject job = new JobObject(si);
//		 job.setJobname("marksssdduss2"+UUID.randomUUID());
//		 job.setCommandline("java -version");
//		 job.addInputFileUrl("/home/markus/test.txt");
//		 job.addInputFileUrl("gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grisu-local-job-dir/java_job_new/test.jsdl");
//		 job.createJob("/ARCS/VPAC");
//				
//		 job.submitJob();
//				
//		 job.adjustSleepTime(2);
//				
//		 job.addValueListener(new JobSubmissionNew());
//		 job.waitForJobToFinish();
		//		
		// System.out.println("Status for job "+job.getJobname()+": "+job.getStatusString(false));
		final JobStatusChangeListener jsl = new JobSubmissionNew();
		for (final String subLoc : submissionLocations) {

			Thread subThread = new Thread() {
				public void run() {

					JobObject jo = new JobObject(si);
					jo.setJobname("java_" + UUID.randomUUID());
					// jo.setApplication("java");
					jo.setCommandline("java -version");
					jo.setSubmissionLocation(subLoc);
					jo.addInputFileUrl("/home/markus/test.txt");
					jo.addInputFileUrl("gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grisu-local-job-dir/java_job_new/test.jsdl");
					jo.addJobStatusChangeListener(jsl);
					
					String site = registry.getResourceInformation().getSite(subLoc);
					System.out.println("Site is: "+site);
					if ( "tpac".equals(site.toLowerCase()) || "ac3".equals(site.toLowerCase()) || site.toLowerCase().contains("rses") ) {
						return;
					}
					try {
						jo.createJob("/ARCS/StartUp");
						jo.submitJob();
					} catch (Exception e) {
						System.err.println("Job to "+jo.getSubmissionLocation()+": "+e.getLocalizedMessage());
					}
				}

			};
			
			executor.execute(subThread);
		}
		
		executor.shutdown();
		System.out.println("Main thread finished.");

	}

	public void jobStatusChanged(JobObject job, int oldStatus, int newStatus) {
		System.out.println("JobSubmissionNew got job statusEvent: "
				+ job.getJobname() + " submitted to " + job.getSubmissionLocation() + ": "
				+ JobConstants.translateStatus(newStatus));
	}

}
