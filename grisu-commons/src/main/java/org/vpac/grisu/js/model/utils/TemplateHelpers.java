

package org.vpac.grisu.js.model.utils;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TemplateHelpers {
	
	static final Logger myLogger = Logger.getLogger(TemplateHelpers.class.getName());
	
	private static XPath xpath = getXPath();
	
	private static final XPath getXPath(){
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new JSDLNamespaceContext());
		return xpath;
	}
	
	/**
	 * Returns a list of all nodes with template tags in them. These have to be displayed to the user and the input
	 * may have to be processed after user input.
	 * @param jsdl the jsdl template document
	 * @return a list of all elements that have template tags in them
	 */
	public static ArrayList<Element> getTemplateNodes(Document jsdl) {
		
		String expression = "//@template";
		NodeList resultNodes = null;
		try {
			resultNodes = (NodeList)xpath.evaluate(expression, jsdl, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			myLogger.warn("No JobName node in jsdl file yet.");
			// that's ok if we want to set the jobname
		}
		
		ArrayList<Element> result = new ArrayList<Element>();
		for ( int i=0; i<resultNodes.getLength(); i++) {
			
			Attr node = (Attr)resultNodes.item(i);
			
			Element element = node.getOwnerElement();
			result.add(element);
		}
		return result;

	}
	
	/**
	 * This method returns a list of all modules that should be used to render this jsdl template on the ui. A module claims responsible for 
	 * a certain amount of (most of the times special) template tags and displays it in a more consistent and user friendly way. It's basically 
	 * a jpanel that is heavily costumized for a certain application.
	 * @param jsdl the jsdl template document
	 * @return a list of all the modules that should be used to render this jsdl template on the ui
	 */
	public static String[] getModules(Document jsdl) {
		
		String[] modules = null;
		NodeList resultNodes = null;
		String expression = "//@grisu_modules";
		try {
			resultNodes = (NodeList)xpath.evaluate(expression, jsdl, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			myLogger.debug("No modules used node in jsdl file.");
			return new String[]{};
		}
		
		if ( resultNodes.getLength() != 1 ) {
			myLogger.error("Zero or more than one module specification found. That's not supported right now. Continuing not using modules.");
			return new String[]{};
		}
		
		modules = resultNodes.item(0).getNodeValue().split(",");
		
		return modules;
		
	}
	
	/**
	 * After the user inputs values for a template tag the template attributes should be deleted from the jsdl template to have a (valid) plain jsdl 
	 * document just before job submission. Actually, that's not quite exact. Some substitution within the document is done only after the job is submitted
	 * to the grisu-core module. Namely the replacement of ${GRISU_JOB_DIRECTORY} with the actual path of the job directory.
	 * @param element
	 */
	public static void removeTemplateAttributes(Element element) {
		
		element.removeAttribute("title");
		element.removeAttribute("template");
		element.removeAttribute("description");
		element.removeAttribute("default");
		element.removeAttribute("multiplicy");
//		element.setTextContent(replacementStringContent);
	}

}
