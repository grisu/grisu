package org.vpac.grisu.client.gridTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

public class ExternalGridTestElement extends GridTestElementFactory {

	public static final String TESTPROPERTIES_FILENAME = "grisu-test.properties";

	private final File testDir;
	private final File jsdl;
	private final Properties testProperties = new Properties();
	private final String testname;
	private final String description;
	private final List<String> inputFiles = new LinkedList<String>();
	private final List<String> outputFiles = new LinkedList<String>();
	private String command;
	private boolean useMds = true;

	public ExternalGridTestElement(File testDir, GridTestController c,
			ServiceInterface si, String version, String subLoc)
			throws MdsInformationException {
		super(c, si, version, subLoc);
		this.testDir = testDir;
		File propertiesFile = new File(testDir, TESTPROPERTIES_FILENAME);
		if (propertiesFile.exists()) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}
		try {
			this.testProperties.load(new FileInputStream(propertiesFile));
		} catch (Exception e) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}

		if (StringUtils.isBlank(testProperties.getProperty("jsdlfile"))) {
			// for the compiler
			jsdl = null;
			System.err.println("No jsdl file specified. Exiting...");
			System.exit(1);
		} else {
			jsdl = new File(testProperties.getProperty("jsdlfile"));
			if (!jsdl.exists()) {
				System.err.println("Specified jsdl file doesn't exist.");
			} else {
				try {
					SeveralXMLHelpers.loadXMLFile(jsdl);
				} catch (Exception e) {
					System.err.println("Could not parse jsdl file: "
							+ e.getLocalizedMessage());
					System.err.println("Exiting...");
					System.exit(1);
				}
			}
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

		String inputFilesString = testProperties.getProperty("inputfiles");
		if (StringUtils.isNotBlank(inputFilesString)) {
			for (String inputFile : inputFilesString.split(",")) {
				inputFiles.add(inputFile);
			}
		}
		String outputFilesString = testProperties
				.getProperty("outputfilenames");
		if (StringUtils.isNotBlank(outputFilesString)) {
			for (String outputFileName : outputFilesString.split(",")) {
				outputFiles.add(outputFileName);
			}
		}

		command = testProperties.getProperty("command");
		command = command.replaceAll("\\$TEST_DIR", testDir.getPath());

		String temp = testProperties.getProperty("usemds", "true");
		if ("true".equals(temp.toLowerCase())) {
			useMds = true;
		} else {
			useMds = false;
		}

	}

	@Override
	protected boolean checkJobSuccess() {

		try {
			String jobDir = jobObject.getJobDirectoryUrl();
			addMessage("Downloading output files from jobdirectory: " + jobDir);

			File localCacheDir = null;

			for (String filename : outputFiles) {
				addMessage("Downloading: " + filename + "...");
				File file = jobObject.downloadAndCacheOutputFile(filename);
				if (localCacheDir == null) {
					localCacheDir = file.getParentFile();
				}
			}
			StringBuffer output = new StringBuffer();

			int out = executeScript(localCacheDir, output);

			addMessage(output.toString());

			if (out == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			addMessage("Could not check job success: "
					+ e.getLocalizedMessage());
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
	}

	@Override
	protected JobObject createJobObject() throws MdsInformationException {

		Document jsdlDoc = SeveralXMLHelpers.loadXMLFile(jsdl);

		JobObject job = new JobObject(serviceInterface, jsdlDoc);
		for (String input : inputFiles) {
			job.addInputFileUrl(input);
		}

		return job;

	}

	@Override
	protected String getApplicationSupported() {
		return application;
	}

	@Override
	public String getTestDescription() {
		return description;
	}

	@Override
	public String getTestName() {
		return testname;
	}

	@Override
	protected boolean useMDS() {
		return useMds;
	}

	private int executeScript(File outputDir, StringBuffer output) {

		String s = null;

		try {

			if (outputDir != null) {
				command = command.replaceAll("\\$OUTPUT_DIR", outputDir
						.getPath());
			}

			System.out.println("Running parser script using: " + command);

			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();

			BufferedReader stdOut = new BufferedReader(new InputStreamReader(p
					.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read the output from the command
			StringBuffer out = new StringBuffer("Stdout:\n");
			while ((s = stdOut.readLine()) != null) {
				out.append(s + "\n");
			}
			out.append("\n");
			// read any errors from the attempted command
			StringBuffer err = new StringBuffer("Stderr:\n");
			while ((s = stdError.readLine()) != null) {
				err.append(s + "\n");
			}
			err.append("\n");

			output.append(out);
			output.append(err);

			int exitValue = p.exitValue();
			output.append("\nExitValue: " + exitValue);

			return exitValue;
		} catch (Exception e) {
			e.printStackTrace();
			output.append("Execution of external command failed:\n"
					+ e.getLocalizedMessage());
			return -1;
		}
	}

	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-cxf/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		Document jsdl = SeveralXMLHelpers
				.loadXMLFile(new File(
						"/home/markus/Workspaces/Grisu-SNAPSHOT/grisu/frontend/grisu-grid-tests/tests/pbstest/pbsTest.jsdl"));

		try {
			si.kill("pbsTest", true);
		} catch (Exception e) {
			//
		}

		JobObject job = new JobObject(si, jsdl);

		job.setSubmissionLocation("sque@edda-m:ng2.vpac.org");
		job.createJob("/ARCS/NGAdmin");

		job.submitJob();

		job.waitForJobToFinish(5);

		File stdout = job.getStdOutFile();
		StringBuffer output = new StringBuffer();

		Properties testProperties = new Properties();
		testProperties
				.load(new FileInputStream(
						"/home/markus/Workspaces/Grisu-SNAPSHOT/grisu/frontend/grisu-grid-tests/tests/pbstest/grisu-test.properties"));

		// int result = executeScript(testProperties, new
		// File("/home/markus/Workspaces/Grisu-SNAPSHOT/grisu/frontend/grisu-grid-tests/tests/pbstest/"),
		// job.getStdOutFile().getParentFile(), output);

		System.out.println("Output: " + output.toString());

	}

}
