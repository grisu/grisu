package org.vpac.grisu.frontend.examples;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobStatusChangeListener;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoMultiPartJob;
import org.vpac.grisu.model.info.ApplicationInformation;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class BlenderTest implements JobStatusChangeListener {

	public static void main(final String[] args) throws Exception {

		ExecutorService executor = Executors.newFixedThreadPool(1);

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				 "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);


		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		ApplicationInformation blenderInfo = registry
				.getApplicationInformation("blender");

//		Set<String> submissionLocations = javaInfo
//				.getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin");
		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		SortedSet<GridResource> resources = blenderInfo.getBestSubmissionLocations(jobProperties, "/ARCS/NGAdmin");
		
		final String subLoc = SubmissionLocationHelpers.createSubmissionLocationString(resources.first());
		
//		registry.getFileManager().uploadFile(new File("/home/markus/Desktop/VPAC_logo.blend"));
		DataHandler dh = new DataHandler(new FileDataSource(new File("/home/markus/Desktop/VPAC_logo.blend")));
		
		final String multiJobName = "MULTI2";
		try {
			si.deleteMultiPartJob(multiJobName, true);
		} catch (Exception e) {
			// doesn't matter
		}
		final DtoMultiPartJob blenderMultiPartJob = si.createMultiPartJob(multiJobName);
		
		for (int i=1; i<4; i++) {

			final int frameNumber = i;
			Thread subThread = new Thread() {
				public void run() {

					
					JobObject jo = new JobObject(si);
//					jo.setJobname("blenderTestNew_" + frameNumber + "_" + UUID.randomUUID());
					jo.setJobname(multiJobName+"_" + frameNumber );
					jo.setApplication("blender");
					jo.setCommandline("blender -b ../../temp/VPAC_logo.blend -F PNG -o logo_ -f "+frameNumber);
					jo.setSubmissionLocation(subLoc);

					String site = registry.getResourceInformation().getSite(subLoc);
					System.out.println("Site is: " + site);

					try {
						jo.createJob("/ARCS/NGAdmin");
						
						si.addJobToMultiPartJob(multiJobName, jo.getJobname());
						blenderMultiPartJob.getJobnames().add(jo.getJobname());
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Job to "
								+ jo.getSubmissionLocation() + ": "
								+ e.getLocalizedMessage());
						jo.kill(true);
					}
				}

			};

			executor.execute(subThread);
		}

		executor.shutdown();
		
		executor.awaitTermination(3600, TimeUnit.SECONDS);
		
		System.out.println("Creation of jobs finished.");
		System.out.println("Uploading input file...");
		si.uploadMultiPartJobInputFile(multiJobName, dh, "/temp/VPAC_logo.blend");
		
		si.submitMultiPartJob(multiJobName);
		
		System.out.println("Finished...");
		

//		for ( String jobname : blenderMultiPartJob.getJobnames() ) {
//			System.out.println(jobname+": "+si.getJobStatus(jobname));
//		}

		
	}

	public final void jobStatusChanged(final JobObject job, final int oldStatus, final int newStatus) {
		System.out.println("JobSubmissionNew got job statusEvent: "
				+ job.getJobname() + " submitted to "
				+ job.getSubmissionLocation() + ": "
				+ JobConstants.translateStatus(newStatus));
	}

}
