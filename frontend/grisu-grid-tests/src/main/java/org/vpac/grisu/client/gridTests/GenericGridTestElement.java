package org.vpac.grisu.client.gridTests;

import java.io.File;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.JobSubmissionProperty;

public class GenericGridTestElement extends GridTestElement {
	
	public GenericGridTestElement(GridTestController c, ServiceInterface si, String version, String subLoc) throws MdsInformationException {
		super(c, si, version, subLoc);
	}

	@Override
	protected boolean checkJobSuccess() {
		
		String jobDir = null;
		try {
			jobDir = serviceInterface.getJobProperty(jobObject.getJobname(), Constants.JOBDIRECTORY_KEY);
		} catch (NoSuchJobException e) {
			addMessage("Could not find job. This is most likely a globus/grisu problem...");
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
		
		String stdout = null;
		try {
			stdout = serviceInterface.getJobProperty(jobObject.getJobname(), JobSubmissionProperty.STDOUT.toString());
			addMessage("url of stdout is: "+jobDir+"/"+stdout);
			
			FileManager fileHelper = GrisuRegistry.getDefault(serviceInterface).getFileManager();
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
		
		jo.setApplication(Constants.GENERIC_APPLICATION_NAME);
		
		jo.setCommandline("cat genericTest.txt");
		jo.addInputFileUrl(controller.getGridTestDirectory().getPath()+File.separator+"genericTest.txt");
		jo.setSubmissionLocation(submissionLocation);
//		jo.addInputFileUrl(controller.getGridTestDirectory().getPath()+File.separator+"genericTest.txt");
		
		return jo;
		
	}

	@Override
	protected String getApplicationSupported() {
		return "generic";
	}

	@Override
	protected boolean useMDS() {
		return false;
	}

	@Override
	public String getTestDescription() {
		return "A simple cat job (including the staging of a small input file. The resulting stdout file is downloaded and checked for a non-zero filesize.";
	}

	@Override
	public String getTestName() {
		return "Simple_Cat_Test";
	}

	


}
