package org.vpac.grisu.frontend.examples;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobStatusChangeListener;
import org.vpac.grisu.frontend.model.job.JobsException;
import org.vpac.grisu.frontend.model.job.MultiPartJobEventListener;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

import au.org.arcs.jcommons.constants.Constants;

public class MultiJobSubmit implements JobStatusChangeListener, MultiPartJobEventListener {

	public static void main(final String[] args) throws Exception {


		MultiJobSubmit mjs = new MultiJobSubmit();
		
		
		ExecutorService executor = Executors.newFixedThreadPool(10);

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
//				 "https://ngportal.vpac.org/grisu-ws/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
//				"http://localhost:8080/enunciate-backend/soap/GrisuService",
				 "Local",
//				"Dummy",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);


		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		final int numberOfJobs = 400;
		
		Date start = new Date();
		final String multiJobName = "400jobs";
		try {
			si.deleteMultiPartJob(multiJobName, true);
		} catch (Exception e) {
			// doesn't matter
			e.printStackTrace();
		}
		
		
		System.out.println("Start: "+start.toString());
		System.out.println("End: "+new Date().toString());
		System.exit(1);

		MultiPartJobObject multiPartJob = new MultiPartJobObject(si, multiJobName, "/ARCS/NGAdmin", Constants.GENERIC_APPLICATION_NAME, Constants.NO_VERSION_INDICATOR_STRING);
				
		multiPartJob.addJobStatusChangeListener(mjs);
//		multiPartJob.setConcurrentJobCreationThreads(3);
		
		for (int i=0; i<numberOfJobs; i++) {

			final int frameNumber = i;
				
				JobObject jo = new JobObject(si);
				jo.setJobname(multiJobName+"_" + frameNumber );
//				jo.setApplication("java");
//				jo.setCommandline("java -version");
				jo.setCommandline("echo hello world");
				jo.setWalltimeInSeconds(60*21);

				multiPartJob.addJob(jo);
						
		}

//		multiPartJob.setConcurrentJobCreationThreads(1);
		multiPartJob.setDefaultApplication("generic");
		
//		multiPartJob.setSitesToExclude(new String[]{"vpac", "massey", "uq", "canterbury", "sapac", "ivec", "otago"});
		
		multiPartJob.setDefaultNoCpus(1);
		multiPartJob.setDefaultWalltimeInSeconds(60*21);
		
		multiPartJob.setSitesToExclude(new String[]{"otago"});
		
		multiPartJob.fillOrOverwriteSubmissionLocationsUsingMatchmaker();
		
		try {
			multiPartJob.prepareAndCreateJobs();
		} catch (JobsException e) {
			for ( JobObject job : e.getFailures().keySet() ) {
				System.out.println("Creation "+job.getJobname()+" failed: "+e.getFailures().get(job).getLocalizedMessage());
			}
			System.exit(1);
		}
		
		multiPartJob.submit();
		

		System.out.println("Submission finished: "+new Date());
		
//		if ( HibernateSessionFactory.HSQLDB_DBTYPE.equals(HibernateSessionFactory.usedDatabase) ) {
//			// for hqsqldb
//			Thread.sleep(10000);
//		}
		
//		MultiPartJobObject newObject = new MultiPartJobObject(si, multiJobName);
//		
//		newObject.monitorProgress();
//		
//		newObject.downloadResults("logo");

		
	}

	public final void jobStatusChanged(final JobObject job, final int oldStatus, final int newStatus) {
		System.out.println("JobSubmissionNew got job statusEvent: "
				+ job.getJobname() + " submitted to "
				+ job.getSubmissionLocation() + ": "
				+ JobConstants.translateStatus(newStatus));
	}

	public void eventOccured(MultiPartJobObject job, String eventMessage) {

		System.out.println(eventMessage);
	}

}
