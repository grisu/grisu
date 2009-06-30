package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;

public class JavaGridTestElement extends GridTestElement {

	public JavaGridTestElement(ServiceInterface si, String version, String submissionLocation) throws MdsInformationException {
		super(si, version, submissionLocation);
	}
	
	@Override
	protected JobObject createJobObject() {
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.application);
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline("java -version");
		jo.addInputFileUrl("/home/markus/test.txt");
		
		return jo;
	}

	@Override
	protected String getApplicationSupported() {
		return "java";
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
