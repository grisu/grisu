package org.vpac.grisu.client.control.example;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.ServiceInterface;

public class SuperMonsterBatchJob {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();
		
		int simultaniousJobs = 10;
		if ( args.length == 3 ) {
			simultaniousJobs = Integer.parseInt(args[2]);
		}
		
		ExecutorService submissionExecutor = Executors.newFixedThreadPool(simultaniousJobs);
		ExecutorService killingExecutor = Executors.newFixedThreadPool(simultaniousJobs);

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		final GrisuRegistry registry = GrisuRegistry.getDefault(si);
		
		final ConcurrentLinkedQueue<JobObject> jobObjects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> failedJobObjects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> killFailedOjbects = new ConcurrentLinkedQueue<JobObject>();
		final ConcurrentLinkedQueue<JobObject> successfulJobObjects = new ConcurrentLinkedQueue<JobObject>();
		
		final Date startDate = new Date();

		for (int i = 0; i < 1000; i++) {

			final int index = new Integer(i);
			Thread temp = new Thread() {
				
				public void run() {
					JobObject job = null;
					try {
						job = new JobObject(si);
						job
								.setApplication(ServiceInterface.GENERIC_APPLICATION_NAME);
						job.setJobname("monsterBatchJob_" + index + "_" + startDate.getTime());
						job.setCommandline("echo \"Hello Brecca!\"");
//						job.addInputFileUrl("/home/markus/test.txt");
						job
								.setSubmissionLocation("dque@brecca-m:ng2.vpac.monash.edu.au");
						//job.addModule("java");
						job.createJob("/ARCS/VPAC");

						job.submitJob();
						
						jobObjects.add(job);
						
					} catch (Exception e) {
						if ( job != null ) {
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
		submissionExecutor.awaitTermination(3600, TimeUnit.SECONDS);
		final Date allSubmissionFinishedDate = new Date();
		
		System.out.println("All submission finished.");
		System.out.println("Start date: "+startDate.toString());
		System.out.println("Submission finished date: "+allSubmissionFinishedDate.toString());
		
		System.out.println("Successfully submitted jobs: "+jobObjects.size());
		System.out.println("Unsuccessfully submitted jobs: "+failedJobObjects.size());
		
		System.out.println("Starting to clean all jobs...");
		
		for ( final JobObject job : jobObjects ) {
			Thread temp = new Thread() {
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
		killingExecutor.awaitTermination(3600, TimeUnit.SECONDS);
		
		final Date endDate = new Date();
		
		System.out.println("All submission finished.");
		System.out.println("Start date: "+startDate.toString());
		System.out.println("Submission finished date: "+allSubmissionFinishedDate.toString());
		System.out.println("End date: "+endDate.toString());
		
		System.out.println("Successfully submitted jobs: "+jobObjects.size());
		System.out.println("Unsuccessfully submitted jobs: "+failedJobObjects.size());
		System.out.println("Unsuccessfully killed jobs: "+killFailedOjbects.size());
		System.out.println("Successful jobs: "+successfulJobObjects.size());

	}
}
