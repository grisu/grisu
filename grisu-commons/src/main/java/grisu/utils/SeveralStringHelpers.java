package grisu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Helper methods for String handling.
 * 
 * @author markus
 * 
 */
public final class SeveralStringHelpers {

	static final Logger myLogger = Logger.getLogger(SeveralStringHelpers.class
			.getName());

	/**
	 * Converts an inputstream to a string.
	 * 
	 * @param is
	 *            the input stream
	 * @return the string
	 */
	public static String fromInputStream(final InputStream is) {

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		final StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (final IOException e) {
			myLogger.error(e);
		} finally {
			try {
				is.close();
			} catch (final IOException e) {
				myLogger.error(e);
			}
		}

		return sb.toString();
	}

	private SeveralStringHelpers() {
	}

}
