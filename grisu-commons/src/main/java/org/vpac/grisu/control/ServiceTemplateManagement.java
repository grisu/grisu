

package org.vpac.grisu.control;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Helps to manage all available jsdl templates that are stored in $HOME/.grisu/templates for a grisu client or $HOME_OF_TOMCAT_USER/.grisu/templates_available.
 * 
 * @author Markus Binsteiner
 *
 */
public class ServiceTemplateManagement {
	
	static final Logger myLogger = Logger.getLogger(ServiceTemplateManagement.class.getName());
	
//	/**
//	 * Not used.
//	 * @return
//	 */
//	public static Document[] getAllTemplates() {
//		
//		File[] templates = new File(Environment.TEMPLATE_DIRECTORY).listFiles();
//		Document[] document_templates = new Document[templates.length];
//		
//		for ( int i=0; i<templates.length; i++ ) {
//			document_templates[i] = loadJsdlFile(templates[i]);
//		}
//		
//		return document_templates;
//	}
	

	
//	/**
//	 * Not used.
//	 * @param file
//	 * @return
//	 */
//	private static Document loadJsdlFile(File file) {
//		
//		Document jsdl = null;
//		
//		final String JAXP_SCHEMA_SOURCE
//		= "http://java.sun.com/xml/jaxp/properties/schemaSource";
//		final String JAXP_SCHEMA_LANGUAGE
//		= "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
//		final String W3C_XML_SCHEMA
//		= "http://www.w3.org/2001/XMLSchema";
//		
////		File schemaFile = new File("/home/markus/workspace/nw-core/jsdl.xsd");
//
//		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
//		docBuildFactory.setNamespaceAware(true);
//		docBuildFactory.setValidating(false);
//
//		docBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); // use LANGUAGE here instead of SOURCE
////		docBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
//
//		try {
//			DocumentBuilder documentBuilder = docBuildFactory.newDocumentBuilder();
//			jsdl = documentBuilder.parse(file);
//			//JsdlHelpers.validateJSDL(jsdl);
//			
//		} catch (ParserConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (SAXException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		return jsdl;
//	}
	
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
	
//	public static Document getAllAvailableApplications() {
//		
//		Document output = null;
//		
//		try {
//				DocumentBuilderFactory docFactory = DocumentBuilderFactory
//						.newInstance();
//				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//				output = docBuilder.newDocument();
//		} catch (ParserConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return null;
//		}
//
//		Element root = output.createElement("Applications");
//		
//		output.appendChild(root);
//		
//		File[] templates = new File(Environment.AVAILABLE_TEMPLATES_DIRECTORY).listFiles();
//		
//		for ( File file : templates ) {
////			Document jsdl = loadJsdlFile(file);
//			if ( file.getName().endsWith(".xml") ) {
//				Element applicationName = output.createElement("Application");
//				applicationName.setAttribute("name", file.getName().substring(0, file.getName().lastIndexOf(".xml")));
//				root.appendChild(applicationName);
//			}
//		}
//		
//		
//		return output;
//	}
	
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
