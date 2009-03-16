

package org.vpac.grisu.js.control.Utils;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.js.model.Job;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This one gathers all information of a job and converts it into a xml document.
 * 
 * It will be replaced soonish with a plain Map based job information converter.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobsToXMLConverter {

	private static DocumentBuilder docBuilder = null;

	private static DocumentBuilder getDocumentBuilder() {

		if (docBuilder == null) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		return docBuilder;

	}

	public static Document getJobsInformation(List<Job> jobs) {

		Document output = null;

		output = getDocumentBuilder().newDocument();
		
		Element root = output.createElement("jobs");
		output.appendChild(root);

		for (Job job : jobs) {
			root.appendChild(createJobElement(output, job));
		}

		return output;
	}

	public static Element createJobElement(Document doc, Job job) {

		Element jobElement = doc.createElement("job");

		Attr jobname = doc.createAttribute("jobname");
		jobname.setValue(job.getJobname());
		jobElement.setAttributeNode(jobname);

		Attr status = doc.createAttribute("status");
		status.setValue(new Integer(job.getStatus()).toString());
		jobElement.setAttributeNode(status);

		String host = job.getSubmissionHost();
		if (host != null && !"".equals(host)) {
			Attr host_attr = doc.createAttribute("host");
			host_attr.setValue(host);
			jobElement.setAttributeNode(host_attr);
		}

		String fqan = job.getFqan();
		if (fqan != null && !"".equals(fqan)) {
			Attr fqan_attr = doc.createAttribute("fqan");
			fqan_attr.setValue(fqan);
			jobElement.setAttributeNode(fqan_attr);
		}
		
		String submissionTime = job.getJobProperty("submissionTime");
		if (submissionTime != null && !"".equals(submissionTime)) {
			Attr submissionTime_attr = doc.createAttribute("submissionTime");
			submissionTime_attr.setValue(submissionTime);
			jobElement.setAttributeNode(submissionTime_attr);
		}

		return jobElement;
	}

	public static Document getDetailedJobInformation(Job job) {

		Document doc = null;

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		Element root = doc.createElement("jobs");
		doc.appendChild(root);

		Element jobElement = doc.createElement("job");

		root.appendChild(jobElement);

		Attr jobname = doc.createAttribute("jobname");
		jobname.setValue(job.getJobname());
		jobElement.setAttributeNode(jobname);
		
		Attr app = doc.createAttribute("application");
		app.setValue(job.getApplication());
		jobElement.setAttributeNode(app);

		Attr status = doc.createAttribute("status");
		status.setValue(new Integer(job.getStatus()).toString());
		jobElement.setAttributeNode(status);

		String host = job.getSubmissionHost();
		if (host != null && !"".equals(host)) {
			Attr host_attr = doc.createAttribute("host");
			host_attr.setValue(host);
			jobElement.setAttributeNode(host_attr);
		}

		String fqan = job.getFqan();
		if (fqan != null && !"".equals(fqan)) {
			Attr fqan_attr = doc.createAttribute("fqan");
			fqan_attr.setValue(fqan);
			jobElement.setAttributeNode(fqan_attr);
		}
		
		Element files = doc.createElement("files");
		files.setAttribute("job_directory", job.getJob_directory());
		root.appendChild(files);
		
		Element stdout = doc.createElement("file");
		stdout.setAttribute("name", "stdout");
		stdout.setTextContent(job.getStdout());
		files.appendChild(stdout);
		
		Element stderr = doc.createElement("file");
		stderr.setAttribute("name", "stderr");
		stderr.setTextContent(job.getStderr());
		files.appendChild(stderr);
		
		Element descriptions = doc.createElement("descriptions");
		root.appendChild(descriptions);
		
		Element jsdl = doc.createElement("description");
		jsdl.setAttribute("type", "jsdl");
		try {
			jsdl.setTextContent(SeveralXMLHelpers.toString(job.getJobDescription()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		descriptions.appendChild(jsdl);
		
		Element rsl = doc.createElement("description");
		rsl.setAttribute("type", "rsl");
		rsl.setTextContent(job.getSubmittedJobDescription());
		descriptions.appendChild(rsl);
		
		
		
		

		return doc;
	}
}
