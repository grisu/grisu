package org.vpac.grisu.client.view.grisuclient;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.control.utils.CommandlineHelpers;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.fs.model.MountPoint;

public class CommandlineProperties extends AbstractJobProperties implements JobProperties {
	
	public final static String DEFAULT_APP_NAME = "generic";
	public final static String DEFAULT_STDOUT = "stdout.txt";
	public final static String DEFAULT_STDERR = "stderr.txt";
	
	public final static String JOBNAME = "jobName";
	public final static String VO_OPTION = "vo";
	public final static String SUBMISSION_LOCATION_OPTION = "submissionLocation";
	public final static String COMMAND_OPTION = "command";
	public final static String WALLTIME_OPTION = "walltime";
	public final static String CPUS_OPTION = "cpus";
	public final static String INPUTFILEPATH_OPTION = "inputFilePath";
	public final static String EMAIL_OPTION = "email";
	public final static String STDOUT_OPTION = "stdout";
	public final static String STDERR_OPTION = "stderr";
	public final static String MODULE_OPTION = "module";
	
	public final static String APPLICATION_OPTION = "application";
	

	private CommandLine line = null;	
	
	private String submissionLocation = null;
	private String jobname = null;
	
	public CommandlineProperties(ServiceInterface serviceInterface, CommandLine line) {
		this.serviceInterface = serviceInterface;
		this.line = line;
	}
	
	public String getApplicationName() {
		
		String appName = line.getOptionValue(APPLICATION_OPTION);
		
		if ( StringUtils.isEmpty(appName) ) {
			appName = DEFAULT_APP_NAME;
		}
		
		return appName;
	}

	public String[] getArguments() {
		String commandline = line.getOptionValue(COMMAND_OPTION);
		
		if ( StringUtils.isEmpty(commandline) ) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}
		
		ArrayList<String> args;
		try {
			args = CommandlineHelpers.extractArgumentsFromCommandline(commandline);
		} catch (ParseException e) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}
		
		return args.toArray(new String[]{});
	}

	public String getEmailAddress() {
		String email = line.getOptionValue(EMAIL_OPTION);
		
		//TODO check whether valid emailaddress
		
		if ( email == null ) 
			return "";
		else
			return email;
	}

	public String getExecutablesName() {

		String commandline = line.getOptionValue(COMMAND_OPTION);
		
		if ( StringUtils.isEmpty(commandline) ) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}
		
		String executable = null;
		try {
			executable = CommandlineHelpers.extractExecutable(commandline);
		} catch (ParseException e) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}
		
		return executable;
	}

	public String[] getInputFiles() {
		
		String inputFilesString = line.getOptionValue(INPUTFILEPATH_OPTION);
		
		String[] result = null;
		if ( !StringUtils.isEmpty(inputFilesString) ) {
		String[] input = inputFilesString.split(",");
		result = new String[input.length];
		for ( int i=0; i<result.length; i++ ) {
			
			File file = new File(input[i]);
			result[i] = file.toURI().toString();
			
		}
		} else {
			result = new String[]{};
		}
		
		return result;
	}

	public String getJobname() {
		
		if ( jobname == null ) {
			jobname = line.getOptionValue(JOBNAME);
		
		if ( StringUtils.isEmpty(jobname) ) {
			throw new InvalidOptionException(JOBNAME, jobname);
		}
		
//			jobname = jobname+new Date().getTime();
		}
		return jobname;
	}

	public String getModule() {
		
		String module = line.getOptionValue(MODULE_OPTION);
		
		if ( module == null ) 
			return "";
		else 
			return module;
	}

	public int getNoCPUs() {
		int noCPUs = -1;
		try {
			noCPUs = Integer.parseInt(line.getOptionValue(CPUS_OPTION));
		} catch (NumberFormatException e) {
			throw new InvalidOptionException(CPUS_OPTION, line.getOptionValue(CPUS_OPTION));
		}
		return noCPUs;
	}

	public String getStderr() {
		
		String stderr = line.getOptionValue(STDERR_OPTION);
		
		if ( stderr == null ) 
			return DEFAULT_STDERR;
		else 
			return stderr;
	}

	public String getStdout() {
		
		String stdout = line.getOptionValue(STDOUT_OPTION);
		
		if ( stdout == null ) 
			return DEFAULT_STDOUT;
		else
			return stdout;
	}

	public String getSubmissionLocation() {
		
		if ( submissionLocation == null ) {
		
			submissionLocation = line.getOptionValue(SUBMISSION_LOCATION_OPTION);
		
		if ( StringUtils.isEmpty(submissionLocation) ) {
			throw new InvalidOptionException(SUBMISSION_LOCATION_OPTION, submissionLocation);
		}
		}
		
		return submissionLocation;

	}

	public int getWalltimeInSeconds() {
		
		int walltimeInSeconds = -1;
		try {
			walltimeInSeconds = Integer.parseInt(line
					.getOptionValue(WALLTIME_OPTION));
		} catch (NumberFormatException e) {
			throw new InvalidOptionException(WALLTIME_OPTION, line.getOptionValue(WALLTIME_OPTION));
		}

		return walltimeInSeconds;
	}

	public String getVO() {
		
		String vo = line.getOptionValue(VO_OPTION);
		
		return vo;
	}


}
