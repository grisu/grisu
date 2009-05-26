package org.vpac.grisu.js.model.utils;

import org.vpac.grisu.control.info.InformationManager;
import org.vpac.grisu.model.GridResource;

public class SubmissionLocationHelpers {
	
	public static String extractQueue(String subLoc) {
		
		int endIndex = subLoc.indexOf(":");
		if ( endIndex <= 0 ) {
//			return null;
			return "Not available";
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
	
	public static String createSubmissionLocationString(GridResource gridResource) {
		
		String hostname = gridResource.getContactString().substring(
				gridResource.getContactString().indexOf("https://") != 0 ? 0 : 8,
				gridResource.getContactString().indexOf(":8443"));
		
		return createSubmissionLocationString(hostname, gridResource.getQueueName(), gridResource.getJobManager());
	}
	
	public static String createSubmissionLocationString(InformationManager im, String contactString, String queue) {
		
		String hostname = contactString.substring(
				contactString.indexOf("https://") != 0 ? 0 : 8,
				contactString.indexOf(":8443"));
		
		String site = im.getSiteForHostOrUrl(hostname);
		
		String jobmanager = im.getJobmanagerOfQueueAtSite(site, queue);
		
		return createSubmissionLocationString(hostname, queue, jobmanager);
	}
	
	public static String createSubmissionLocationString(String hostname, String queue, String jobManager ) {
		
		StringBuffer result = new StringBuffer(queue);
		result.append(":");
		result.append(hostname);
		if ( jobManager != null && jobManager.length() > 0 ) {
			if (jobManager.toLowerCase().indexOf("pbs") < 0) {
				result.append("#");
				result.append(jobManager);
			} 
		}
		return result.toString();
		
	}
	

}
