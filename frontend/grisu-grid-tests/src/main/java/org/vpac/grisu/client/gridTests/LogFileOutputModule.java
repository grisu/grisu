package org.vpac.grisu.client.gridTests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class LogFileOutputModule implements OutputModule {

	private String output;
	
	public LogFileOutputModule(String output) {
		this.output = output;
	}
	
	public void outputResult(List<GridTestElement> elements) {

		StringBuffer outputString = new StringBuffer();

		
		for (GridTestElement gte : elements) {
			
			outputString.append(OutputModuleHelpers.createStringReport(gte));

		}

		try {

			String uFileName = output;
			FileWriter fileWriter = new FileWriter(uFileName);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
