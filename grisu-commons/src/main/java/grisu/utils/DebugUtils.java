package grisu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Helper class to turn on jsdl debug output application wide.
 * 
 * @author markus
 * 
 */
public final class DebugUtils {

	static final Logger myLogger = LoggerFactory.getLogger(DebugUtils.class
			.getName());

	public static void jsdlDebugOutput(final String stage, final Document jsdl) {

		if (false) {

			try {
				myLogger.debug("Jsdl when processing stage: " + stage
						+ "\n-----------------------------\n"
						+ SeveralXMLHelpers.toString(jsdl));
			} catch (final Exception e) {
				myLogger.error("Couldn't parse jsdl document.");
			}
		}

	}

	private DebugUtils() {
	}

}
