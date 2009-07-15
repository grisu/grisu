package org.vpac.grisu.utils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Helper class to turn on jsdl debug output application wide.
 * 
 * @author markus
 * 
 */
public final class DebugUtils {

	private DebugUtils() {
	}

	static final Logger myLogger = Logger.getLogger(DebugUtils.class.getName());

	public static void jsdlDebugOutput(final String stage, final Document jsdl) {

		if (false) {

			try {
				myLogger.debug("Jsdl when processing stage: " + stage
						+ "\n-----------------------------\n"
						+ SeveralXMLHelpers.toString(jsdl));
			} catch (Exception e) {
				myLogger.error("Couldn't parse jsdl document.");
			}
		}

	}

}
