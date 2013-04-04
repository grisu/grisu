package grisu.frontend.control;

import grisu.control.JobnameHelpers;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.jcommons.constants.Constants;
import grisu.model.job.JobDescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class GJob {

	public static void main(String[] args) throws Exception {

		LoginManager.initGrisuClient("gjob");

		ServiceInterface si = LoginManager.login("nesi", false);
		// ServiceInterface si = LoginManager.login("local", false);

		GJob gjob = new GJob(
				"/data/src/config/end-to-end-tests/Gaussian/jobs/H2O");
		// GJob gjob = new
		// GJob("/data/src/config/end-to-end-tests/generic/jobs/stdinput_test");

		GrisuJob job = gjob.createJobDescription(si);

		job.submitJob();

	}

	public final static Logger myLogger = LoggerFactory.getLogger(GJob.class);

	public final static String JOB_KEY = "job";
	public final static String GROUP_KEY = "group";
	public final static String QUEUE_KEY = "queue";
	public final static String SUBMIT_PROPERTIES_FILE_EXTENSION = ".grisu";
	public final static String JOB_PROPERTIES_FILE_NAME = "job.grisu";
	public final static String SUBMIT_PROPERTIES_FILE_NAME = "submit.grisu";
	public final static String FILES_PROPERTIES_FILE_NAME = "files.grisu";

	public final static String FILES_DIR_NAME = "files";

	public static File getConfigFile(String file, String config_filename) {
		return getConfigFile(new File(file), config_filename);
	}

	public static Map<String, String> parsePropertiesFile(String file,
			String config_filename) {
		return parsePropertiesFile(new File(file), config_filename);
	}

	public static Map<String, String> parsePropertiesFile(File file,
			String config_filename) {

		if (StringUtils.isNotEmpty(config_filename)) {
			file = getConfigFile(file, config_filename);
		}

		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(file);
			props.load(fis);
		} catch (Exception e1) {
			throw new RuntimeException("Can't parse properties: "
					+ e1.getLocalizedMessage());
		}

		return new HashMap<String, String>((Map) props);

	}

	public static Map<String, String> parsePropertiesFile(File file) {
		return parsePropertiesFile(file, null);
	}

	public static File getConfigFile(File file_or_folder, String config_filename) {

		File result = null;

		if (file_or_folder.isFile()
				&& config_filename.equals(file_or_folder.getName())) {
			result = file_or_folder;
		} else if (file_or_folder.isDirectory()) {
			result = new File(file_or_folder, config_filename);
		} else {
			File parent = file_or_folder.getParentFile();
			result = new File(parent, config_filename);
			if (!result.exists() || !result.isFile() || !result.canRead()) {
				throw new RuntimeException("Can't get config file '"
						+ config_filename + "' for: " + file_or_folder);
			}
		}

		if (!result.exists()) {
			throw new RuntimeException("Config file: "
					+ result.getAbsolutePath() + " does not exist.");
		}
		return result;
	}

	private final String job_name;
	private final File job_folder;

	private final File job_properties_file;
	private JobDescription job_description;
	private final File files_folder;
	private final File files_file;
	private final Set<String> additional_files = new HashSet<String>();

	private final File submit_properties_file;

	private final Map<String, String> properties;

	public GJob(String some_file_or_folder) {
		this(new File(some_file_or_folder));
	}

	public GJob(String some_file_or_folder, String jobname) {
		this(new File(some_file_or_folder), jobname);
	}

	public GJob(File some_file_or_folder) {
		this(some_file_or_folder, null);
	}

	public GJob(File some_file_or_folder, String jobname) {

		String name = some_file_or_folder.getName();
		if (JOB_PROPERTIES_FILE_NAME.equals(name)
				&& some_file_or_folder.isFile()) {
			job_folder = some_file_or_folder.getParentFile();
			job_properties_file = some_file_or_folder;
			submit_properties_file = null;
		} else if (name != null
				&& name.endsWith(SUBMIT_PROPERTIES_FILE_EXTENSION)
				&& some_file_or_folder.isFile()) {
			submit_properties_file = some_file_or_folder;
			Map<String, String> tempProperties = parsePropertiesFile(some_file_or_folder);
			String job = tempProperties.get(JOB_KEY);
			if (StringUtils.isBlank(job)) {
				throw new RuntimeException("No job specified in: "
						+ some_file_or_folder);
			}
			if (job.startsWith("..")) {
				job = new File(submit_properties_file.getParentFile(), job)
						.getAbsolutePath();
			}
			job_properties_file = getConfigFile(job, JOB_PROPERTIES_FILE_NAME);
			job_folder = job_properties_file.getParentFile();
			// job_properties_file = getConfigFile(job_folder,
			// JOB_PROPERTIES_FILE_NAME);
		} else if (some_file_or_folder.isDirectory()) {
			job_folder = some_file_or_folder;
			job_properties_file = getConfigFile(job_folder,
					JOB_PROPERTIES_FILE_NAME);
			File temp = null;
			try {
				temp = getConfigFile(job_folder, SUBMIT_PROPERTIES_FILE_NAME);
			} catch (Exception e) {
				// doesn't matter
			}
			if (temp != null && temp.exists() && temp.isFile()) {
				submit_properties_file = temp;
			} else {
				submit_properties_file = null;
			}
		} else {
			throw new RuntimeException(
					"Can't figure out how to assemble job from: "
							+ some_file_or_folder.getAbsolutePath());
		}

		properties = parsePropertiesFile(job_properties_file, null);
//		String temp = null;
//		if (StringUtils.isBlank(jobname)) {
//
//			temp = properties.get(Constants.JOBNAME_KEY);
//			if (StringUtils.isBlank(temp)) {
//				temp = job_folder.getName();
//			}
//		} else {
//			temp = jobname;
//		}

		job_name = jobname;
		files_folder = new File(job_folder, FILES_DIR_NAME);
		if (new File(job_folder, FILES_PROPERTIES_FILE_NAME).exists()) {
			files_file = new File(job_folder, FILES_PROPERTIES_FILE_NAME);
			try {
				List<String> paths = FileUtils.readLines(files_file);
				additional_files.addAll(paths);
			} catch (IOException e) {
				throw new RuntimeException("Can't read file: "
						+ files_file.getAbsolutePath());
			}

		} else {
			files_file = null;
		}

	}

	public GrisuJob createJobDescription(ServiceInterface si)
			throws JobPropertiesException {
		return createJobDescription(si, null);
	}

	public GrisuJob createJobDescription(ServiceInterface si, File submit_config)
			throws JobPropertiesException {
		return createJobDescription(si, submit_config, true);
	}

	public GrisuJob createJobDescription(ServiceInterface si,
			File submit_config, boolean createJobOnBackend)
			throws JobPropertiesException {

		if (submit_config == null) {
			submit_config = this.submit_properties_file;
		}

		Map<String, String> final_submit_props = Maps
				.newHashMap(this.properties);
		Map<String, String> submit_props = null;
		if (submit_config != null) {
			if (submit_config.isDirectory()) {
				submit_props = parsePropertiesFile(submit_config,
						SUBMIT_PROPERTIES_FILE_NAME);
			} else {
				submit_props = parsePropertiesFile(submit_config);
			}

			final_submit_props.putAll(submit_props);
		}

		String group = final_submit_props.get(GROUP_KEY);
		final_submit_props.remove(GROUP_KEY);
		if (StringUtils.isEmpty(group)) {
			group = final_submit_props.get(Constants.FQAN_KEY);
			final_submit_props.remove(Constants.FQAN_KEY);
		}
		String queue = final_submit_props.get(QUEUE_KEY);
		final_submit_props.remove(QUEUE_KEY);
		if (StringUtils.isBlank(queue)) {
			queue = final_submit_props.get(Constants.SUBMISSIONLOCATION_KEY);
			final_submit_props.remove(Constants.SUBMISSIONLOCATION_KEY);
		}
		String jobname = job_name;

		if (StringUtils.isBlank(jobname)) {
			jobname = final_submit_props.get(Constants.JOBNAME_KEY);
			final_submit_props.remove(Constants.JOBNAME_KEY);

			if (StringUtils.isBlank(jobname)) {
				if (submit_config == null) {
					jobname = job_properties_file.getParentFile().getName();
				} else {
					if (submit_config.isDirectory()) {
						jobname = submit_config.getName();
					} else {
						jobname = submit_config.getParentFile().getName();
					}
				}
				jobname = JobnameHelpers.calculateTimestampedJobname(jobname);
			}
		}

		JobDescription desc = new JobDescription(final_submit_props);
		
		desc.setJobname(jobname);

		if (files_folder.exists()) {
			if (files_folder instanceof File) {
				for (File f : files_folder.listFiles()) {
					desc.addInputFile(f);
				}
			}
		}

		GrisuJob job = GrisuJob.createJobObject(si, desc);

		if (StringUtils.isBlank(job.getApplication())) {
			job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		}

//		job.setTimestampJobname(jobname);

		job.setSubmissionLocation(queue);

		if (createJobOnBackend) {
			job.createJob(group);
		}

		return job;
	}

}
