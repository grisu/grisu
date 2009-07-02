package org.vpac.grisu.client.gridTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.client.control.login.LoginParams;
import org.vpac.grisu.client.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.model.ApplicationInformation;
import org.vpac.security.light.plainProxy.LocalProxy;

public class GridTestController {

	public static final File GridTestDirectory = new File(System
			.getProperty("user.home"), "grid-tests");

	ExecutorService submitJobExecutor = Executors.newFixedThreadPool(5);
	ExecutorService processJobExecutor = Executors.newFixedThreadPool(5);

	private Map<String, Thread> createAndSubmitJobThreads = new HashMap<String, Thread>();
	private Map<String, Thread> checkAndKillJobThreads = new HashMap<String, Thread>();

	private Map<String, GridTestElement> gridTestElements = new HashMap<String, GridTestElement>();
	private List<GridTestElement> finishedElements = new LinkedList<GridTestElement>();

	private ServiceInterface serviceInterface;
	private final GrisuRegistry registry;

	private String[] applications;
	private final String fqan;
	private String output = "gridtestResults.txt";
	private String[] filters;

	public GridTestController(String[] args) {

		GridTestCommandlineOptions options = new GridTestCommandlineOptions(
				args);

		if (options.getMyproxyUsername() != null
				&& options.getMyproxyUsername().length() != 0) {
			try {
				ConsoleReader consoleReader = new ConsoleReader();
				char[] password = consoleReader.readLine(
						"Please enter your myproxy password: ",
						new Character('*')).toCharArray();

				LoginParams loginParams = new LoginParams(
				// "http://localhost:8080/grisu-ws/services/grisu",
						// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
						"Local", options.getMyproxyUsername(), password);

				serviceInterface = ServiceInterfaceFactory
						.createInterface(loginParams);
			} catch (Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		} else {
			// trying to get local proxy

			LoginParams loginParams = new LoginParams("Local", null, null,
					"myproxy2.arcs.org.au", "443");
			try {
				serviceInterface = LoginHelpers.login(loginParams, LocalProxy
						.loadGSSCredential());
			} catch (Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		}

		registry = GrisuRegistry.getDefault(this.serviceInterface);

		fqan = options.getFqan();
		if (options.getOutput() != null && options.getOutput().length() > 0) {
			output = options.getOutput();
		}
		applications = options.getApplications();
		
		filters = options.getFilters();

	}

	public GridTestController(ServiceInterface si, String[] applications,
			String fqan) {
		this.serviceInterface = si;
		registry = GrisuRegistry.getDefault(this.serviceInterface);
		this.fqan = fqan;
	}

	/**
	 * @param args
	 * @throws ServiceInterfaceException
	 * @throws MdsInformationException
	 */
	public static void main(String[] args) throws ServiceInterfaceException,
			MdsInformationException {

		GridTestController gtc = new GridTestController(args);

		gtc.start();

	}

	public void start() {

		try {
			createJobsJobThreads();
		} catch (MdsInformationException e) {

			System.out.println("Could not create all necessary jobs: "
					+ e.getLocalizedMessage() + ". Exiting...");
			System.exit(1);

		}
		
		System.out.println("Initialized jobs:");
		System.out.println(StringUtils.join(gridTestElements.values(), "\n"));

		createAndSubmitAllJobs();

		for (GridTestElement gte : gridTestElements.values()) {

			if (gte.failed()) {
				finishedElements.add(gte);
			} else {
				checkAndKillJobThreads.put(gte.getId(),
						createCheckAndKillJobThread(gte));
			}
		}
		// remove failed gtes from map
		for (GridTestElement gte : finishedElements) {
			gridTestElements.remove(gte.getId());
		}

		waitForJobsToFinishAndCheckAndKillThem();
		
		writeStatistics();

		System.out.println("\nSummary:\n-------------\n");
		int countFailed = 0;
		int countSuccess = 0;
		StringBuffer failedSubLocs = new StringBuffer();
		for (GridTestElement gte : finishedElements) {
			if (gte.failed()) {
				countFailed = countFailed + 1;
				failedSubLocs
						.append("\t" + gte.getSubmissionLocation() + ":\n");
				for (Exception e : gte.getExceptions()) {
					failedSubLocs.append(Utils.fromException(e) + "\n");
				}
			} else {
				countSuccess = countSuccess + 1;
			}
		}
		System.out.println("Total jobs:\t" + finishedElements.size());
		System.out.println("Successful jobs:\t" + countSuccess);
		System.out.println("Failed jobs\t: " + countFailed);
		if (countFailed > 0) {
			System.out.println("Failed submission locations: ");
			System.out.println(failedSubLocs.toString());
		}
	}
	
	public void waitForJobsToFinishAndCheckAndKillThem() {

		while (gridTestElements.size() > 0) {

			List<GridTestElement> batchOfRecentlyFinishedJobs = new LinkedList<GridTestElement>();

			for (GridTestElement gte : gridTestElements.values()) {

				if (gte.getJobStatus(true) >= JobConstants.FINISHED_EITHER_WAY
						|| gte.getJobStatus(false) <= JobConstants.READY_TO_SUBMIT) {
					batchOfRecentlyFinishedJobs.add(gte);
				}
			}

			for (GridTestElement gte : batchOfRecentlyFinishedJobs) {
				gridTestElements.remove(gte.getId());
				gte.finishTest();
				finishedElements.add(gte);
				processJobExecutor.execute(checkAndKillJobThreads.get(gte
						.getId()));
			}

			if (gridTestElements.size() == 0) {
				break;
			}

			StringBuffer remainingSubLocs = new StringBuffer();
			for ( GridTestElement gte : gridTestElements.values() ) {
				remainingSubLocs.append("\t"+gte.getApplicationSupported()+", "+gte.version+" at "+gte.getSubmissionLocation()+"\n");
			}
			System.out.println("\nStill " + gridTestElements.size()
					+ " jobs not finished:"); 
			System.out.println(remainingSubLocs.toString());
			
			System.out.println("Sleeping for another 30 seconds...");

			try {
				Thread.sleep(30000);
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

	public void writeStatistics() {

		StringBuffer outputString = new StringBuffer();

		for (GridTestElement gte : finishedElements) {

			outputString.append("SubmissionLocation: "
					+ gte.getSubmissionLocation() + "\n");
			outputString
			.append("-------------------------------------------------"
					+ "\n");
			outputString.append("Started: "+gte.getStartDate().toString()+"\n");
			outputString.append("Ended: "+gte.getEndDate().toString()+"\n");
			outputString
			.append("-------------------------------------------------"
					+ "\n");
			outputString.append(gte.getResultString() + "\n");
			outputString
					.append("-------------------------------------------------"
							+ "\n");

		}

		try {

			String uFileName = output;
			FileWriter fileWriter = new FileWriter(uFileName);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
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

	public void createJobsJobThreads() throws MdsInformationException {

		for (String application : applications) {

			if (ServiceInterface.GENERIC_APPLICATION_NAME.equals(application)) {
				
				String[] subLocs = registry.getResourceInformation().getAllAvailableSubmissionLocations(fqan);
				
				for ( String subLoc : subLocs ) {

					boolean skipSubLoc = false;
					for ( String filter : filters ) {
						if ( subLoc.indexOf(filter) >= 0 ) {
							skipSubLoc = true;
							break;
						}
					}
					
					if ( skipSubLoc ) {
						continue;
					}
					
					GridTestElement gte = GridTestElement.createGridTestElement(application, serviceInterface, ServiceInterface.NO_VERSION_INDICATOR_STRING, subLoc);
					gridTestElements.put(gte.getId(), gte);
					
					Thread createJobThread = createCreateAndSubmitJobThread(
							gte, fqan);
					createAndSubmitJobThreads.put(gte.getId(),
							createJobThread);
				}
				
			} else {
				ApplicationInformation appInfo = registry.getApplicationInformation(application);
				Set<String> allVersions = appInfo
						.getAllAvailableVersionsForFqan(fqan);

				for (String version : allVersions) {

					Set<String> subLocsForVersion = appInfo
							.getAvailableSubmissionLocationsForVersionAndFqan(
									version, fqan);
					for (String subLoc : subLocsForVersion) {
						
						boolean skipSubLoc = false;
						for ( String filter : filters ) {
							if ( subLoc.indexOf(filter) >= 0 ) {
								skipSubLoc = true;
								break;
							}
						}
						
						if ( skipSubLoc ) {
							continue;
						}

						GridTestElement gte = GridTestElement
								.createGridTestElement(appInfo
										.getApplicationName(),
										serviceInterface, version, subLoc);

						gridTestElements.put(gte.getId(), gte);

						Thread createJobThread = createCreateAndSubmitJobThread(
								gte, fqan);
						createAndSubmitJobThreads.put(gte.getId(),
								createJobThread);

					}
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
				if (gte.failed()) {
					System.out
							.println("Submission to "
									+ gte.getSubmissionLocation()
									+ " finished: Failed");
				} else {
					System.out.println("Submission to "
							+ gte.getSubmissionLocation()
							+ " finished: Success");
				}
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
				gte.checkWhetherJobDidWhatItWasSupposedToDo();
				if (gte.failed()) {
					System.out.println("Job submitted to "
							+ gte.getSubmissionLocation()
							+ " failed on resource.");
				} else {
					System.out.println("Job submitted to "
							+ gte.getSubmissionLocation()
							+ " completed successfully.");
				}
				System.out.println("Killing and cleaning job submitted to: "
						+ gte.getSubmissionLocation());
				gte.killAndClean();
				if (gte.failed()) {
					System.out
							.println("Killing and cleaning of job submitted to "
									+ gte.getSubmissionLocation() + " failed.");
				} else {
					System.out
							.println("Killing and cleaning of job submitted to "
									+ gte.getSubmissionLocation()
									+ " was successful.");
				}

			}
		};

		return thread;

	}

}
