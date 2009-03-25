import java.io.File;

import org.vpac.grisu.client.control.ServiceInterfaceFactory;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.js.model.utils.JsdlHelpers;
import org.w3c.dom.Document;


public class LeonExample {
	
	public static void main (String[] args) {
		
		String myproxy_username = args[0];
		char[] myproxy_password = args[1].toCharArray();
		String vo = "/ARCS/StartUp";
		String path_to_jsdl_file = args[2];
		
		// load the xml document
		Document jsdl = SeveralXMLHelpers.loadXMLFile(new File(path_to_jsdl_file));
		// load the jobname from the jsdl document
		String jobname = JsdlHelpers.getJobname(jsdl);
		String submissionLocation = JsdlHelpers.getCandidateHosts(jsdl)[0];
		
		ServiceInterface serviceInterface = null;
		try {
			serviceInterface = ServiceInterfaceFactory.createInterface(
					"Local", 
//					"https://ngportaldev.vpac.org/grisu-ws/services/grisu",
					myproxy_username, myproxy_password, 
					"myproxy.arcs.org.au", "443", 
					null, -1, null, null);
		} catch (ServiceInterfaceException e12) {
			System.err.println("Could not login to remote grisu backend: "+e12.getLocalizedMessage());
			System.exit(1);
		}
		
		serviceInterface.login(myproxy_username, myproxy_password);
		
		// kill possibly existing job
		try {
			serviceInterface.kill(jobname, true);
		} catch (Exception e0) {
			System.err.println("Couln't delete partially created job from db: "+e0.getLocalizedMessage());
		}
		
		String finalJobname = null;
		try {
			finalJobname = serviceInterface.createJob(jobname, JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME);
		} catch (JobCreationException e) {
			System.err.println("Could not create job on grisu server: "+e.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e4) {
				System.err.println("Couln't delete partially created job from db: "+e4.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		String absoluteJobDirectory = serviceInterface.calculateAbsoluteJobDirectory(finalJobname, submissionLocation, vo);
		System.out.println("Job directory: "+absoluteJobDirectory);
		
		String workingDirectory = serviceInterface.calculateRelativeJobDirectory(finalJobname);
		System.out.println("Working directory: "+workingDirectory);
		
		// this is needed for the jsdl document
		// maybe I should have an extra serviceInterface method for this? What do you think?
		String executionFileSystem = absoluteJobDirectory.substring(0, absoluteJobDirectory.length()-(workingDirectory.length()+1));
		System.out.println("Execution file system: "+executionFileSystem);
		
		String jsdl_string = null;
		try {
			jsdl_string = SeveralXMLHelpers.toString(jsdl);
		} catch (Exception e1) {
			System.err.println("Jsdl not valid: "+e1.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e6) {
				System.err.println("Couln't delete partially created job from db: "+e6.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		jsdl_string = jsdl_string.replaceAll("XXX_WORKINGDIRECTORY_XXX", workingDirectory);
		jsdl_string = jsdl_string.replaceAll("XXX_USEREXECUTIONHOSTFS_XXX", executionFileSystem);

		System.out.println("Finished jsdl document:\n\n");
		System.out.println(jsdl_string);
		
		try {
			jsdl = SeveralXMLHelpers.fromString(jsdl_string);
		} catch (Exception e7) {
			System.err.println("Couldn't convert jsdl string to xml document: "+e7.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e8) {
				System.err.println("Couln't delete partially created job from db: "+e8.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		// now we're setting the job description on the remote grisu backend and connect it to the already
		// create job
		try {
			serviceInterface.setJobDescription(jobname, jsdl);
		} catch (Exception e1) {
			System.err.println("Couldn't set job description: "+e1.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e9) {
				System.err.println("Couln't delete partially created job from db: "+e9.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		try {
			serviceInterface.submitJob(jobname, vo);
		} catch (Exception e10) {
			System.err.println("Could not submit job: "+e10.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e11) {
				System.err.println("Couln't delete partially created job from db: "+e11.getLocalizedMessage());
			}
			System.exit(1);
		}
		
	}
	

}
