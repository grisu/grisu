package grisu.frontend.control;

import com.google.common.collect.Maps;
import grisu.control.JobnameHelpers;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.PackageFileHelper;
import grisu.jcommons.view.html.VelocityUtils;
import grisu.model.job.JobDescription;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class GJob {

	public static void main(String[] args) throws Exception {

        if ( args.length != 1 ) {
            System.err.println("Wrong number of arguments, only path to job is allowed.");
        }
        String path = args[0];


		LoginManager.initGrisuClient("gjob");

        System.out.println("Logging in...");
		ServiceInterface si = LoginManager.login("nesi", false);
		// ServiceInterface si = LoginManager.login("local", false);


        System.out.println("Reading job description...");
        GJob gjob = new GJob(path);
//		GJob gjob = new GJob(
//				"/data/src/config/end-to-end-tests/Gaussian/jobs/H2O");
		// GJob gjob = new
		// GJob("/data/src/config/end-to-end-tests/generic/jobs/stdinput_test");

		GrisuJob job = gjob.createJobDescription(si);
        job.setCompress_input_files(true);

		job.submitJob();

	}



    public static void createJobStub(File parentFolder, String jobname, String commandline, String walltime, String queue, String group, String application, String version, Integer cpus, String memory, String virtualMemory) {
        Map<String, Object> properties = Maps.newHashMap();
        if (StringUtils.isBlank(commandline)) {
            properties.put("commandline", "cat inputFile.txt");
        }
        if ( cpus == null || cpus < 1 ) {
            properties.put("cpus", "//cpus = 1");
        } else {
            properties.put("cpus", "cpus = "+cpus.toString());
        }
        if ( StringUtils.isBlank(application)) {
            properties.put("application", "//application = generic");
        } else {
            properties.put("application", "application = "+application);
        }
        if ( StringUtils.isBlank(version)) {
            properties.put("applicationVersion", "//applicationVersion = generic");
        } else {
            properties.put("applicationVersion", "applicationVersion = "+version);
        }
        if (StringUtils.isBlank(memory)) {
            properties.put("memory", "//memory = 2g");
        } else {
            properties.put("memory", "memory = "+memory);
        }
        if (StringUtils.isBlank(virtualMemory)) {
            properties.put("virtualMemory", "//virtualMemory = 2g");
        } else {
            properties.put("virtualMemory", "virtualMemory = "+virtualMemory);
        }

        if (StringUtils.isBlank(walltime)) {
            properties.put("walltime", "//walltime = 5m");
        } else {
            properties.put("walltime", "walltime = "+walltime);
        }


        if (StringUtils.isBlank(queue)) {
            properties.put("queue", "queue = pan:gram.uoa.nesi.org.nz");
        } else {
            properties.put("queue", "queue = "+queue);
        }

        if (StringUtils.isBlank(group)) {
            properties.put("group", "group = /nz/nesi");
        } else {
            properties.put("group", "group = "+group);
        }


        createJobStub(parentFolder, jobname, properties);
    }

    public static void createJobStub(String parentFolder, String jobname) {
        createJobStub(new File(parentFolder), jobname);
    }

    public static void createJobStub(File parentFolder, String jobname) {
        createJobStub(parentFolder, jobname, null, null, null, null, null, null, null, null, null);
    }

    public static void createJobStub(File parentFolder, String jobname, Map<String, Object> properties) {

        File jobFolder = new File(parentFolder, jobname);
        if ( jobFolder.exists() ) {
            throw new RuntimeException("Jobfolder '"+jobFolder.toString()+"'already exists.");
        }

        jobFolder.mkdirs();

        if (! jobFolder.exists() ) {
            throw new RuntimeException("Could not create jobfolder: "+jobFolder.toString());
        }

		File temp = null;
        File jobFile = new File(jobFolder, "job.grisu");

        String configContent = VelocityUtils.render("job.grisu", properties);

        try {
            FileUtils.writeStringToFile(jobFile, configContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File submitFile = new File(jobFolder, "submit.grisu");
        configContent = VelocityUtils.render("submit.grisu", properties);
        try {
            FileUtils.writeStringToFile(submitFile, configContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File filesFolder = new File(jobFolder, GJob.FILES_DIR_NAME);
        filesFolder.mkdirs();

        if ( ! filesFolder.exists() ) {
            throw new RuntimeException("Could not create input files folder: "+filesFolder.toString());
        }

        temp = PackageFileHelper.getFile("inputFile.txt");

        try {
            FileUtils.copyFile(temp, new File(filesFolder, temp.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Could not copy input file to: "+filesFolder.toString());
        }


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
            if ( job == null ) {
                job = ".";
            }
			if (StringUtils.isBlank(job)) {
				throw new RuntimeException("No job specified in: "
						+ some_file_or_folder);
			}
            if (job.startsWith("..")) {
				job = new File(submit_properties_file.getParentFile(), job)
						.getAbsolutePath();
			} else if ( ".".equals(job) ) {
                job = new File(submit_properties_file.getParentFile(), JOB_PROPERTIES_FILE_NAME).getAbsolutePath();
            } else {
                job = new File(submit_properties_file.getParentFile(), job).getAbsolutePath();
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
        if ( additional_files != null ) {
            for ( String path : additional_files ) {
                desc.addInputFileUrl(path);
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

    public String getJobname() {
        if (job_name == null ) {
            return job_properties_file.getAbsolutePath();
        }
        return job_name;
    }

}
