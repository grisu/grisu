package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;

public class SimpleJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.loginCommandline("nesi");
		
		GrisuJob j = new GrisuJob(si);
		
		j.setCommandline("R --version");
		j.createJob("/nz/nesi");

		j.submitJob();
				
		

	}

}
