package org.vpac.grisu.client.gridTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.utils.SeveralStringHelpers;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import com.Ostermiller.util.StringHelper;

public class PbsGridTestElement extends GridTestElement {
	
	private File jsdlFile;
	
	public PbsGridTestElement(GridTestController c, ServiceInterface si, String version,
			String submissionLocation) throws MdsInformationException {
		super(c, si, version, submissionLocation);
	}

	@Override
	protected boolean checkJobSuccess() {

		
		StringBuffer output = new StringBuffer();
		int out = runPerlScript(this.jobObject.getStdOutFile(), output);
		
		addMessage(output.toString());
		
		if ( out == 0 ) {
			return true;
		} else {
			return false;
		}

		
	}

	@Override
	protected JobObject createJobObject() throws MdsInformationException {

		jsdlFile = new File(controller.getGridTestDirectory(), "pbsTest.jsdl");
		Document jsdl = SeveralXMLHelpers.loadXMLFile(jsdlFile);
		
		JobObject job = new JobObject(serviceInterface, jsdl);
		job.addInputFileUrl(jsdlFile.getPath());
		
		return job;
		
	}

	@Override
	protected String getApplicationSupported() {
		return "pbsTest";
	}

	@Override
	protected boolean useMDS() {
		return false;
	}

	public int runPerlScript(File file, StringBuffer output) {
        String s = null;

        try {
            
        	String command = "perl "+controller.getGridTestDirectory()+
        	File.separator+"parse_scripts.pl "+jsdlFile.getPath()+" "+file.getPath();
            
        	System.out.println("Running parser script using: "+command);
        	
        	Process p = Runtime.getRuntime().exec(command);
        	p.waitFor();
        	
            BufferedReader stdOut = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            // read the output from the command
            StringBuffer out = new StringBuffer("Stdout:\n");
            while ((s = stdOut.readLine()) != null) {
            	out.append(s+"\n");
            }
            out.append("\n");
            // read any errors from the attempted command
            StringBuffer err = new StringBuffer("Stderr:\n");
            while ((s = stdError.readLine()) != null) {
            	err.append(s+"\n");
            }
            err.append("\n");
            
           	output.append(out);
           	output.append(err);
        	
        	int exitValue = p.exitValue();
           	output.append("\nExitValue: "+exitValue);
           	
            return exitValue;
        }
        catch (Exception e) {
            e.printStackTrace();
            output.append("Execution of external command failed:\n"+e.getLocalizedMessage());
            return -1;
        }
	}
	
	public static void main (String[] args) throws Exception {
		
		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/grisu-cxf/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				 "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		Document jsdl = SeveralXMLHelpers.loadXMLFile(new File("/home/markus/Desktop/grisu-grid-tests/tests/pbsTest.jsdl"));
		
		try {
			si.kill("pbsTest", true);
		} catch (Exception e) {
			//
		}
		
		JobObject job = new JobObject(si, jsdl);
		
		job.setSubmissionLocation("sque@edda-m:ng2.vpac.org");
		job.createJob("/ARCS/NGAdmin");
		
		job.submitJob();
		
		job.waitForJobToFinish(5);
		
//		try {
//			String stdout = job.getStdOutContent();
//			System.out.println(stdout);
//		} catch (Exception e) {
//			System.out.println("Could not get stdout content: "+e.getLocalizedMessage());
//		}
//		try {
//			String stderrText = job.getStdErrContent();
//			System.out.println(stderrText);
//		} catch (Exception e) {
//			System.out.println("Could not get stderr content: "+e.getLocalizedMessage());
//		}
		
		StringBuffer output = new StringBuffer();
//		int out = runPerlScript(job.getStdOutFile(), output);
//
//		System.out.println(output);
//		
//		if ( out == 0 ) {
//			System.out.println("Success!");
//		} else {
//			System.out.println("Failed!");
//		}
		
	}
	
}
