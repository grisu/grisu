package org.vpac.grisu.client.gridTests;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
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
import org.vpac.grisu.backend.hibernate.HibernateSessionFactory;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginHelpers;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.settings.Environment;
import org.vpac.security.light.plainProxy.LocalProxy;

import au.org.arcs.mds.Constants;

public class GridTestController {

	private final String grisu_base_directory;

	private final File grid_tests_directory;

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
	private String output = null;
	private String[] excludes;
	private String[] includes;

	private final Date timeoutDate;

	private List<OutputModule> outputModules = new LinkedList<OutputModule>();

	public GridTestController(String[] args, String grisu_base_directory_param) {

		if (StringUtils.isBlank(grisu_base_directory_param)) {
			this.grisu_base_directory = System.getProperty("user.home")
					+ File.separator + "grisu-grid-tests";
		} else {
			this.grisu_base_directory = grisu_base_directory_param;
		}

		Environment.setGrisuDirectory(this.grisu_base_directory);
		HibernateSessionFactory
				.setCustomHibernateConfigFile(this.grisu_base_directory
						+ File.separator + "grid-tests-hibernate-file.cfg.xml");

		grid_tests_directory = new File(this.grisu_base_directory, "tests");

		output = grid_tests_directory + File.separator + "testResults_"
				+ new Date().getTime() + ".log";

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

		excludes = options.getExcludes();
		includes = options.getIncludes();

		outputModules.add(new LogFileOutputModule(output));
		outputModules.add(new XmlRpcOutputModule());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, options.getTimeout());
		timeoutDate = cal.getTime();

