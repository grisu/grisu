package org.vpac.grisu.client.gridTests;

public class OutputModuleHelpers {
	
	public static StringBuffer createStringReport(GridTestElement gte) {
		
		StringBuffer outputString = new StringBuffer();
		
		outputString.append("Test for "+gte.getApplicationSupported()+", version: "+gte.getVersion());

		outputString.append("SubmissionLocation: "
				+ gte.getSubmissionLocation() + "\n");
		outputString
		.append("-------------------------------------------------"
				+ "\n");
		outputString.append("Started: "+gte.getStartDate().toString()+"\n");
		outputString.append("Ended: "+gte.getEndDate().toString()+"\n");
		outputString
		.append("-------------------------------------------------"
				+ "\n");
		outputString.append(gte.getResultString() + "\n");
		outputString
				.append("-------------------------------------------------"
						+ "\n");
		
		return outputString;
	}

}
