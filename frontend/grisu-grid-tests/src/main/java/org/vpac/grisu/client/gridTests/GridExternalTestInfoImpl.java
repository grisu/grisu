package org.vpac.grisu.client.gridTests;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.gridTests.testElements.ExternalGridTestElement;
import org.vpac.grisu.client.gridTests.testElements.GridTestElement;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.JsdlHelpers;

public class GridExternalTestInfoImpl implements GridTestInfo {

	public static final String TESTPROPERTIES_FILENAME = "grisu-test.properties";

	private final String testname;
	private final String description;
	private final boolean useMds;
	private final String applicationName;
	private final String versionName;
	private final Map<String, Set<String>> subLocsPerVersions = new TreeMap<String, Set<String>>();
	private final File testDir;
	private final File jsdlFile;
	private final List<String> inputFiles;

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getTestDir()
	 */
	public File getTestBaseDir() {
		return testDir;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getJsdlFile()
	 */
	public File getJsdlFile() {
		return jsdlFile;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getInputFiles()
	 */
	public List<String> getInputFiles() {
		return inputFiles;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getOutputFiles()
	 */
	public List<String> getOutputFiles() {
		return outputFiles;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getCommand()
	 */
	public String getCommand() {
		return command;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getJsdlDoc()
	 */
	public Document getJsdlDoc() {
		return jsdlDoc;
	}

	private final List<String> outputFiles;
	private final String command;
	private final Document jsdlDoc;

	private final GridTestController controller;

	public static final List<GridTestInfo> generateGridTestInfos(
			GridTestController controller, String[] testnames) {
		
		List<GridTestInfo> result = new LinkedList<GridTestInfo>();
		File baseDir = controller.getGridTestDirectory();

		File[] children = baseDir.listFiles();

		if (children == null) {
			return result;
		}
		for (File child : children) {

			if (child.exists() && child.isDirectory()
					&& !child.getName().startsWith(".")) {
				GridExternalTestInfoImpl info = new GridExternalTestInfoImpl(child, controller);
				if ( testnames.length == 0 || Arrays.binarySearch(testnames, info.getTestname()) >= 0 ) {
					result.add(info);
				}
			}

		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#generateAllGridTestElements()
	 */
	public final List<GridTestElement> generateAllGridTestElements()
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

	public GridExternalTestInfoImpl(File rootfolder, GridTestController controller) {

		this.controller = controller;

		this.testDir = rootfolder;
		File propertiesFile = new File(testDir, TESTPROPERTIES_FILENAME);
		if (!propertiesFile.exists()) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}
		Properties testProperties = new Properties();
		try {
			testProperties.load(new FileInputStream(propertiesFile));
		} catch (Exception e) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}

		if (StringUtils.isBlank(testProperties.getProperty("jsdlfile"))) {
			// for the compiler
			jsdlFile = null;
			System.err.println("No jsdl file specified. Exiting...");
			System.exit(1);
		} else {
			String jsdlFilename = testProperties.getProperty("jsdlfile");
			jsdlFile = new File(testDir, jsdlFilename);
			if (!jsdlFile.exists()) {
				System.err.println("Specified jsdl file doesn't exist.");
			} else {
				try {
					SeveralXMLHelpers.loadXMLFile(jsdlFile);
				} catch (Exception e) {
					System.err.println("Could not parse jsdl file: "
							+ e.getLocalizedMessage());
					System.err.println("Exiting...");
					System.exit(1);
				}
			}
		}

		String temp = testProperties.getProperty("usemds", "true");
		if ("true".equals(temp.toLowerCase())) {
			useMds = true;
		} else {
			useMds = false;
		}

		jsdlDoc = SeveralXMLHelpers.loadXMLFile(jsdlFile);

		applicationName = JsdlHelpers.getApplicationName(jsdlDoc);
		if (StringUtils.isBlank(applicationName)) {
			System.err.println("No application name specified in jsdl file: "
					+ jsdlFile.getPath());
			System.err.println("Exiting...");
			System.exit(1);
		}
		versionName = JsdlHelpers.getApplicationVersion(jsdlDoc);

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
			Set<String> submissionLocations = new HashSet<String>(Arrays.asList(GrisuRegistry.getDefault(controller.getServiceInterface()).getResourceInformation().getAllAvailableSubmissionLocations(controller.getFqan())));
			subLocsPerVersions.put(Constants.NO_VERSION_INDICATOR_STRING, submissionLocations);
		}

		if (StringUtils.isBlank(testProperties.getProperty("testname"))) {
			testname = testDir.getName();
		} else {
			testname = testProperties.getProperty("testname");
		}

		if (StringUtils.isBlank(testProperties.getProperty("description"))) {
			description = "No description.";
		} else {
			description = testProperties.getProperty("description");
		}

		inputFiles = new LinkedList<String>();
		String inputFilesString = testProperties.getProperty("inputfiles");
		if (StringUtils.isNotBlank(inputFilesString)) {
			for (String inputFile : inputFilesString.split(",")) {
				inputFiles.add(inputFile);
			}
		}
		String outputFilesString = testProperties
				.getProperty("outputfiles");
		outputFiles = new LinkedList<String>();
		if (StringUtils.isNotBlank(outputFilesString)) {
			for (String outputFileName : outputFilesString.split(",")) {
				outputFiles.add(outputFileName);
			}
		}

		command = testProperties.getProperty("command").replaceAll(
				"\\$TEST_DIR", testDir.getPath());

	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getController()
	 */
	public GridTestController getController() {
		return this.controller;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getTestname()
	 */
	public String getTestname() {
		return testname;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#isUseMds()
	 */
	public boolean isUseMds() {
		return useMds;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getApplicationName()
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getSubmissionLocationsPerVersion()
	 */
	public Map<String, Set<String>> getSubmissionLocationsPerVersion() {
		return subLocsPerVersions;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#createGridTestElement(java.lang.String, java.lang.String)
	 */
	public GridTestElement createGridTestElement(String version,
			String submissionLocation) throws MdsInformationException {

		GridTestElement el = new ExternalGridTestElement(this, version,
				submissionLocation);
		return el;
	}

}
