

package org.vpac.grisu.control;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper methods that do convert/extract xml documents and vice versa.
 * 
 * @author Markus Binsteiner
 *
 */
public class SeveralXMLHelpers {
	
//	public static String[] extractMountPoints(Document mps) {
//		
//		NodeList mountPoints = mps.getFirstChild().getChildNodes();
//		String[] result = new String[mountPoints.getLength()];
//		
//		for ( int i=0; i<result.length; i++ ) {
//			result[i] = mountPoints.item(i).getAttributes().getNamedItem("mountpoint").getNodeValue();
//		}
//		
//		return result;
//	}
	
//	public static String[] extractMountURLs(Document mps) {
//		NodeList mountPoints = mps.getFirstChild().getChildNodes();
//		String[] result = new String[mountPoints.getLength()];
//		
//		for ( int i=0; i<result.length; i++ ) {
//			result[i] = mountPoints.item(i).getAttributes().getNamedItem("url").getNodeValue();
//		}
//		
//		return result;
//	}
	
//	public static Map<String, String> extractMounts(Document mps) {
//		NodeList mountPoints = mps.getFirstChild().getChildNodes();
//		Map<String, String> result = new HashMap<String, String>();
//		
//		for ( int i=0; i<mountPoints.getLength(); i++ ) {
//			result.put(mountPoints.item(i).getAttributes().getNamedItem("mountpoint").getNodeValue(),
//					mountPoints.item(i).getAttributes().getNamedItem("url").getNodeValue());
//		}
//		
//		return result;
//	}
	
//	public static Map<String, Map<String, String>> extractJobsInformation(Document jobs) {
//		NodeList allJobNodes = jobs.getFirstChild().getChildNodes();
//		Map<String, Map<String,String>> result = new HashMap<String, Map<String,String>>();
//		
//		for ( int i=0; i<allJobNodes.getLength(); i++ ) {
//			String jobname = allJobNodes.item(i).getAttributes().getNamedItem("jobname").getNodeValue();
//			String status = null;
//			try {
//				status = allJobNodes.item(i).getAttributes().getNamedItem("status").getNodeValue();
//			} catch (NullPointerException e) {
//			}
//			String host = null;
//			try {
//				host = allJobNodes.item(i).getAttributes().getNamedItem("host").getNodeValue();
//			} catch (NullPointerException e) {
//			}
//			String fqan = null;
//			try {
//				fqan = allJobNodes.item(i).getAttributes().getNamedItem("fqan").getNodeValue();
//			} catch (NullPointerException e) {
//			}
//			
//			if ( status == null ) status = "n/a";
//			if ( host == null ) host = "n/a";
//			if ( fqan == null ) fqan = "n/a";
//			
//			Map<String,String> jobMap = new HashMap<String,String>();
//			jobMap.put("status",status);
//			jobMap.put("host", host);
//			jobMap.put("fqan", fqan);
//			
//			result.put(jobname, jobMap);
//		}
//		
//		return result;
//	}
	
	private static String convertElementToString(Element element) {
		
		StringBuffer result = new StringBuffer();
		String tagName = element.getTagName();
		result.append("<"+tagName+" ");
		NamedNodeMap attributes = element.getAttributes();
		for ( int i=0; i<attributes.getLength(); i++ ) {
			Node node = attributes.item(i);
			Attr attr = (Attr)node;
			result.append(attr.getName());
			String value = attr.getValue();
			result.append("="+value+" ");
		}
		result.append(">\n");
		
		NodeList childs = element.getChildNodes();
		for ( int i=0; i<childs.getLength(); i++ ) {
			Node node = childs.item(i);
			Element child = (Element)node;
			result.append(convertElementToString(child));
		}
		result.append("</"+tagName+">\n");
		return result.toString();
	}

	public static String convertToString(Element element) {
		
		return convertElementToString(element);		
	}
	
	public static String toString(Document jsdl) throws TransformerFactoryConfigurationError, TransformerException {
			//TODO use static transformer to reduce overhead?
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	
	//		initialize StreamResult with InputFile object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(jsdl);
			transformer.transform(source, result);
	
			String jsdl_string = result.getWriter().toString();
			
			return jsdl_string;
		}
	
	public static String toStringWithoutAnnoyingExceptions(Document jsdl) {
		
		String result;
		try {
			result = toString(jsdl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getLocalizedMessage();
		}
		
		return result;
		
	}
	
	public static Document fromInputStream(InputStream input) throws Exception {
		try {
			final String JAXP_SCHEMA_SOURCE
			= "http://java.sun.com/xml/jaxp/properties/schemaSource";
			final String JAXP_SCHEMA_LANGUAGE
			= "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
			final String W3C_XML_SCHEMA
			= "http://www.w3.org/2001/XMLSchema";
			
//			File schemaFile = new File("/home/markus/workspace/nw-core/jsdl.xsd");
		
			DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
			docBuildFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docBuildFactory.setNamespaceAware(true);
			docBuildFactory.setValidating(false);
		
			docBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); // use LANGUAGE here instead of SOURCE
//			docBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
		
			DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
			return docBuilder.parse(input);
			} catch (Exception e) {
//				e.printStackTrace();
				throw e;
			}
	}

	public static Document fromString(String jsdl_string) throws Exception {
		
		try {
		final String JAXP_SCHEMA_SOURCE
		= "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE
		= "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String W3C_XML_SCHEMA
		= "http://www.w3.org/2001/XMLSchema";
		
//		File schemaFile = new File("/home/markus/workspace/nw-core/jsdl.xsd");
	
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		docBuildFactory.setNamespaceAware(true);
		docBuildFactory.setValidating(false);
	
		docBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); // use LANGUAGE here instead of SOURCE
//		docBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
	
		DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
		return docBuilder.parse(new ByteArrayInputStream(jsdl_string.getBytes()));
		} catch (Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}
	
	public static Document loadXMLFile(File file) {
		
		Document jsdl = null;
		
		final String JAXP_SCHEMA_SOURCE
		= "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE
		= "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String W3C_XML_SCHEMA
		= "http://www.w3.org/2001/XMLSchema";
		
//		File schemaFile = new File("/home/markus/workspace/nw-core/jsdl.xsd");

		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		docBuildFactory.setValidating(false);

		docBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); // use LANGUAGE here instead of SOURCE
//		docBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);

		try {
			DocumentBuilder documentBuilder = docBuildFactory.newDocumentBuilder();
			jsdl = documentBuilder.parse(file);
			//JsdlHelpers.validateJSDL(jsdl);
			
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return jsdl;
	}
}
