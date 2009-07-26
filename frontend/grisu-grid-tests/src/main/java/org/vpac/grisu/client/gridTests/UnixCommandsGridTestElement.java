package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.frontend.model.job.JobObject;

public class UnixCommandsGridTestElement extends GridTestElementFactory {

	public UnixCommandsGridTestElement(GridTestController c,ServiceInterface si, String version, String submissionLocation) throws MdsInformationException {
		super(c, si, version, submissionLocation);
	}
	
	@Override
	protected JobObject createJobObject() {
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.application);
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline("echo hello world");
//		jo.addInputFileUrl("/home/markus/test.txt");
		
		return jo;
	}

	@Override
	protected String getApplicationSupported() {
		return "unixcommands";
	}
	
	protected boolean checkJobSuccess() {
		
		if ( JobConstants.DONE == this.jobObject.getStatus(true) ) {
			addMessage("Status checked. Equals \"Done\". Good");
			return true;
		} else {
			addMessage("Status checked. Status is \""+jobObject.getStatus(false)+". Not good.");
			return false;
		}
		
		
	}

	@Override
	protected boolean useMDS() {
		return true;
	}

	@Override
	public String getTestDescription() {
		return "A simple \"echo hello world\" is run. The tests checks whether the job status equals \"Done\" after the job finished.";
	}

	@Override
	public String getTestName() {
		return "Simple_Echo_Test";
	}

}
