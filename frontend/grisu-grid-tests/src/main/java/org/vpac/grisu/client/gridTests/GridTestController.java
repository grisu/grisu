package org.vpac.grisu.client.gridTests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.model.ApplicationInformation;

public class GridTestController {

	ExecutorService submitJobExecutor = Executors.newFixedThreadPool(5);
	ExecutorService processJobExecutor = Executors.newFixedThreadPool(5);

	private Map<String, Thread> createAndSubmitJobThreads = new HashMap<String, Thread>();
	private Map<String, Thread> checkAndKillJobThreads = new HashMap<String, Thread>();

	private Map<String, GridTestElement> gridTestElements = new HashMap<String, GridTestElement>();
	private List<GridTestElement> finishedElements = new LinkedList<GridTestElement>();

	private final ServiceInterface serviceInterface;
	private final GrisuRegistry registry;

	private final ApplicationInformation[] appInfos;
	private final String fqan;

	public GridTestController(ServiceInterface si, String[] applications,
			String fqan) {
		this.serviceInterface = si;
		registry = GrisuRegistry.getDefault(this.serviceInterface);
		appInfos = new ApplicationInformation[applications.length];
		for (int i = 0; i < applications.length; i++) {
			appInfos[i] = registry.getApplicationInformation(applications[i]);
		}
		this.fqan = fqan;
	}

	/**
	 * @param args
	 * @throws ServiceInterfaceException
	 * @throws MdsInformationException 
	 */
	public static void main(String[] args) throws ServiceInterfaceException, MdsInformationException {

		String username = args[0];
		char[] password = args[1].toCharArray();

		String fqan = args[2];

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		GridTestController gtc = new GridTestController(si, new String[]{"BLAST"}, fqan);

		gtc.start();

	}
	
	public void start() {

		try {
			createJobThreads();
		} catch (MdsInformationException e) {

			System.out.println("Could not create all necessary jobs: "+e.getLocalizedMessage()+". Exiting...");
			System.exit(1);
			
		}

		createAndSubmitAllJobs();

		waitForJobsToFinishAndCheckAndKillThem();
		
		displayStatistics();
	}

	public void waitForJobsToFinishAndCheckAndKillThem() {

		while (gridTestElements.size() > 0) {

			List<GridTestElement> batchOfRecentlyFinishedJobs = new LinkedList<GridTestElement>();

			for (GridTestElement gte : gridTestElements.values()) {

				if (gte.getJobStatus(true) >= JobConstants.FINISHED_EITHER_WAY) {
					batchOfRecentlyFinishedJobs.add(gte);
				}
			}

			for (GridTestElement gte : batchOfRecentlyFinishedJobs) {
				gridTestElements.remove(gte.getId());
				finishedElements.add(gte);
				processJobExecutor.execute(checkAndKillJobThreads.get(gte
						.getId()));
			}
			System.out.println("Still " + gridTestElements.size()
					+ " jobs not finished... Sleeping for 4 seconds...");

			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		processJobExecutor.shutdown();

		try {
			processJobExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void displayStatistics() {

		for (GridTestElement gte : finishedElements) {

			System.out.println("SubmissionLocation: "
					+ gte.getSubmissionLocation());
			System.out
					.println("-------------------------------------------------");
			gte.printTestResults();
			System.out
					.println("-------------------------------------------------");

		}

	}

	public void createAndSubmitAllJobs() {

		for (Thread thread : createAndSubmitJobThreads.values()) {
			submitJobExecutor.execute(thread);
		}

		submitJobExecutor.shutdown();

		try {
			submitJobExecutor.awaitTermination(600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createJobThreads() throws MdsInformationException {

		for (ApplicationInformation appInfo : appInfos) {

			Set<String> allVersions = appInfo
					.getAllAvailableVersionsForFqan(fqan);

			for (String version : allVersions) {

				Set<String> subLocsForVersion = appInfo
						.getAvailableSubmissionLocationsForVersionAndFqan(
								version, fqan);
				for (String subLoc : subLocsForVersion) {

					GridTestElement gte = GridTestElement.createGridTestElement(appInfo.getApplicationName(), serviceInterface, version, subLoc);

					gridTestElements.put(gte.getId(), gte);

					Thread createJobThread = createCreateAndSubmitJobThread(
							gte, fqan);
					createAndSubmitJobThreads.put(gte.getId(), createJobThread);
					Thread checkJobThread = createCheckAndKillJobThread(gte);
					checkAndKillJobThreads.put(gte.getId(), checkJobThread);

				}
			}
		}
	}

	private Thread createCreateAndSubmitJobThread(final GridTestElement gte,
			final String fqan) {

		Thread thread = new Thread() {
			public void run() {
				System.out.println("Creating job for subLoc: "
						+ gte.getSubmissionLocation());
				gte.createJob(fqan);
				System.out.println("Submitting job for subLoc: "
						+ gte.getSubmissionLocation());
				gte.submitJob();
				System.out.println("Submission to "
						+ gte.getSubmissionLocation() + " finished.");
			}
		};

		return thread;

	}

	private Thread createCheckAndKillJobThread(final GridTestElement gte) {

		Thread thread = new Thread() {
			public void run() {
				System.out
						.println("Checking job success for job submitted to: "
								+ gte.getSubmissionLocation());
				gte.checkJobSuccess();
				System.out.println("Killing job submitted to: "
						+ gte.getSubmissionLocation());
				gte.killAndClean();
				System.out.println("Job to " + gte.getSubmissionLocation()
						+ " killed.");
			}
		};

		return thread;

	}

}
