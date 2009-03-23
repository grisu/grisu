import java.io.File;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.w3c.dom.Document;


public class QuickAndDirtyJobSubmission {

	/**
	 * args[0] myproxy username
	 * args[1] myproxy password
	 * args[2] unique jobname
	 * args[3] path to jsdl file
	 * args[4] VO
	 */
	public static void main(String[] args) throws Exception {

		
		ServiceInterface serviceInterface = Login.getServiceInterfaceForDevelopmentServer2(args[0], args[1].toCharArray());
		System.out.println("Logged in.");
		
		try {
			serviceInterface.kill(args[2], true);
		} catch (Exception e) {
			// doesn't matter
		}
		
		serviceInterface.createJob(args[2], JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME);
		
		Document jsdl = SeveralXMLHelpers.loadXMLFile(new File(args[3]));
		
		serviceInterface.setJobDescription(args[2], jsdl);
		
		serviceInterface.submitJob(args[2], args[4]);
		
	}

}
