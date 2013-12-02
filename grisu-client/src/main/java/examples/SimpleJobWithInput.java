package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;

import java.io.File;

public class SimpleJobWithInput {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.login("dev");

		GrisuJob j = new GrisuJob(si);

        j.setSubmissionLocation("pan:gram.uoa.nesi.org.nz");

        j.addInputFile(new File("/home/markus/monolix_job_original"));
		j.setCommandline("find .");
		j.createJob("/nz/uoa/projects/uoa99999");

		j.submitJob();



	}

}
