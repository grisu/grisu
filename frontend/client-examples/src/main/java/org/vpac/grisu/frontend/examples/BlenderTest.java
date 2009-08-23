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

public class BlenderTest implements JobStatusChangeListener {

	public static void main(final String[] args) throws Exception {


		ExecutorService executor = Executors.newFixedThreadPool(10);

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
//				 "https://ngportal.vpac.org/grisu-ws/services/grisu",
				 "Local",
//				"Dummy",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);


		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		ApplicationInformation blenderInfo = registry
				.getApplicationInformation("blender");
		Set<String> versions = blenderInfo.getAllAvailableVersionsForFqan("/ARCS/NGAdmin");

		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		SortedSet<GridResource> resources = blenderInfo.getBestSubmissionLocations(jobProperties, "/ARCS/NGAdmin");
		
		final String subLoc = SubmissionLocationHelpers.createSubmissionLocationString(resources.first());
		
		Date start = new Date();
		final String multiJobName = "PerformanceTest4";
		try {
			si.deleteMultiPartJob(multiJobName, true);
		} catch (Exception e) {
			// doesn't matter
		}
		
		System.out.println("Start: "+start.toString());
		System.out.println("End: "+new Date().toString());
		
		System.exit(1);
		MultiPartJobObject multiPartJob = new MultiPartJobObject(si, multiJobName, "/ARCS/NGAdmin");
				
//		multiPartJob.setConcurrentJobCreationThreads(3);
		
		for (int i=1; i<1001; i++) {

			final int frameNumber = i;
				
				JobObject jo = new JobObject(si);
				jo.setJobname(multiJobName+"_" + frameNumber );
				jo.setApplication("blender");
//				jo.setCommandline("blender -b "+multiPartJob.pathToInputFiles()+"/CubesTest.blend -F PNG -o cubes_ -f "+frameNumber);
				jo.setCommandline("echo hello");
				jo.setSubmissionLocation(subLoc);
				jo.setModules(new String[]{"blender/2.49"});
				jo.setWalltimeInSeconds(100);
				jo.setCpus(1);
				multiPartJob.addJob(jo);
						
		}

		multiPartJob.addInputFile("/home/markus/Desktop/CubesTest.blend");
		
		try {
			multiPartJob.prepareAndCreateJobs();
		} catch (JobsException e) {
			for ( JobObject job : e.getFailures().keySet() ) {
				System.out.println("Creation "+job.getJobname()+" failed: "+e.getFailures().get(job).getLocalizedMessage());
			}
			System.exit(1);
		}
		
		multiPartJob.submit(false);
		

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
