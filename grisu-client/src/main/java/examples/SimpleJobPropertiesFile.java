package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.model.job.JobDescription;

import java.io.File;

public class SimpleJobPropertiesFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.login("local", false);
		
		File jp = new File("/home/markus/src/grisu/gee/examples/R/tests/simple-R/job.properties");
		
		JobDescription jd = new JobDescription(jp);
		
		GrisuJob j = GrisuJob.createJobObject(si, jd);
		
		j.createJob("/nz/nesi");

		j.submitJob();
				
		

	}

}
