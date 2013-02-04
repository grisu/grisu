package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.JobObject;

public class SimpleJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.loginCommandline("nesi");
		
		JobObject j = new JobObject(si);
		
		j.setCommandline("R --version");
		j.createJob("/nz/nesi");

		j.submitJob();
				
		

	}

}
