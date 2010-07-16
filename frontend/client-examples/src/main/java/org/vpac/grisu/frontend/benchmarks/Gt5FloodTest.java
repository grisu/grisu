package org.vpac.grisu.frontend.benchmarks;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoJobs;

import au.org.arcs.jcommons.constants.Constants;

public class Gt5FloodTest {

	public static void main(String[] args) throws LoginException,
			InterruptedException {

		int simultaniousJobs = 10;
		if (args.length >= 1) {
			simultaniousJobs = Integer.parseInt(args[0]);
		}

		int totalNumberOfJobs = 2000;
		if (args.length == 2) {
			totalNumberOfJobs = Integer.parseInt(args[1]);
		}

		ExecutorService submissionExecutor = Executors
				.newFixedThreadPool(simultaniousJobs);
		ExecutorService killingExecutor = Executors
				.newFixedThreadPool(simultaniousJobs);

		final ServiceInterface si = LoginManager.loginCommandline("Local");

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		final ConcurrentLinkedQueue<JobObject> jobObjects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> failedJobObjects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> killFailedOjbects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> successfulJobObjects = new ConcurrentLinkedQueue<JobObject>();

		final Date startDate = new Date();

		for (int i = 0; i < totalNumberOfJobs; i++) {

			final int index = new Integer(i);
			Thread temp = new Thread() {

				@Override
				public void run() {
					JobObject job = null;
					try {
						System.out.println("Creating job  #" + index);
						job = new JobObject(si);
						job.setApplication(Constants.GENERIC_APPLICATION_NAME);
						job.setJobname("monsterBatchJob_" + index + "_"
								+ startDate.getTime());
						job.setCommandline("echo \"Hello Canterbury!\"");
						job.setSubmissionLocation("small:ng2.canterbury.ac.nz");
						// job
						// .setSubmissionLocation("gt5test:ng1.canterbury.ac.nz");
						job.createJob("/ARCS/NGAdmin");
						System.out.println("Created job  #" + index
								+ ". Submitting...");
						String jobname = job.getJobname();
						si.submitJob(jobname);
						System.out.println("Submitted job  #" + index);
						jobObjects.add(job);

					} catch (Exception e) {
						if (job != null) {
							try {
								job.kill(true);
							} catch (Exception e1) {
								System.err.println(e1.getLocalizedMessage());
							}
							failedJobObjects.add(job);
						}
					}
				}
			};

			submissionExecutor.execute(temp);
		}

		submissionExecutor.shutdown();
		submissionExecutor.awaitTermination(36000, TimeUnit.SECONDS);
		final Date allSubmissionFinishedDate = new Date();

		DtoJobs ps = si.ps(null, true);

		final Date psDate = new Date();

		System.out.println("All submission finished.");
		System.out.println("Start date: " + startDate.toString());
		System.out.println("Submission finished date: "
				+ allSubmissionFinishedDate.toString());
		System.out.println("Fetched ps document date: " + psDate.toString());
		System.out.println("Successfully submitted jobs: " + jobObjects.size());
		System.out.println("Unsuccessfully submitted jobs: "
				+ failedJobObjects.size());

		System.out.println("Starting to clean all jobs...");

		for (final JobObject job : jobObjects) {
			Thread temp = new Thread() {
				@Override
				public void run() {
					try {
						job.kill(true);
						successfulJobObjects.add(job);
					} catch (Exception e) {
						System.err.println(e.getLocalizedMessage());
						killFailedOjbects.add(job);
					}
				}
			};
			killingExecutor.execute(temp);
		}

		killingExecutor.shutdown();
		killingExecutor.awaitTermination(36000, TimeUnit.SECONDS);

		final Date endDate = new Date();

		System.out
				.println("---------------------------------------------------------------------------");
		final Date psDate2 = new Date();

		System.out.println("All submission finished.");
		System.out.println("Start date: " + startDate.toString());
		System.out.println("Submission finished date: "
				+ allSubmissionFinishedDate.toString());
		System.out.println("Fetched ps document date: " + psDate.toString());
		System.out.println("End date: " + endDate.toString());
		System.out.println("Fetched ps2 document date: " + psDate2.toString());

		System.out.println("Successfully submitted jobs: " + jobObjects.size());
		System.out.println("Unsuccessfully submitted jobs: "
				+ failedJobObjects.size());
		System.out.println("Unsuccessfully killed jobs: "
				+ killFailedOjbects.size());
		System.out.println("Successful jobs: " + successfulJobObjects.size());

	}

}