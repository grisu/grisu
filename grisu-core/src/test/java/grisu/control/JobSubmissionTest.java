package grisu.control;

import grisu.backend.AllTests;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.control.serviceInterfaces.AbstractServiceInterface;
import grisu.frontend.model.job.JobObject;
import grisu.jcommons.constants.Constants;

import java.util.HashMap;

import org.junit.Test;

public class JobSubmissionTest {

	@Test
	public void testExpiringProxyWhileJobIsRunning()
			throws JobPropertiesException, JobSubmissionException,
			InterruptedException {

		AbstractServiceInterface asi = (AbstractServiceInterface) AllTests
				.getServiceInterface();
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("submitProxyLifetime", "60");
		asi.setDebugProperties(props);

		JobObject job = new JobObject(asi);
		job.setCommandline("sleep 10000000");
		job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		job.setSubmissionLocation("route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz");
		job.setWalltimeInSeconds(360);
		// job.setSubmissionLocation("default:gram5.ceres.auckland.ac.nz");
		job.createJob("/ARCS/BeSTGRID");
		job.submitJob();

		job.waitForJobToFinish(5);

		System.out.println("Status: " + job.getStatusString(true));

		props.put("submitProxyLifetime", "");
		asi.setDebugProperties(props);

	}

}
