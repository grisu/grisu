import java.io.File;
import java.util.Arrays;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.vpac.grisu.client.control.ServiceInterfaceFactory;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.js.model.utils.JsdlHelpers;
import org.w3c.dom.Document;


public class BasicJobSubmission {

	/**
	 * First arg: myproxy username
	 * Second arg: myproxy passphrase
	 * Third arg: the vo to use to submit the job
	 * Forth arg: the path to the jsdl file (template) which will be parsed & submitted
	 * 
	 * @param args the commandline arguments
	 */
	public static void main(String[] args) {
		
		String myproxy_username = args[0];
		char[] myproxy_password = args[1].toCharArray();
		String vo = args[2];
		String path_to_jsdl_file = args[3];

		// load the xml document
		Document jsdl = SeveralXMLHelpers.loadXMLFile(new File(path_to_jsdl_file));
		// load the jobname from the jsdl document
		String jobname = JsdlHelpers.getJobname(jsdl);
		String submissionLocation = JsdlHelpers.getCandidateHosts(jsdl)[0];
		
		ServiceInterface serviceInterface = null;
		try {
			serviceInterface = ServiceInterfaceFactory.createInterface(
					"http://localhost:8080/grisu-ws/services/grisu", 
					myproxy_username, myproxy_password, 
					"myproxy.arcs.org.au", "443", 
					null, -1, null, null);
		} catch (ServiceInterfaceException e12) {
			System.err.println("Could not login to remote grisu backend: "+e12.getLocalizedMessage());
			System.exit(1);
		}
		
		try {
			serviceInterface.kill(jobname, true);
		} catch (Exception e0) {
			System.err.println("Couln't delete partially created job from db: "+e0.getLocalizedMessage());
		}
		
		// get a list of all current jobnames to be able to decide on an unused one
		String[] currentJobnames = serviceInterface.getAllJobnames();
		
		// test whether jobname already taken
		if ( Arrays.asList(currentJobnames).contains(jobname) ) {
			System.err.println("Jobname already taken.");
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e1) {
				System.err.println("Couln't delete partially created job from db: "+e1.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		// get a list of all available VOs 
		String[] allAvailableVOs = serviceInterface.getFqans();
		if ( ! Arrays.asList(allAvailableVOs).contains(vo) ) {
			System.err.println("The VO you specified is not available.");
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e2) {
				System.err.println("Couln't delete partially created job from db: "+e2.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		// get a list of all available submission locations
		String[] allAvailableSubmissionLocations = serviceInterface.getAllSubmissionLocations(vo);
		
		if ( ! Arrays.asList(allAvailableSubmissionLocations).contains(submissionLocation) ) {
			System.err.println("The submission location you specified is not available (at least for this VO).");
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e3) {
				System.err.println("Couln't delete partially created job from db: "+e3.getLocalizedMessage());
			}
			System.exit(1);
		}
		

		// now create the job on the server.
		// for the integer argument you can choose between these values (can be found in the 
		// org.vpac.grisu.control.JobConstants class in the grisu-commons module):
		//  DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME = 0;
		// ALWAYS_INCREMENT_JOB_NAME = 1;
		// ONLY_INCREMENT_JOB_NAME_IF_JOB_EXISTS_WITH_SAME_NAME = 2;
		// ALWAYS_TIMESTAMP_JOB_NAME = 3;
		// ONLY_TIMESTAMP_JOB_NAME_IF_JOB_EXISTS_WITH_SAME_NAME = 3;
		// OVERWRITE_EXISTING_JOB = 10; // not recommended
		// 
		// at the moment, only DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME & ALWAYS_INCREMENT_JOB_NAME are implemented.
		// I would recommend DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME
		
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
		
		// uploading a local file in addition to the one that is specified in the jsdl which gets
		// cross-stage by the grisu web service automatically.
		File localInputFile = new File("/home/markus/test.txt");
		DataSource source = new FileDataSource(localInputFile);
		String filename = localInputFile.getName();
		
		try {
			System.out.println("Uploading local file: "+localInputFile.toString()+"...");
			serviceInterface.upload(source, absoluteJobDirectory+"/"+filename, false);
			System.out.println("Uploading successful.");
		} catch (Exception e1) {
			System.err.println("Couldn't upload file: "+localInputFile.toString()+": "+e1.getLocalizedMessage());
			try {
				serviceInterface.kill(jobname, true);
			} catch (Exception e5) {
				System.err.println("Couln't delete partially created job from db: "+e5.getLocalizedMessage());
			}
			System.exit(1);
		}
		
		// now, we convert the jsdl to a String and just replace the placeholders with the actual values.
		// of course, there are other ways to do this...
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
		
		jsdl_string = jsdl_string.replaceAll("WORKING_DIRECTORY", workingDirectory);
		jsdl_string = jsdl_string.replaceAll("EXECUTION_HOST_FS", executionFileSystem);
		jsdl_string = jsdl_string.replaceAll("LOCALINPUTFILENAME", filename);
		
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
