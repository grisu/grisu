package grisu.frontend.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.python.google.common.collect.Maps;

public class CommonCLIParser {

	public enum OPTIONS {
		NOLOGIN, BACKEND, REST, LOGOUT, LOGIN
	}

	public static Map<OPTIONS, Object> parse(String[] args) {
		final CommandLineParser parser = new PosixParser();
		CommandLine cl = null;
		final Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("nologin")
				.withDescription("disables login at the start").create('n'));
		options.addOption(OptionBuilder
				.withLongOpt("login")
				.withDescription(
						"asks for login details if not logged in already")
						.create('l'));
		options.addOption(OptionBuilder
				.withLongOpt("logout")
				.withDescription(
						"destroys a possible grid session and exits straight away")
						.create());
		// options.addOption(OptionBuilder.withLongOpt("credential").hasArg()
		// .withArgName("credential-file").withDescription("a file containing credential information")
		// .create('c'));
		options.addOption(OptionBuilder.withLongOpt("backend").hasArg()
				.withArgName("backend").withDescription("change backend")
				.create('b'));
		// options.addOption(OptionBuilder.withLongOpt("username").hasArg()
		// .withArgName("username")
		// .withDescription("institution or myproxy username").create("u"));
		// options.addOption(OptionBuilder.withLongOpt("institution").hasArg()
		// .withArgName("institution_name")
		// .withDescription("institution name").create("i"));
		// options.addOption(OptionBuilder.withLongOpt("x509")
		// .withDescription("x509 certificate login").create("x"));
		// options.addOption(OptionBuilder.withLongOpt("credential").hasArg()
		// .withDescription("credential config file").create("c"));
		// options.addOption(OptionBuilder.withLongOpt("myproxy").hasArg()
		// .withDescription("MyProxy host to use)").create("m"));

		try {
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

		Map<OPTIONS, Object> result = Maps.newHashMap();
		if (cl.hasOption('n')) {
			result.put(OPTIONS.NOLOGIN, true);
		} else {
			result.put(OPTIONS.NOLOGIN, false);
		}
		if (cl.hasOption('b')) {
			result.put(OPTIONS.BACKEND, cl.getOptionValue('b'));
		} else {
			result.put(OPTIONS.BACKEND, "testbed");
		}
		if (cl.hasOption("logout")) {
			result.put(OPTIONS.LOGOUT, true);
		} else {
			result.put(OPTIONS.LOGOUT, false);
		}
		if (cl.hasOption('l')) {
			result.put(OPTIONS.LOGIN, true);
		} else {
			result.put(OPTIONS.LOGIN, false);
		}

		List<String> rest = cl.getArgList();
		result.put(OPTIONS.REST, rest);

		return result;
	}

}
