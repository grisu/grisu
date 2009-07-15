package org.vpac.grisu.utils;

import java.util.regex.Pattern;

/**
 * Helper class to determine whether a string is a valid email address.
 * 
 * @author markus
 * 
 */
public final class EmailUtils {
	
	private EmailUtils() {
		
	}

	// RFC 2822 token definitions for valid email - only used together to form a
	// java Pattern object:
	private static final String SP = "\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~";
	private static final String ATEXT = "[a-zA-Z0-9" + SP + "]";
	private static final String ATOM = ATEXT + "+";
	// one or more atext chars
	private static final String DOTATOM = "\\." + ATOM;
	private static final String LOCALPART = ATOM + "(" + DOTATOM + ")*";
	// one atom followed by 0 or more dotAtoms.
	// RFC 1035 tokens for domain names:
	private static final String LETTER = "[a-zA-Z]";
	private static final String LETDIG = "[a-zA-Z0-9]";
	private static final String LETDIGHIP = "[a-zA-Z0-9-]";
	public static final String RFCLABEL = LETDIG + "(" + LETDIGHIP + "{0,61}"
			+ LETDIG + ")?";
	private static final String DOMAIN = RFCLABEL + "(\\." + RFCLABEL + ")*\\."
			+ LETTER + "{2,6}";
	// Combined together, these form the allowed email regexp allowed by RFC
	// 2822:
	private static final String ADDRSPEC = "^" + LOCALPART + "@" + DOMAIN + "$";
	// now compile it:
	public static final Pattern VALID_PATTERN = Pattern.compile(ADDRSPEC);

	public static boolean isValid(final String userEnteredEmailString) {
		return VALID_PATTERN.matcher(userEnteredEmailString).matches();
	}

}
