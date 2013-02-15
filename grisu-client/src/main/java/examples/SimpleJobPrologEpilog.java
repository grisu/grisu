package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;

public class SimpleJobPrologEpilog {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.login("nesi", false);
		
		GrisuJob j = new GrisuJob(si);
		j.setSubmissionLocation("pan:pan.nesi.org.nz");
		j.setJobname("env_job");
		j.addEnvironmentVariable("PROLOG", "echo prolog >> benchmark.log");
		j.addEnvironmentVariable("EPILOG", "echo epilog >> benchmark.log");
		
		j.setCommandline("env");
		j.createJob("/nz/nesi");

		j.submitJob(true);
				
		

	}

}