		System.out.println("All remaining jobs will be killed at: "
				+ timeoutDate.toString());
	}

	// public GridTestController(ServiceInterface si, String[] applications,
	// String fqan) {
	// this.serviceInterface = si;
	// registry = GrisuRegistry.getDefault(this.serviceInterface);
	// this.fqan = fqan;
	// }

	public File getGridTestDirectory() {
		return grid_tests_directory;
	}

	/**
	 * @param args
	 * @throws ServiceInterfaceException
	 * @throws MdsInformationException
	 */
	public static void main(String[] args) throws ServiceInterfaceException,
			MdsInformationException {

		String name = GridTestController.class.getName();
		name = name.replace('.', '/') + ".class";
		URL url = GridTestController.class.getClassLoader().getResource(name);
		String path = url.getPath();
		// System.out.println("Executable path: "+path);
		String baseDir = null;
		if (url.toString().startsWith("jar:")) {
			baseDir = path.toString().substring(path.indexOf(":") + 1,
					path.indexOf(".jar!"));
			baseDir = baseDir.substring(0, baseDir.lastIndexOf("/"));
		} else {
			baseDir = null;
		}

		System.out.println("Using directory: " + baseDir);

		GridTestController gtc = new GridTestController(args, baseDir);

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

		StringBuffer setup = OutputModuleHelpers
				.createTestSetupString(gridTestElements.values());

		for (OutputModule module : outputModules) {
			module.writeTestsSetup(setup.toString());
		}
		System.out.println(setup.toString());

		createAndSubmitAllJobs();

		for (GridTestElement gte : gridTestElements.values()) {
			//
			// if (gte.failed()) {
			// finishedElements.add(gte);
			// } else {
			checkAndKillJobThreads.put(gte.getTestId(),
					createCheckAndKillJobThread(gte));
			// }
		}

		// // remove failed gtes from map
		// for (GridTestElement gte : finishedElements) {
		// gridTestElements.remove(gte.getId());
		// }

		waitForJobsToFinishAndCheckAndKillThem();

		writeStatistics();

	}

	public void waitForJobsToFinishAndCheckAndKillThem() {

		while (gridTestElements.size() > 0) {

			if (new Date().after(timeoutDate)) {

				for (GridTestElement gte : gridTestElements.values()) {
					System.out.println("Interrupting not finished job: "
							+ gte.getApplicationSupported() + ", "
							+ gte.version + " at "
							+ gte.getSubmissionLocation());
					if (!gte.failed()
							&& gte.getJobStatus(true) < JobConstants.FINISHED_EITHER_WAY) {
						gte.interruptRunningJob();
					}
				}
			}

			List<GridTestElement> batchOfRecentlyFinishedJobs = new LinkedList<GridTestElement>();

			for (GridTestElement gte : gridTestElements.values()) {

				if (gte.getJobStatus(true) >= JobConstants.FINISHED_EITHER_WAY
						|| gte.getJobStatus(false) <= JobConstants.READY_TO_SUBMIT
						|| gte.failed()) {
					batchOfRecentlyFinishedJobs.add(gte);
				}
			}

			for (GridTestElement gte : batchOfRecentlyFinishedJobs) {
				gridTestElements.remove(gte.getTestId());
				// gte.finishTest();
				finishedElements.add(gte);
				processJobExecutor.execute(checkAndKillJobThreads.get(gte
						.getTestId()));
			}

			if (gridTestElements.size() == 0) {
				break;
			}

			StringBuffer remainingSubLocs = new StringBuffer();
			for (GridTestElement gte : gridTestElements.values()) {
				remainingSubLocs.append("\t" + gte.getApplicationSupported()
						+ ", " + gte.version + " at "
						+ gte.getSubmissionLocation() + "\n");
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

	public synchronized void writeGridTestElementLog(GridTestElement gte) {

		for (OutputModule module : outputModules) {
			System.out.println("Writing output using: "
					+ module.getClass().getName());
			module.writeTestElement(gte);
		}

	}

	public void writeStatistics() {

		StringBuffer statistics = OutputModuleHelpers
				.createStatisticsString(finishedElements);

		for (OutputModule module : outputModules) {
			module.writeTestsStatistic(statistics.toString());
		}

		System.out.println(statistics.toString());

	}

	public void createAndSubmitAllJobs() {

		for (Thread thread : createAndSubmitJobThreads.values()) {
			submitJobExecutor.execute(thread);
		}

		submitJobExecutor.shutdown();

		try {
			submitJobExecutor.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createJobsJobThreads() throws MdsInformationException {

		for (String application : applications) {

			if (!GridTestElement.useMds(application)) {

				String[] subLocs = registry.getResourceInformation()
						.getAllAvailableSubmissionLocations(fqan);

				for (String subLoc : subLocs) {

					boolean skipSubLoc = false;
					if (includes.length == 0) {
						for (String filter : excludes) {
							if (subLoc.indexOf(filter) >= 0) {
								skipSubLoc = true;
								break;
							}
						}
					} else {
						for (String filter : includes) {
							if (subLoc.indexOf(filter) < 0) {
								skipSubLoc = true;
								break;
							}
						}
					}

					if (skipSubLoc) {
						continue;
					}

					GridTestElement gte = GridTestElement
							.createGridTestElement(this, application,
									serviceInterface,
									Constants.NO_VERSION_INDICATOR_STRING,
									subLoc);
					gridTestElements.put(gte.getTestId(), gte);

					Thread createJobThread = createCreateAndSubmitJobThread(
							gte, fqan);
					createAndSubmitJobThreads.put(gte.getTestId(),
							createJobThread);
				}

			} else {
				ApplicationInformation appInfo = registry
						.getApplicationInformation(application);
				Set<String> allVersions = appInfo
						.getAllAvailableVersionsForFqan(fqan);

				for (String version : allVersions) {

					Set<String> subLocsForVersion = appInfo
							.getAvailableSubmissionLocationsForVersionAndFqan(
									version, fqan);

					for (String subLoc : subLocsForVersion) {

						boolean skipSubLoc = false;
						if (includes.length == 0) {
							for (String filter : excludes) {
								if (subLoc.indexOf(filter) >= 0) {
									skipSubLoc = true;
									break;
								}
							}
						} else {
							for (String filter : includes) {
								if (subLoc.indexOf(filter) < 0) {
									skipSubLoc = true;
									break;
								}
							}
						}

						if (skipSubLoc) {
							continue;
						}

						GridTestElement gte = GridTestElement
								.createGridTestElement(this, appInfo
										.getApplicationName(),
										serviceInterface, version, subLoc);

						gridTestElements.put(gte.getTestId(), gte);

						Thread createJobThread = createCreateAndSubmitJobThread(
								gte, fqan);
						createAndSubmitJobThreads.put(gte.getTestId(),
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
				if (!gte.failed()) {
					System.out.println("Job submitted to "
							+ gte.getSubmissionLocation()
							+ " completed successfully.");
				}
				System.out.println("Killing and cleaning job submitted to: "
						+ gte.getSubmissionLocation());
				gte.killAndClean();
				if (!gte.failed()) {
					System.out
							.println("Killing and cleaning of job submitted to "
									+ gte.getSubmissionLocation()
									+ " was successful.");
				}

				gte.finishTest();
				writeGridTestElementLog(gte);

			}
		};

		return thread;

	}

}
