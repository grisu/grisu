package org.vpac.grisu.client.gridTests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import au.org.arcs.mds.SubmissionLocationHelpers;

public class XmlRpcOutputModule implements OutputModule {

	private XmlRpcClient client;
	
	public XmlRpcOutputModule() {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    try {
			config.setServerURL(new URL("http://shib-mp.arcs.org.au/xmlrpc/"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	    
	    client = new XmlRpcClient();
	    client.setConfig(config);
	    
		
	}
	
	public void outputResult(List<GridTestElement> elements) {

		String username = "grisu_test_client";
		String password = "kaiJaej9ieSh"; 
		
		for ( GridTestElement element : elements ) {

			String application = element.getApplicationSupported();
			String version = element.getVersion();
			Date startDate = element.getStartDate();
			Date endDate = element.getEndDate();
			String submissionLocation = element.getSubmissionLocation();
			String submissionHost = SubmissionLocationHelpers.extractHost(submissionLocation);
			String queue = SubmissionLocationHelpers.extractQueue(submissionLocation);
			boolean success = !element.failed();
			String output = OutputModuleHelpers.createStringReport(element).toString();
			
			Object[] params = new Object[] {
					username,
					password,
					application,
					version,
					startDate,
					endDate,
					submissionHost,
					queue,
					success,
					output
			};

		    Integer result;
			try {
				System.out.println("Transferring results for test: "+application+", "+version+", "+submissionLocation+"...");
				result = (Integer) client.execute("new_test_result", params);
				System.out.println("Success. Output: "+result);
			} catch (XmlRpcException e) {
				System.out.println("Couldn't transfer results for test: "+application+", "+version+", "+submissionLocation+": "+e.getLocalizedMessage());
			}
			
			
		}
	    
	    

		
	}

}
