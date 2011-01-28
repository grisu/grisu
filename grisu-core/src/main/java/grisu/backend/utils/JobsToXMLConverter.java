package grisu.backend.utils;

import grisu.backend.model.job.Job;
import grisu.utils.SeveralXMLHelpers;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.constants.Constants;

/**
 * This one gathers all information of a job and converts it into a xml
 * document.
 * 
 * It will be replaced soonish with a plain Map based job information converter.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class JobsToXMLConverter {

	private static DocumentBuilder docBuilder = null;

	public static Element createJobElement(final Document doc, final Job job) {

		final Element jobElement = doc.createElement("job");

		final Attr jobname = doc.createAttribute("jobname");
		jobname.setValue(job.getJobname());
		jobElement.setAttributeNode(jobname);

		final Attr status = doc.createAttribute("status");
		status.setValue(new Integer(job.getStatus()).toString());
		jobElement.setAttributeNode(status);

		final String host = job.getJobProperty(Constants.SUBMISSION_HOST_KEY);
		if (host != null && !"".equals(host)) {
			final Attr host_attr = doc.createAttribute("host");
			host_attr.setValue(host);
			jobElement.setAttributeNode(host_attr);
		}

		final String fqan = job.getFqan();
		if (fqan != null && !"".equals(fqan)) {
			final Attr fqan_attr = doc.createAttribute("fqan");
			fqan_attr.setValue(fqan);
			jobElement.setAttributeNode(fqan_attr);
		}

		final String submissionTime = job.getJobProperty("submissionTime");
		if (submissionTime != null && !"".equals(submissionTime)) {
			final Attr submissionTime_attr = doc
					.createAttribute("submissionTime");
			submissionTime_attr.setValue(submissionTime);
			jobElement.setAttributeNode(submissionTime_attr);
		}

		return jobElement;
	}

	public static Element createJobElementNew(final Document doc, final Job job) {

		final Element jobElement = doc.createElement("job");

		final Element jobname = doc.createElement("jobname");
		jobname.setTextContent(job.getJobname());
		jobElement.appendChild(jobname);

		final Element status = doc.createElement("status");
		status.setTextContent(new Integer(job.getStatus()).toString());
		jobElement.appendChild(status);

		final String host = job.getJobProperty(Constants.SUBMISSION_HOST_KEY);
		if (host != null && !"".equals(host)) {
			final Element hostElement = doc.createElement("host");
			hostElement.setTextContent(host);
			jobElement.appendChild(hostElement);
		}

		final String fqan = job.getFqan();
		if (fqan != null && !"".equals(fqan)) {
			final Element fqanElement = doc.createElement("fqan");
			fqanElement.setTextContent(fqan);
			jobElement.appendChild(fqanElement);
		}

		final String submissionTime = job.getJobProperty("submissionTime");
		if (submissionTime != null && !"".equals(submissionTime)) {
			final Element submissionTimeElement = doc
					.createElement("submissionTime");
			submissionTimeElement.setTextContent(submissionTime);
			jobElement.appendChild(submissionTimeElement);
		}

		return jobElement;

	}

	public static Document getDetailedJobInformation(final Job job) {

		Document doc = null;

		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (final ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		final Element root = doc.createElement("jobs");
		doc.appendChild(root);

		final Element jobElement = doc.createElement("job");

		root.appendChild(jobElement);

		final Attr jobname = doc.createAttribute("jobname");
		jobname.setValue(job.getJobname());
		jobElement.setAttributeNode(jobname);

		final Attr app = doc.createAttribute("application");
		app.setValue(job.getJobProperty(Constants.APPLICATIONNAME_KEY));
		jobElement.setAttributeNode(app);

		final Attr status = doc.createAttribute("status");
		status.setValue(new Integer(job.getStatus()).toString());
		jobElement.setAttributeNode(status);

		final String host = job.getJobProperty(Constants.SUBMISSION_HOST_KEY);
		if (host != null && !"".equals(host)) {
			final Attr host_attr = doc.createAttribute("host");
			host_attr.setValue(host);
			jobElement.setAttributeNode(host_attr);
		}

		final String fqan = job.getFqan();
		if (fqan != null && !"".equals(fqan)) {
			final Attr fqan_attr = doc.createAttribute("fqan");
			fqan_attr.setValue(fqan);
			jobElement.setAttributeNode(fqan_attr);
		}

		final Element files = doc.createElement("files");
		files.setAttribute("job_directory",
				job.getJobProperty(Constants.JOBDIRECTORY_KEY));
		root.appendChild(files);

		final Element stdout = doc.createElement("file");
		stdout.setAttribute("name", "stdout");
		stdout.setTextContent(job.getJobProperty(Constants.STDOUT_KEY));
		files.appendChild(stdout);

		final Element stderr = doc.createElement("file");
		stderr.setAttribute("name", "stderr");
		stderr.setTextContent(job.getJobProperty(Constants.STDERR_KEY));
		files.appendChild(stderr);

		final Element descriptions = doc.createElement("descriptions");
		root.appendChild(descriptions);

		final Element jsdl = doc.createElement("description");
		jsdl.setAttribute("type", "jsdl");
		try {
			jsdl.setTextContent(SeveralXMLHelpers.toString(job
					.getJobDescription()));
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		descriptions.appendChild(jsdl);

		final Element rsl = doc.createElement("description");
		rsl.setAttribute("type", "rsl");
		rsl.setTextContent(job.getSubmittedJobDescription());
		descriptions.appendChild(rsl);

		return doc;
	}

	private static DocumentBuilder getDocumentBuilder() {

		if (docBuilder == null) {
			try {
				final DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				docBuilder = docFactory.newDocumentBuilder();
			} catch (final ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		return docBuilder;

	}

	public static Document getJobsInformation(final List<Job> jobs) {

		Document output = null;

		output = getDocumentBuilder().newDocument();

		final Element root = output.createElement("jobs");
		output.appendChild(root);

		for (final Job job : jobs) {
			root.appendChild(createJobElementNew(output, job));
		}

		return output;
	}

	private JobsToXMLConverter() {
	}
}
