package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.frontend.model.job.JobObject;

public class JavaGridTestElement extends GridTestElementFactory {

	public JavaGridTestElement(GridTestController c, ServiceInterface si, String version, String submissionLocation) throws MdsInformationException {
		super(c, si, version, submissionLocation);
	}
	
	@Override
	protected JobObject createJobObject() {
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.application);
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline("java -version");
//		jo.addInputFileUrl("/home/markus/test.txt");
		
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

	@Override
	protected boolean useMDS() {
		return true;
	}

	@Override
	public String getTestDescription() {
		return "Simple test that checks whether the module on the resource is loaded correctly. It runs the java" +
		" command to print out the java version and checks whether the job ran with error code 0";
		
	}

	@Override
	public String getTestName() {
		return "Java_Version_Test";
	}


}
