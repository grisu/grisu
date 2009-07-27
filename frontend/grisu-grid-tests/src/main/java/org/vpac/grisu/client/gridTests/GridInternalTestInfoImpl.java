package org.vpac.grisu.client.gridTests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.gridTests.testElements.GridTestElement;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.info.ApplicationInformation;

import au.org.arcs.mds.Constants;

public class GridInternalTestInfoImpl implements GridTestInfo {

	private Class testClass;
	private final String testname;
	private boolean useMds;
	private String applicationName;
	private String versionName;
	private String description;
	private final GridTestController controller;
	private final Map<String, Set<String>> subLocsPerVersions = new TreeMap<String, Set<String>>();

	public GridInternalTestInfoImpl(String testname,
			GridTestController controller) throws ClassNotFoundException {
		this.controller = controller;
		this.testname = testname;

		testClass = Class
				.forName("org.vpac.grisu.client.gridTests.testElements."
						+ testname+"GridTestElement");

		try {
			Method useMdsMethod = testClass.getMethod("useMDS");
			useMds = (Boolean) (useMdsMethod.invoke(null));

		} catch (Exception e) {
			System.err.println("Could not create internal test " + testname
					+ " because the static useMDS method is not implemented: "
					+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			Method method = testClass.getMethod("getApplicationName");
			applicationName = (String) (method.invoke(null));

		} catch (Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getApplicationName method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			Method method = testClass.getMethod("getTestDescription");
			description = (String) (method.invoke(null));

		} catch (Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getTestDescription method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			Method method = testClass.getMethod("getFixedVersion");
			versionName = (String) (method.invoke(null));

		} catch (Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getFixedVersion method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		if (useMds) {
			ApplicationInformation appInfo = GrisuRegistry.getDefault(
					controller.getServiceInterface())
					.getApplicationInformation(applicationName);
			if (StringUtils.isBlank(versionName)
					|| Constants.NO_VERSION_INDICATOR_STRING
							.equals(versionName)) {
				Set<String> versions = appInfo
						.getAllAvailableVersionsForFqan(controller.getFqan());
				for (String version : versions) {
					Set<String> submissionLocations = appInfo
							.getAvailableSubmissionLocationsForVersionAndFqan(
									version, controller.getFqan());
					subLocsPerVersions.put(version, submissionLocations);
				}
			} else {
				Set<String> submissionLocations = appInfo
						.getAvailableSubmissionLocationsForVersionAndFqan(
								versionName, controller.getFqan());
				subLocsPerVersions.put(versionName, submissionLocations);
			}
		} else {
			Set<String> submissionLocations = new HashSet<String>(Arrays
					.asList(GrisuRegistry.getDefault(
							controller.getServiceInterface())
							.getResourceInformation()
							.getAllAvailableSubmissionLocations(
									controller.getFqan())));
			subLocsPerVersions.put(Constants.NO_VERSION_INDICATOR_STRING,
					submissionLocations);
		}

	}

	public GridTestElement createGridTestElement(String version,
			String submissionLocation) throws MdsInformationException {

		Constructor testConstructor = null;
		try {
			testConstructor = testClass.getConstructor(
					GridTestInfo.class, String.class, String.class);
		} catch (Exception e) {
			System.err.println("Could not create internal test " + testname
					+ ": " + e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		GridTestElement gte = null;
		try {
			gte = (GridTestElement)testConstructor.newInstance(this, version, submissionLocation);
		} catch (Exception e) {
			System.err.println("Could not create internal test " + testname
					+ ": " + e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		return gte;
	}

	public static final List<GridTestInfo> generateGridTestInfos(
			GridTestController controller, String[] testnames) {

		List<GridTestInfo> result = new LinkedList<GridTestInfo>();
		
		if ( testnames.length == 0 ) {
			testnames = new String[]{"Java","SimpleCatJob","Underworld","UnixCommands"};
		}

		for (String testname : testnames) {
			GridInternalTestInfoImpl info;
			try {
				info = new GridInternalTestInfoImpl(testname, controller);
				result.add(info);
			} catch (ClassNotFoundException e) {
				System.out.println("No internal gridtest with the name: "
						+ testname + ". Ignoring it...");
				continue;
			}
		}

		return result;
	}

	public List<GridTestElement> generateAllGridTestElements()
			throws MdsInformationException {

		List<GridTestElement> results = new LinkedList<GridTestElement>();

		Map<String, Set<String>> map = getSubmissionLocationsPerVersion();
		for (String version : map.keySet()) {
			for (String subLoc : map.get(version)) {
				results.add(createGridTestElement(version, subLoc));
			}
		}

		return results;

	}

	public String getApplicationName() {
		return applicationName;
	}

	public GridTestController getController() {
		return controller;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, Set<String>> getSubmissionLocationsPerVersion() {
		return subLocsPerVersions;
	}

	public File getTestBaseDir() {
		return controller.getGridTestDirectory();
	}

	public String getTestname() {
		return testname;
	}

	public boolean isUseMds() {
		return useMds;
	}

}
