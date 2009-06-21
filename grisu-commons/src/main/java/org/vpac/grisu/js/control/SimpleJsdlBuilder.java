package org.vpac.grisu.js.control;

import java.io.InputStream;
import java.util.Map;

import org.vpac.grisu.control.SeveralStringHelpers;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.js.model.JobProperty;
import org.vpac.grisu.js.model.utils.CommandlineHelpers;
import org.w3c.dom.Document;

/** 
 * This class creats a job description document out of the job properties using String replacements on an xml file.
 * 
 * I know I should probably write a class that uses builds an xml file from scratch, but I just don't have the time for that.
 *  
 * @author markus
 *
 */
public class SimpleJsdlBuilder {
	
	public static Document buildJsdl(Map<JobProperty, String> jobProperties) {
		
		InputStream in = SimpleJsdlBuilder.class.getResourceAsStream("/generic.xml");
		
		String jsdlTemplateString = SeveralStringHelpers.fromInputStream(in);
		
		for (JobProperty jp : JobProperty.values()) {
			
			if ( jp.equals(JobProperty.COMMANDLINE) ) {
				
				String executable = CommandlineHelpers.extractExecutable(jobProperties.get(jp));
				StringBuffer exeAndArgsElements = new StringBuffer();
				exeAndArgsElements.append("<Executable>");
				exeAndArgsElements.append(executable);
				exeAndArgsElements.append("</Executable>");
				
				for ( String arg : CommandlineHelpers.extractArgumentsFromCommandline(jobProperties.get(jp)) ) {
					exeAndArgsElements.append("<Argument>");
					exeAndArgsElements.append(arg);
					exeAndArgsElements.append("</Argument>");
				}
				
				jsdlTemplateString = jsdlTemplateString.replaceAll("XXX_"+jp.toString()+"_XXX", exeAndArgsElements.toString());
				
			} else if ( jp.equals(JobProperty.INPUT_FILE_URLS) ) {
				
				String inputFileUrls = jobProperties.get(jp);
				if ( inputFileUrls == null || "".equals(inputFileUrls) ) {
					jsdlTemplateString = jsdlTemplateString.replaceAll("XXX_"+jp.toString()+"_XXX", "");
					continue;
				}
				
				StringBuffer dataStagingElements = new StringBuffer();
				for ( String inputFileUrl : inputFileUrls.split(",") ) {
					dataStagingElements.append("<DataStaging>\n<FileName />\n<FileSystemName>userExecutionHostFs</FileSystemName>\n"+
					"<Source>\n<URI>");
					dataStagingElements.append(inputFileUrl);
					dataStagingElements.append("</URI>\n</Source>\n</DataStaging>\n");
				}
				
				jsdlTemplateString = jsdlTemplateString.replaceAll("XXX_"+jp.toString()+"_XXX", dataStagingElements.toString());
				
			} else {
				if ( jobProperties.get(jp) == null ) {
					jsdlTemplateString = jsdlTemplateString.replaceAll("XXX_"+jp.toString()+"_XXX", jp.defaultValue());
				} else {
					jsdlTemplateString = jsdlTemplateString.replaceAll("XXX_"+jp.toString()+"_XXX", jobProperties.get(jp));
				}
			}
		}
		
		Document result;
		try {
			result = SeveralXMLHelpers.fromString(jsdlTemplateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Couldn't create jsdl document");
		}
		return result;
	}

}
