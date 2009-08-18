package org.vpac.grisu.frontend.examples;

import java.io.File;
import java.util.Date;
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
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoMultiPartJob;
import org.vpac.grisu.model.info.ApplicationInformation;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class BlenderTest implements JobStatusChangeListener {

	public static void main(final String[] args) throws Exception {

		ExecutorService executor = Executors.newFixedThreadPool(5);

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

		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		SortedSet<GridResource> resources = blenderInfo.getBestSubmissionLocations(jobProperties, "/ARCS/NGAdmin");
		
		final String subLoc = SubmissionLocationHelpers.createSubmissionLocationString(resources.first());
		
		final String multiJobName = "MULTI2";
		try {
			si.deleteMultiPartJob(multiJobName, true);
		} catch (Exception e) {
			// doesn't matter
		}

		MultiPartJobObject multiPartJob = new MultiPartJobObject(si, multiJobName, "/ARCS/NGAdmin");
				
		
		for (int i=1; i<10; i++) {

			final int frameNumber = i;
				
				JobObject jo = new JobObject(si);
				jo.setJobname(multiJobName+"_" + frameNumber );
				jo.setApplication("blender");
				jo.setCommandline("blender -b "+multiPartJob.pathToInputFiles()+"/VPAC_logo.blend -F PNG -o logo_ -f "+frameNumber);
				jo.setSubmissionLocation(subLoc);

				multiPartJob.addJob(jo);
						
		}

		multiPartJob.addInputFile("/home/markus/Desktop/VPAC_logo.blend");
		
		multiPartJob.prepareAndCreateJobs();
		
		multiPartJob.submit();
		
		
		System.out.println("Submission finished...");
		
		MultiPartJobObject newObject = new MultiPartJobObject(si, multiJobName);
		
		newObject.monitorProgress();
		
		newObject.downloadResults("logo");

		
	}

	public final void jobStatusChanged(final JobObject job, final int oldStatus, final int newStatus) {
		System.out.println("JobSubmissionNew got job statusEvent: "
				+ job.getJobname() + " submitted to "
				+ job.getSubmissionLocation() + ": "
				+ JobConstants.translateStatus(newStatus));
	}

}
