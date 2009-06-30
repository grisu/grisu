package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;

public class GenericGridTestElement extends GridTestElement {
	
	public GenericGridTestElement(ServiceInterface si, String version, String subLoc) throws MdsInformationException {
		super(si, version, subLoc);
	}

	@Override
	protected boolean checkJobSuccess() {

		if ( JobConstants.FINISHED_EITHER_WAY <= this.jobObject.getStatus(true) ) {
			addMessage("Status checked. Equals \"Done\". Good");
			return true;
		} else {
			addMessage("Status checked. Status is \""+jobObject.getStatus(false)+". Not good.");
			return false;
		}
		
		
	}

	@Override
	protected JobObject createJobObject() throws MdsInformationException {

		String[] exes = GrisuRegistry.getDefault(serviceInterface).getApplicationInformation(this.application).getExecutables(this.submissionLocation, version);
		
		if ( exes == null || exes.length == 0 ) {
			throw new MdsInformationException("Could not find executables for: "+application+", "+version+", "+submissionLocation);
		}
		
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.application);
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline(exes[0]);
		
		return jo;
		
	}

	@Override
	protected String getApplicationSupported() {
		return "generic";
	}

}
