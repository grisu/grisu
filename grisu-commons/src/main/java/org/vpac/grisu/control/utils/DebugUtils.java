package org.vpac.grisu.control.utils;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.w3c.dom.Document;

public class DebugUtils {
	
	static final Logger myLogger = Logger.getLogger(DebugUtils.class
			.getName());


	public static void jsdlDebugOutput(String stage, Document jsdl) {
		
		if ( false ) {
		
		try {
			myLogger.debug("Jsdl when processing stage: "+stage+"\n-----------------------------\n"+SeveralXMLHelpers.toString(jsdl));
		} catch (Exception e) {
			myLogger.error("Couldn't parse jsdl document.");
		}
		}
		
	}
	
}
