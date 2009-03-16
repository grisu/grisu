

package org.vpac.grisu.control;

/**
 * Helps with the handling of FQANs. 
 * Basically just String manipulation.
 * 
 * @author Markus Binsteiner
 *
 */
public class FqanHelpers {
	
	/**
	 * Parses a fqan for the last part of the group (/APACGrid/NGAdmin would return NGAdmin).
	 * @param fqan the fqan
	 * @return the name of the last subgroup
	 */
	public static String getLastGroupPart(String fqan) {
		String group =  getGroupPart(fqan);
		return group.substring(group.lastIndexOf("/")+1);
	}
	
	/**
	 * Parses a fqan for the group
	 * @param fqan the fqan
	 * @return the group (something like /APACGrid/NGAdmin)
	 */
	public static String getGroupPart(String fqan) {
		String groupPart = null;
		try {
			int index = fqan.indexOf("/Role=");
			if ( index == -1 )
				groupPart = fqan;
			else 
				groupPart = fqan.substring(0, fqan.indexOf("/Role="));
		} catch (Exception e) {
			return null;
		}
		return groupPart;
	}
	
	/**
	 * Parses the fqan for the role part
	 * @param fqan the fqan
	 * @return the role (something like Member)
	 */
	public static String getRolePart(String fqan) {
		
		String role = null;
		try {
			role = fqan.substring(fqan.indexOf("/Role=")+6, fqan.indexOf("/Capability="));
		} catch (Exception e) {
			return null;
		}
		return role;
	}

}
