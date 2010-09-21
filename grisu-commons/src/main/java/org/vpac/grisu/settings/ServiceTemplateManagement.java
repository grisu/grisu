package org.vpac.grisu.settings;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Helps to manage all available jsdl templates that are stored in
 * $HOME/.grisu/templates for a grisu client or
 * $HOME_OF_TOMCAT_USER/.grisu/templates_available.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class ServiceTemplateManagement {

	static final Logger myLogger = Logger
			.getLogger(ServiceTemplateManagement.class.getName());

	/**
	 * Checks the $HOME_OF_TOMCAT_USER/.grisu/templates_available directories
	 * for xml files and returns a list of all of the filenames (without
	 * .xml-extension).
	 * 
	 * @return the list of all available application templates
	 */
	public static String[] getAllAvailableApplications() {

		final File[] templates = new File(
				Environment.getAvailableTemplatesDirectory()).listFiles();
		final Set<String> allAvalableTemplates = new TreeSet<String>();

		if (templates == null) {
			return new String[] {};
		}

		for (final File file : templates) {
			if (file.getName().endsWith(".template")) {
				allAvalableTemplates.add(file.getName().substring(0,
						file.getName().lastIndexOf(".template")));
			}
		}

		return allAvalableTemplates.toArray(new String[allAvalableTemplates
				.size()]);
	}

	public static String getTemplate(String name) {

		final File file = new File(
				Environment.getAvailableTemplatesDirectory(), name
						+ ".template");

		String temp;
		try {
			temp = FileUtils.readFileToString(file);
		} catch (final IOException e) {
			return null;
		}

		return temp;
	}

	// /**
	// * Loads the jsdl template from the .grisu/templates_available directory
	// * into a {@link Document}.
	// *
	// * @param name
	// * the name of the jsdl template
	// * @return the template as xml Document or null if it could not be
	// * found/loaded
	// */
	// public static Document getAvailableTemplate(final String name) {
	//
	// Document jsdl_template = null;
	// try {
	// jsdl_template = SeveralXMLHelpers.fromString(FileHelpers
	// .readFromFile(new File(Environment
	// .getAvailableTemplatesDirectory()
	// + File.separator + name + ".xml")));
	// } catch (Exception e) {
	// myLogger
	// .error("Could not find/load jsdl template for application: "
	// + name + ": " + e.getMessage());
	// return null;
	// }
	// return jsdl_template;
	// }

	private ServiceTemplateManagement() {
	}

}
