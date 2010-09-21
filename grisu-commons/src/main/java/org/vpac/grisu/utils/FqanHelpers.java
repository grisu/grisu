package org.vpac.grisu.utils;

/**
 * Helps with the handling of FQANs. Basically just String manipulation.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class FqanHelpers {

	/**
	 * Parses a fqan for the group.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the group (something like /APACGrid/NGAdmin)
	 */
	public static String getGroupPart(final String fqan) {
		String groupPart = null;
		try {
			final int index = fqan.indexOf("/Role=");
			if (index == -1) {
				groupPart = fqan;
			} else {
				groupPart = fqan.substring(0, fqan.indexOf("/Role="));
			}
		} catch (final Exception e) {
			return null;
		}
		return groupPart;
	}

	/**
	 * Parses a fqan for the last part of the group (/APACGrid/NGAdmin would
	 * return NGAdmin).
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the name of the last subgroup
	 */
	public static String getLastGroupPart(final String fqan) {
		final String group = getGroupPart(fqan);
		return group.substring(group.lastIndexOf("/") + 1);
	}

	/**
	 * Parses the fqan for the role part.
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the role (something like Member)
	 */
	public static String getRolePart(final String fqan) {

		String role = null;
		try {
			role = fqan.substring(fqan.indexOf("/Role=") + 6,
					fqan.indexOf("/Capability="));
		} catch (final Exception e) {
			return null;
		}
		return role;
	}

	private FqanHelpers() {
	}

}
