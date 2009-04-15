package org.vpac.grisu.client.view.grisuclient;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class GrisuClientCommandlineProperties implements GrisuClientProperties {

	static final Logger myLogger = Logger
			.getLogger(GrisuClient.class.getName());

//	private final static String PARAMETER = "PARAMETER";

	// common options
	public final static String SERVICE_INTERFACE_URL_OPTION = "serviceInterfaceUrl";
//	public final static String READ_PASSWORD_FROM_STDIN = "readPasswordFromStdin";
	public final static String MODE_OPTION = "mode";
	public final static String FORCE_ALL_MODE_PARAMETER = "force-all";
	public final static String ALL_MODE_PARAMETER = "all";
	public final static String SUBMIT_MODE_PARAMETER = "submit";
	public final static String STATUS_MODE_PARAMETER = "check";
	// public final static String STAGEOUT_MODE_PARAMETER = "stageout";
	public final static String JOIN_MODE_PARAMETER = "join";
		
	public final static String VERBOSE_OPTION = "verbose";
	public final static String DEBUG_OPTION = "debug";


	public final static String MYPROXY_USERNAME_OPTION = "myproxy_username";

	public final static String KILL_POSSIBLY_EXISTING_JOB = "killExistingJob";
	
	public final static String TIME_TO_WAIT_BEFORE_RECHECK_STATUS = "statusRecheckInterval";
	public final static String STAGEOUT_OPTION = "stageout";
	public final static String CLEAN_OPTION = "clean";
	public final static String FORCE_CLEAN_MODE = "force-clean";
	// submit options

	// stageout options
//	public final static String OUTPUTFILENAMES_OPTION = "outputFileNames";
	public final static String STAGEOUTDIRECTORY_OPTION = "stageoutDirectory";

	private String serviceInterfaceUrl = "https://ngportal.vpac.org/grisu-ws/services/grisu";

	private Set<String> failedParameters = null;

	private CommandLine line = null;
	private HelpFormatter formatter = new HelpFormatter();
	private Options options = null;

	private String mode = null;
	private boolean stageout = false;
	private boolean clean = false;

	private int recheckInterval = -1;

	private String[] outputFileNames = null;
	private URI stageOutDirectory = null;

	public GrisuClientCommandlineProperties(String[] args) {
		this.options = getOptions();
		parseCLIargs(args);

	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getStageoutDirectory()
	 */
	public String getStageoutDirectory() {
		return line.getOptionValue(STAGEOUTDIRECTORY_OPTION);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getServiceInterfaceUrl()
	 */
	public String getServiceInterfaceUrl() {
		return serviceInterfaceUrl;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getMode()
	 */
	public String getMode() {
		return mode;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#stageOutResults()
	 */
	public boolean stageOutResults() {
		return stageout;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#cleanAfterStageOut()
	 */
	public boolean cleanAfterStageOut() {
		return clean;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#verbose()
	 */
	public boolean verbose() {
		return line.hasOption(VERBOSE_OPTION);
	}
	
	public boolean debug() {
		return line.hasOption(DEBUG_OPTION);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#killPossiblyExistingJob()
	 */
	public boolean killPossiblyExistingJob() {
		return line.hasOption(KILL_POSSIBLY_EXISTING_JOB);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getRecheckInterval()
	 */
	public int getRecheckInterval() {
		return recheckInterval;
	}
	
	private void parseCLIargs(String[] args) {
		
		// create the parser
		CommandLineParser parser = new GnuParser();
		
		try {
			// parse the command line arguments
			line = parser.parse(this.options, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}
		
		String[] arguments = line.getArgs();
		
		if ( arguments.length > 0 ) {
			if ( arguments.length == 1 ) {
				System.err.println("Unknown argument: "+arguments[0]);
			} else {
				StringBuffer buf = new StringBuffer();
				for ( String arg : arguments ) {
					buf.append(arg+" ");
				}	
				System.err.println("Unknown argument: "+buf.toString());
			}
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(MODE_OPTION)) {
			// System.err
			// .println("Please specify the mode you want to use: all|submit|status|stageout|clean|join. \"all\" is the default mode.");
			// formatter.printHelp("grisu-batch", this.options);
			// System.exit(1);
			mode = ALL_MODE_PARAMETER;
		} else {
			// for the different modes
			mode = line.getOptionValue(MODE_OPTION);
		}

		if (SUBMIT_MODE_PARAMETER.equals(mode)) {

			checkSubmitModeParameters();

		} else if (STATUS_MODE_PARAMETER.equals(mode)) {

			checkStatusModeParameters();

		} else if (JOIN_MODE_PARAMETER.equals(mode)) {

			checkJoinModeParameters();


		} else if (ALL_MODE_PARAMETER.equals(mode) || FORCE_ALL_MODE_PARAMETER.equals(mode)) {

			checkAllModeParameters();


		} else {

			System.err.println("Mode " + mode + " not supported.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		// common options
		checkCommonOptions();
		
		// other options
		if ( line.hasOption(TIME_TO_WAIT_BEFORE_RECHECK_STATUS) ) {
		try {
			recheckInterval = Integer
					.parseInt(line
							.getOptionValue(TIME_TO_WAIT_BEFORE_RECHECK_STATUS));
		} catch (NumberFormatException e) {
			System.err.println("Please use an integer for the recheck interval time");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}
		}

	}

	private void checkCommonOptions() {

		if (!line.hasOption(CommandlineProperties.JOBNAME)) {
			System.err.println("Please specify the jobname.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (line.hasOption(SERVICE_INTERFACE_URL_OPTION)) {
			serviceInterfaceUrl = line
					.getOptionValue(SERVICE_INTERFACE_URL_OPTION);
		}
		
		if ( ! line.hasOption(MYPROXY_USERNAME_OPTION) ) {
			System.err.println("Please specify your myproxy username.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

	}

	private void checkSubmitModeParameters() {

		if (!line
				.hasOption(CommandlineProperties.SUBMISSION_LOCATION_OPTION)) {
			System.err
					.println("Please specify the submission location you want to use.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(CommandlineProperties.VO_OPTION)) {
			System.err.println("Please specify the VO you want to use.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(CommandlineProperties.COMMAND_OPTION)) {
			System.err.println("Please specify the comand you want to run.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(CommandlineProperties.WALLTIME_OPTION)) {
			System.err.println("Please specify the walltime.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(CommandlineProperties.CPUS_OPTION)) {
			System.err.println("Please specify the number of cpus.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(CommandlineProperties.INPUTFILEPATH_OPTION)) {
			myLogger.debug("No input files to stage for this job.");
		} else {
			String inputFileString = line.getOptionValue(CommandlineProperties.INPUTFILEPATH_OPTION);
			if ( !StringUtils.isEmpty(inputFileString) ) {
			String[] input = inputFileString.split(",");
			for ( String filepath : input ) {
				
				File file = new File(filepath);
				if ( ! file.exists() ) {
					System.err.println("Input file "+filepath+" doesn't exist.");
					System.exit(1);
				}
			}
			}
			
		}

		try {
			Integer
					.parseInt(line
							.getOptionValue(CommandlineProperties.WALLTIME_OPTION));
		} catch (NumberFormatException e) {
			System.err.println("Please use an integer for the walltime");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		try {
			Integer.parseInt(line
					.getOptionValue(CommandlineProperties.CPUS_OPTION));
		} catch (NumberFormatException e) {
			System.err.println("Please use an integer for the no. of cpus");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}
		
	}

	private void checkStatusModeParameters() {

		checkStageoutModeParameters();
		
	}

	private void checkStageoutModeParameters() {

		if (line.hasOption(STAGEOUT_OPTION)) {

//			if (!line.hasOption(OUTPUTFILENAMES_OPTION)) {
//				myLogger
//						.debug("No files to stageout specified. Using \"stdout.txt\"");
//				outputFileNames = new String[] { "stdout.txt" };
//			} else {
//				outputFileNames = line.getOptionValue(OUTPUTFILENAMES_OPTION)
//						.split(",");
//			}

			if (!line.hasOption(STAGEOUTDIRECTORY_OPTION)) {
				myLogger
						.debug("No stageout directory specified. Using current directory.");
				stageOutDirectory = new File(".").toURI();

			} else {
				stageOutDirectory = new File(line
						.getOptionValue(STAGEOUTDIRECTORY_OPTION)).toURI();
			}
			
			// check whether the dir can be used
			File dir = new File(stageOutDirectory);
			if ( dir.exists() ) {
				if (! dir.canWrite() ) {
					myLogger.debug("Can't write to stageout directory.");
					System.err.println("Can't write to stageout directory.");
					System.exit(1);
				}
			} else {
				System.err.println("Stageout directory doesn't exist.");
				System.exit(1);
			}
			
			stageout = true;
		} else {
			stageout = false;
		}
		
		if ( line.hasOption(CLEAN_OPTION) ) {
			clean = true;
		} else {
			clean = false;
		}

	}


	private void checkJoinModeParameters() {

		checkStatusModeParameters();
		checkStageoutModeParameters();

	}

	private void checkAllModeParameters() {

		checkSubmitModeParameters();
		checkStatusModeParameters();
		checkStageoutModeParameters();
//		checkCleanModeParameters();

	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getCommandLine()
	 */
	public CommandLine getCommandLine() {
		return line;
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.client.view.grisuclient.GrisuClientProperties#getMyProxyUsername()
	 */
	public String getMyProxyUsername() {
		if (!line.hasOption(MYPROXY_USERNAME_OPTION)) {
			myLogger.debug("No myproxy username specified...");
			return null;
		}
		return line.getOptionValue(MYPROXY_USERNAME_OPTION);
	}

	// helper methods

	private static Options getOptions() {

		Options options = null;

		// common option
		Option serviceInterfaceUrl = OptionBuilder.withArgName(
				SERVICE_INTERFACE_URL_OPTION).hasArg().withDescription(
				"the serviceinterface to connect to (optional, default: https://ngportal.vpac.org/grisu-ws/services/grisu)").create(
				SERVICE_INTERFACE_URL_OPTION);
		Option mode = OptionBuilder
				.withArgName(MODE_OPTION)
				.hasArg()
				.withDescription(
						"the mode you want to use: all|submit|check|join|force-clean (optional, default: all)")
				.create(MODE_OPTION);
		Option baseName = OptionBuilder.withArgName(
				CommandlineProperties.JOBNAME).hasArg().withDescription(
				"the name for the job (required)").create(
				CommandlineProperties.JOBNAME);
//		Option readPasswordFromStdin = OptionBuilder.withArgName(READ_PASSWORD_FROM_STDIN)
//				.withDescription("don't prompt for password but read from stdin, (optional)")
//				.create(READ_PASSWORD_FROM_STDIN);

		// common optional options
		Option myproxy_username = OptionBuilder.withArgName(
				MYPROXY_USERNAME_OPTION).hasArg().withDescription(
				"myproxy username (required)").create(MYPROXY_USERNAME_OPTION);
		Option email = OptionBuilder
				.withArgName(CommandlineProperties.EMAIL_OPTION)
				.hasArg()
				.withDescription(
						"the email address to send status reports to (optional)")
				.create(CommandlineProperties.EMAIL_OPTION);
		Option verbose = OptionBuilder.withArgName(VERBOSE_OPTION).withDescription("verbose output (optional)").create(VERBOSE_OPTION);
		Option debug = OptionBuilder.withArgName(DEBUG_OPTION).withDescription("more debug output to $HOME/.grisu/grisu.debug (optional)").create(DEBUG_OPTION);	
		
		// submit options
		Option submissionLocation = OptionBuilder
				.withArgName(
						CommandlineProperties.SUBMISSION_LOCATION_OPTION)
				.hasArg()
				.withDescription(
						"the submission location (e.g. dque@brecca-m:ng2.vpac.monash.edu.au), (required for modes all|submit)")
				.create(CommandlineProperties.SUBMISSION_LOCATION_OPTION);
		Option vo = OptionBuilder.withArgName(
				CommandlineProperties.VO_OPTION).hasArg()
				.withDescription("the vo to use (required for modes all|submit)").create(
						CommandlineProperties.VO_OPTION);
		Option command = OptionBuilder.withArgName(
				CommandlineProperties.COMMAND_OPTION).hasArg()
				.withDescription("the commandline to run remotely (required for modes all|submit)").create(
						CommandlineProperties.COMMAND_OPTION);
		Option baseInputFilePath = OptionBuilder.withArgName(
				CommandlineProperties.INPUTFILEPATH_OPTION).hasArg()
				.withDescription("the inputfiles, seperated with a comma (optional)").create(
						CommandlineProperties.INPUTFILEPATH_OPTION);
		Option walltime = OptionBuilder.withArgName(
				CommandlineProperties.WALLTIME_OPTION).hasArg()
				.withDescription("the walltime in seconds (required for modes all|submit)").create(
						CommandlineProperties.WALLTIME_OPTION);
		Option cpus = OptionBuilder.withArgName(
				CommandlineProperties.CPUS_OPTION).hasArg()
				.withDescription("the number of cpus to run the job with (required for modes all|submit)")
				.create(CommandlineProperties.CPUS_OPTION);
		Option stdout = OptionBuilder.withArgName(
				CommandlineProperties.STDOUT_OPTION).hasArg()
				.withDescription("the name of the stdout file (optional)")
				.create(CommandlineProperties.STDOUT_OPTION);
		Option stderr = OptionBuilder.withArgName(
				CommandlineProperties.STDERR_OPTION).hasArg()
				.withDescription("the name of the stderr file (optional)")
				.create(CommandlineProperties.STDERR_OPTION);
		Option module = OptionBuilder
				.withArgName(CommandlineProperties.MODULE_OPTION)
				.hasArg()
				.withDescription("the module to load on the cluster (optional)")
				.create(CommandlineProperties.MODULE_OPTION);
		Option killExisting = OptionBuilder.withArgName(KILL_POSSIBLY_EXISTING_JOB)
				.withDescription("specify this if you want to kill & clean a possibly existing job with the same name before submitting the job (optional)")
				.create(KILL_POSSIBLY_EXISTING_JOB);
		Option recheckIntervall = OptionBuilder.withArgName(TIME_TO_WAIT_BEFORE_RECHECK_STATUS).hasArg()
				.withDescription("time to wait inbetween status checks in seconds (default: 600, optional for all|join)")
				.create(TIME_TO_WAIT_BEFORE_RECHECK_STATUS);
		// stageout options
		Option stageout = OptionBuilder
				.withArgName(STAGEOUT_OPTION)
				.withDescription(
						"if you want to stageout the results if the job is finished (optional for modes all|check|join)")
				.create(STAGEOUT_OPTION);
		Option clean = OptionBuilder.withArgName(CLEAN_OPTION)
				.withDescription("specify this if you want to clean the job after a successful stageout (optional for modes all|check|join)").create(CLEAN_OPTION);
//		Option stageOutFileNames = OptionBuilder
//				.withArgName(OUTPUTFILENAMES_OPTION)
//				.hasArg()
//				.withDescription(
//						"the filenames to stage out, seperated with commas, relative to the jobdirectory")
//				.create(OUTPUTFILENAMES_OPTION);
		Option stageOutDirectory = OptionBuilder.withArgName(
				STAGEOUTDIRECTORY_OPTION).hasArg().withDescription(
				"the local directory to stage out the files to (default: current directory, optional for modes all|status|join)").create(
				STAGEOUTDIRECTORY_OPTION);

		options = new Options();
		options.addOption(serviceInterfaceUrl);
		options.addOption(myproxy_username);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(email);
		options.addOption(mode);
		options.addOption(submissionLocation);
		options.addOption(vo);
		options.addOption(baseName);
//		options.addOption(readPasswordFromStdin);
		options.addOption(command);
		options.addOption(baseInputFilePath);
		options.addOption(walltime);
		options.addOption(cpus);
		options.addOption(stdout);
		options.addOption(stderr);
		options.addOption(module);
		options.addOption(killExisting);
		options.addOption(recheckIntervall);
		options.addOption(stageout);
		options.addOption(clean);
//		options.addOption(stageOutFileNames);
		options.addOption(stageOutDirectory);

		return options;

	}

}
