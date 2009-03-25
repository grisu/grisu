package org.vpac.grisu.js.model.utils;

public class SubmissionLocationHelpers {
	
	public static String extractQueue(String subLoc) {
		
		int endIndex = subLoc.indexOf(":");
		if ( endIndex <= 0 ) {
			return null;
		}
		
		return subLoc.substring(0, endIndex);
	}
	
	public static String extractHost(String subLoc) {
		
		int startIndex = subLoc.indexOf(":") + 1;
		if (startIndex == -1)
			startIndex = 0;

		int endIndex = subLoc.indexOf("#");
		if (endIndex == -1)
			endIndex = subLoc.length();

		return subLoc.substring(startIndex, endIndex);
		
	}

}
