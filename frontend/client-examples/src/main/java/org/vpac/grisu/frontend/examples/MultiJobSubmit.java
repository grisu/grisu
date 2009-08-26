package org.vpac.grisu.frontend.examples;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vpac.grisu.backend.hibernate.HibernateSessionFactory;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobStatusChangeListener;
import org.vpac.grisu.frontend.model.job.JobsException;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class MultiJobSubmit implements JobStatusChangeListener {

	public static void main(final String[] args) throws Exception {


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

		final int numberOfJobs = 100;
		
		Date start = new Date();
		final String multiJobName = "PerformanceTest7";
		try {
			si.deleteMultiPartJob(multiJobName, true);
		} catch (Exception e) {
			// doesn't matter
			e.printStackTrace();
		}
		
		System.out.println("Start: "+start.toString());
		System.out.println("End: "+new Date().toString());
		
//		System.exit(1);
		MultiPartJobObject multiPartJob = new MultiPartJobObject(si, multiJobName, "/ARCS/NGAdmin");
				
//		multiPartJob.setConcurrentJobCreationThreads(3);
		
		for (int i=0; i<numberOfJobs; i++) {

			final int frameNumber = i;
				
				JobObject jo = new JobObject(si);
				jo.setJobname(multiJobName+"_" + frameNumber );
//				jo.setApplication("java");
				jo.setCommandline("java -version");

				multiPartJob.addJob(jo);
						
		}

		multiPartJob.setDefaultApplication("java");
		
		multiPartJob.setDefaultNoCpus(1);
		multiPartJob.setDefaultWalltimeInSeconds(60*21);
		
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
		

		System.out.println("Submission finished...");
		
		if ( HibernateSessionFactory.HSQLDB_DBTYPE.equals(HibernateSessionFactory.usedDatabase) ) {
			// for hqsqldb
			Thread.sleep(10000);
		}
		
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

}