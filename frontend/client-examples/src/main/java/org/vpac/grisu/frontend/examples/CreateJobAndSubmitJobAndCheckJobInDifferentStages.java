package org.vpac.grisu.frontend.examples;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.model.events.JobStatusEvent;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.utils.SeveralXMLHelpers;

import au.org.arcs.jcommons.constants.Constants;

public final class CreateJobAndSubmitJobAndCheckJobInDifferentStages implements
		EventTopicSubscriber<JobStatusEvent> {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				// "http://localhost:8080/enunciate-backend/soap/GrisuService",
				// "http://localhost:8080/soap/GrisuService",
				"LOCAL_WS",
				// "ARCS_DEV",
				// "Local",
				args[0], args[1].toCharArray());

		ServiceInterface si = LoginManager.login(null, null, null, null,
				loginParams);

		CreateJobAndSubmitJobAndCheckJobInDifferentStages eventHolder = new CreateJobAndSubmitJobAndCheckJobInDifferentStages();

		JobObject createJobObject = new JobObject(si);

		createJobObject.setApplication("Java");
		createJobObject.setCommandline("java -version");
		createJobObject.setWalltimeInSeconds(3600 * 24 * 40);
		createJobObject.setCpus(1);
		createJobObject.addInputFileUrl("/home/markus/test.txt");
		createJobObject
				.addInputFileUrl("gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grix_splash_v1.1.jpg");

		// GrisuRegistry registry = GrisuRegistry.getDefault(si);
		// System.out.println(StringUtils.join(registry.getApplicationInformation("java").getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin"),"\n"));

		System.out.println(SeveralXMLHelpers
				.toStringWithoutAnnoyingExceptions(createJobObject
						.getJobDescriptionDocument()));

		String newJobname = createJobObject.createJob("/ARCS/NGAdmin",
				Constants.TIMESTAMP_METHOD);

		EventBus.subscribe(newJobname, eventHolder);

		JobObject submitJobObject = new JobObject(si, newJobname);

		System.out.println("Application: " + submitJobObject.getApplication());

		submitJobObject.submitJob();

		final JobObject checkJobObject = new JobObject(si, newJobname);

		Thread waitThread = new Thread() {
			public void run() {
				try {
					Thread.sleep(80000);
					System.out.println("Sleeping over.");
					checkJobObject.stopWaitingForJobToFinish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		};

		waitThread.start();

		boolean finished = checkJobObject.waitForJobToFinish(3);

		if (!finished) {
			System.out.println("not finished yet.");
			checkJobObject.kill(true);
			waitThread.interrupt();
		} else {
			System.out.println("Stdout: " + checkJobObject.getStdOutContent());
			System.out.println("Stderr: " + checkJobObject.getStdErrContent());
			checkJobObject.kill(true);
			waitThread.interrupt();
		}

	}

	private CreateJobAndSubmitJobAndCheckJobInDifferentStages() {
	}

	public void onEvent(String arg0, JobStatusEvent arg1) {

		System.out.println("Topic: " + arg0 + " Event: "
				+ JobConstants.translateStatus(arg1.getNewStatus()));
	}

}
