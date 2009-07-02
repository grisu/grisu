package org.vpac.grisu.client.gridTests;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class GridTestCommandlineOptions {
	
	private CommandLine line = null;
	private HelpFormatter formatter = new HelpFormatter();
	private Options options = null;
	
	private String fqan;
	private String[] applications;
	private String[] filters = new String[]{};
	private String myproxyUsername;
	private String url;
	private String output;
	
	public String getFqan() {
		return fqan;
	}

	public String[] getApplications() {
		return applications;
	}

	public String getMyproxyUsername() {
		return myproxyUsername;
	}
	
	public String getServiceInterfaceUrl() {
		return url;
	}
	
	public String getOutput() {
		return output;
	}
	
	public String[] getFilters() {
		return filters;
	}


	public GridTestCommandlineOptions(String[] args) {
		this.formatter.setLongOptPrefix("--");
		this.formatter.setOptPrefix("-");
		this.options = getOptions();
		parseCLIargs(args);
	}
	
	private void parseCLIargs(String[] args) {
		
		// create the parser
		CommandLineParser parser = new PosixParser();
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
			formatter.printHelp("grisu-grid-test", this.options);
			System.exit(1);
		}
		
		if (!line.hasOption("applications")) {
			System.err.println("No applications specified.");
			formatter.printHelp("grisu-grid-test", this.options);
			System.exit(1);
		} else {
			applications = line.getOptionValue("applications").split(",");
		}
		
		if (!line.hasOption("vo")) {
			System.err.println("No vo specified.");
			formatter.printHelp("grisu-grid-test", this.options);
			System.exit(1);
		} else {
			fqan = line.getOptionValue("vo");
		}
		
		if (line.hasOption("username")) {
			myproxyUsername = line.getOptionValue("username");
		}
		
		if (line.hasOption("output")) {
			output = line.getOptionValue("output");
		}
		
		if (line.hasOption("filter")) {
			filters = line.getOptionValue("filter").split(","); 
		}
		
	}
	

	// helper methods

	// option with long name, no arguments
	private static  Option createOption(String longName,String description){
		return OptionBuilder.withLongOpt(longName).withDescription(description).create();
	}

	// option with long name, has arguments
	private  static Option createOptionWithArg(String longName, String description){
		return OptionBuilder.withArgName(longName).hasArg().withLongOpt(longName).withDescription(description).create();
	}

	// option with long name, short name, no arguments
	private static Option createOption(String  longName, String shortName, String description){
		return OptionBuilder.withLongOpt(longName).withDescription(description).create(shortName);
	}

	// option with  long name,short name and argument
	private static Option  createOptionWithArg(String longName, String shortName, String description){
		return OptionBuilder.withArgName(longName).hasArg().withLongOpt(longName).withDescription(description).create(shortName);
	}
	
	private static Options getOptions() {
		
		Options options = null;
		
		// common options
		Option apps = createOptionWithArg("applications", "a", "all applications to test, seperated using commas");				    
		Option myProxyUsername = createOptionWithArg("username", "u", "the myproxy username to use");
		Option fqan = createOptionWithArg("vo", "v", "the vo to use");
		Option outputFile = createOptionWithArg("output", "o", "the output file");
		Option filter = createOptionWithArg("filter", "f", "(comma-seperated) filters to exclude certain queues");
		
		options = new Options();
		options.addOption(apps);
		options.addOption(myProxyUsername);
		options.addOption(fqan);
		options.addOption(outputFile);
		options.addOption(filter);
		
		return options;
	}
	
}
