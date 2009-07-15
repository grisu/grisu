package org.vpac.grisu.utils;

import java.util.ArrayList;

/**
 * Helper methods to parse a commandline and split it up into executable and
 * arguments.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class CommandlineHelpers {

	private CommandlineHelpers() {
	}

	/**
	 * Returns the list of arguments for the specified commandline.
	 * 
	 * @param string
	 *            the commandline
	 * @return the parsed arguments
	 */
	public static ArrayList<String> extractArgumentsFromCommandline(
			final String string) {

		ArrayList<String> args = parseString(string);
		args.remove(0);
		return args;
	}

	/**
	 * Returns the executable (first string) for this commandline.
	 * 
	 * @param string
	 *            the commandline
	 * @return the executable
	 */
	public static String extractExecutable(final String string) {
		ArrayList<String> strings = parseString(string);
		return strings.get(0);
	}

	/**
	 * Parses the specified string and returns a list of tokens.
	 * 
	 * The tokens are either seperated by whitespaces or surrounded by double
	 * quotation marks.
	 * 
	 * @param string
	 *            the commandline
	 * @return all tokens for this commandline
	 */
	public static ArrayList<String> parseString(final String string) {
		ArrayList<String> strings = new ArrayList<String>();

		boolean lastCharacterIsWhitespace = false;
		boolean inbetweenQuotationMarks = false;
		StringBuffer part = new StringBuffer();
		for (char character : string.toCharArray()) {
			if (Character.isWhitespace(character)) {
				if (!lastCharacterIsWhitespace && !inbetweenQuotationMarks) {
					strings.add(part.toString());
					part = new StringBuffer();
					lastCharacterIsWhitespace = true;
					continue;
				}
				if (inbetweenQuotationMarks) {
					part.append(character);
				} else {
					lastCharacterIsWhitespace = true;
					// strings.add(part.toString());
					// part = new StringBuffer();
					continue;
				}
			} else {
				if (character == '"') {
					if (inbetweenQuotationMarks) {
						strings.add(part.toString());
						part = new StringBuffer();
						inbetweenQuotationMarks = false;
						lastCharacterIsWhitespace = true;
						continue;
					} else {
						inbetweenQuotationMarks = true;
						continue;
					}
				} else {
					part.append(character);
					lastCharacterIsWhitespace = false;
				}
			}

		}
		if (inbetweenQuotationMarks) {
			throw new RuntimeException(
					"No end quotations marks when parsing commandline.");
		} else {
			if (part.length() > 0) {
				strings.add(part.toString());
			}
		}
		return strings;
	}

}
