package org.vpac.grisu.client.gridTests;

import java.io.File;

import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.files.FileHelper;
import org.vpac.grisu.js.model.JobProperty;

public class GenericGridTestElement extends GridTestElement {
	
	public GenericGridTestElement(ServiceInterface si, String version, String subLoc) throws MdsInformationException {
		super(si, version, subLoc);
	}

	@Override
	protected boolean checkJobSuccess() {
		
		String jobDir = null;
		try {
			jobDir = serviceInterface.getJobProperty(jobObject.getJobname(), ServiceInterface.JOBDIRECTORY_KEY);
		} catch (NoSuchJobException e) {
			addMessage("Could not find job. This is most likely a globus/grisu problem...");
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
		
		String stdout = null;
		try {
			stdout = serviceInterface.getJobProperty(jobObject.getJobname(), JobProperty.STDOUT.toString());
			addMessage("url of stdout is: "+jobDir+"/"+stdout);
			
			FileHelper fileHelper = new FileHelper(serviceInterface);
			File stdoutFile = fileHelper.downloadFile(jobDir+"/"+stdout);
			
			if ( stdoutFile.length() > 0 ) {
				addMessage("Downloaded stdout file. Filesize "+stdoutFile.length()+". That's good.");
				//TODO deleteFile again
				return true;
			} else {
				addMessage("Downloaded stdout file. Filesize "+stdoutFile.length()+". That's not good.");
				return false;
			}
			
		} catch (Exception e) {
			addMessage("Could not get children of output directory.");
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
		
		
	}

	@Override
	protected JobObject createJobObject() throws MdsInformationException {

		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(ServiceInterface.GENERIC_APPLICATION_NAME);
		
		jo.setCommandline("cat genericTest.txt");
		jo.addInputFileUrl(GridTestController.GridTestDirectory+File.separator+"genericTest.txt");
		jo.setSubmissionLocation(submissionLocation);
		jo.addInputFileUrl(GridTestController.GridTestDirectory.toString()+File.separator+"genericTest.txt");
		
		return jo;
		
	}

	@Override
	protected String getApplicationSupported() {
		return "generic";
	}

}
