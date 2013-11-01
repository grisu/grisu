package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;

public class SimpleNonmemJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.loginCommandline("bestgrid");

		GrisuJob j = new GrisuJob(si);

        j.setUniqueJobname("nonmem");

        j.setApplication("nonmem");
        j.setApplicationVersion("72");

		j.setCommandline("env");
		j.createJob("/nz/nesi");

		j.submitJob();



	}

}
