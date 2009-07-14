

package org.vpac.grisu.control.utils;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.Environment;
import org.w3c.dom.Document;

/**
 * Helps to manage all available jsdl templates that are stored in $HOME/.grisu/templates for a grisu client or $HOME_OF_TOMCAT_USER/.grisu/templates_available.
 * 
 * @author Markus Binsteiner
 *
 */
public class ServiceTemplateManagement {
	
	static final Logger myLogger = Logger.getLogger(ServiceTemplateManagement.class.getName());
	
	
	/**
	 * Loads the jsdl template from the .grisu/templates_available directory into a {@link Document}.
	 * @param name the name of the jsdl template
	 * @return the template as xml Document or null if it could not be found/loaded
	 */
	public static Document getAvailableTemplate(String name) {
		
		Document jsdl_template = null;
		try {
			jsdl_template = SeveralXMLHelpers.fromString(FileHelpers.readFromFile(new File(Environment.AVAILABLE_TEMPLATES_DIRECTORY+File.separator+name+".xml")));
		} catch (Exception e) {
			myLogger.error("Could not find/load jsdl template for application: "+name+": "+e.getMessage());
			return null;
		}
		return jsdl_template;
	}
	
	
	/**
	 * Checks the $HOME_OF_TOMCAT_USER/.grisu/templates_available directories for xml files and returns a list of all of the filenames (without .xml-extension).
	 * @return the list of all available application templates
	 */
	public static String[] getAllAvailableApplications() {
		
		File[] templates = new File(Environment.AVAILABLE_TEMPLATES_DIRECTORY).listFiles();
		Set<String> allAvalableTemplates = new TreeSet<String>();
		
		for ( File file : templates ) {
			if ( file.getName().endsWith(".xml") ) {
				allAvalableTemplates.add(file.getName().substring(0, file.getName().lastIndexOf(".xml")));
			}
		}
		
		return allAvalableTemplates.toArray(new String[allAvalableTemplates.size()]);
	}
	
}
