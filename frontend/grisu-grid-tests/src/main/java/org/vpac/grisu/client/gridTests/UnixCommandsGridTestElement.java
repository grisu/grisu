package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.frontend.model.job.JobObject;

public class UnixCommandsGridTestElement extends GridTestElement {

	public UnixCommandsGridTestElement(ServiceInterface si, String version, String submissionLocation) throws MdsInformationException {
		super(si, version, submissionLocation);
	}
	
	@Override
	protected JobObject createJobObject() {
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.application);
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline("echo hello");
//		jo.addInputFileUrl("/home/markus/test.txt");
		
		return jo;
	}

	@Override
	protected String getApplicationSupported() {
		return "UnixCommands";
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

}
