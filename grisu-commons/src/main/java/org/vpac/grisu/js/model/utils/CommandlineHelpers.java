package org.vpac.grisu.js.model.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.IllegalFormatException;

public class CommandlineHelpers {
	
	public static ArrayList<String> extractArgumentsFromCommandline(String string) {
		
		ArrayList<String> args = parseString(string);
		args.remove(0);
		return args;
	}
	
	public static String extractExecutable(String string) {
		ArrayList<String> strings = parseString(string);
		return strings.get(0);
	}
	
	public static ArrayList<String> parseString(String string) {
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
//					strings.add(part.toString());
//					part = new StringBuffer();
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
		if ( inbetweenQuotationMarks ) {
			throw new RuntimeException("No end quotations marks when parsing commandline.");
		} else {
			if ( part.length() > 0 ) 
				strings.add(part.toString());
		}
		return strings;
	}

}
