package org.vpac.grisu.backend.model.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.JsdlHelpers;

/**
 * This class holds all the relevant information about a job.
 * 
 * @author Markus Binsteiner
 * 
 */
@Entity
public class Job {

	static final Logger myLogger = Logger.getLogger(Job.class.getName());

	// for hibernate
	private Long id;

	// for the user to remember the job
	private String jobname = null;
	// the jobhandle that comes back from the job submission
	private String jobhandle = null;

	// the user's dn
	private String dn = null;

	// the vo for which the job runs
	private String fqan = null;

	// the job description
	private Document jobDescription = null;
	// this is the job description that was submitted to the gateway (probably a
	// gt4 rsl document)
	private String submittedJobDescription = null;

	// the submissionHost the job is gonna be/was submitted to
	private String submissionHost = null;

	// the status of the job
	private int status = -1;

	// the credential that is/was used to submit the job
	private ProxyCredential credential = null;

	private String submissionType = null;

	// ---------------------------------------------------------------
	// not important infos but useful
//	private String application = null;
//	private String job_directory = null;
//	private List<String> inputFiles = new ArrayList<String>();
	private Map<String, String> jobProperties = new HashMap<String, String>();
//	private String stdout = null;
//	private String stderr = null;
//
	// TODO later add requirements
	// private ArrayList<Requirement> requirements = null;

	// for hibernate
	public Job() {
	}

	/**
	 * If you use this constructor save the Job object straight away to prevent
	 * duplicate names.
	 * 
	 * @param jobname
	 *            the (base-)name you want for your job
	 */
	public Job(final String dn, final String jobname) {
		this.dn = dn;
		this.jobname = jobname;
	}

	/**
	 * Creates a Job and associates a jsdl document with it straight away. It
	 * parses this jsdl document for the name of the job, calculates the final
	 * name and stores it back into the jsdl document. Try to store it as soon
	 * as possible to prevent duplicate jobnames.
	 * 
	 * @param dn
	 *            the dn of the user who created this job
	 * @param jsdl
	 *            the job description in jsdl format
	 * @param createJobNameMethod
	 *            the method how to create the jobname (if you have already a
	 *            job with the same name)
	 * @throws SAXException
	 *             if the job description is not valid xml
	 * @throws XPathExpressionException
	 *             if the job description does not contain a jobname
	 */
	public Job(final String dn, final Document jsdl) throws SAXException,
			XPathExpressionException {
		this.dn = dn;
		// if ( ! JsdlHelpers.validateJSDL(jobDescription) ) throw new
		// SAXException("Job description not a valid jsdl document");
		this.jobDescription = jsdl;
		this.jobname = JsdlHelpers.getJobname(jsdl);
		try {
			JsdlHelpers.setJobname(jsdl, this.jobname);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		// TODO change the jobname in the jobDescription
	}

	/**
	 * The dn of the user who created/submits this job.
	 * 
	 * @return the dn
	 */
	@Column(nullable = false)
	public final String getDn() {
		return dn;
	}

	/**
	 * Sets the dn of the user who submits this job. Should be only used by
	 * hibernate
	 * 
	 * @param dn
	 *            the dn
	 */
	protected final void setDn(final String dn) {
		this.dn = dn;
	}

	/**
	 * The fqan of the VO/group for which this job is/was submitted.
	 * 
	 * @return the fqan
	 */
	public final String getFqan() {
		return fqan;
	}

	/**
	 * Sets the fqan of the VO/group for which this job is going to be
	 * submitted.
	 * 
	 * @param fqan
	 */
	public final void setFqan(final String fqan) {
		this.fqan = fqan;
	}

	/**
	 * Connects a job to a credential.
	 * 
	 * @param credential
	 *            the credential
	 */
	public final void setCredential(final ProxyCredential credential) {
		this.credential = credential;
	}

	/**
	 * Gets the credential for this job which is used to submit it to the
	 * endpoint.
	 * 
	 * @return the credential
	 */
	@Transient
	public final ProxyCredential getCredential() {
		return this.credential;
	}

	/**
	 * Gets the host to which this job is going to be submitted/was submitted.
	 * 
	 * @return the hostname
	 */
	public final String getSubmissionHost() {
		return submissionHost;
	}

	/**
	 * Sets the host to which this job is going to be submitted.
	 * 
	 * @param host
	 *            the hostname (like ng2.vpac.org)
	 */
	public final void setSubmissionHost(final String host) {
		this.submissionHost = host;
	}

	/**
	 * Gets the jsdl job description for this job.
	 * 
	 * @return the jsdl document
	 */
	@Transient
	public final Document getJobDescription() {
		// TODO return jobDescription;
		return this.jobDescription;
	}

	/**
	 * Sets the job description for this job. Take care that you have got the
	 * same jobname within this job description and in the jobname property.
	 * 
	 * @param jobDescription
	 *            the job description as jsdl xml document
	 */
	public final void setJobDescription(final Document jobDescription) {
		this.jobDescription = jobDescription;
	}

	/**
	 * Gets the (JobSubmitter-specific) jobhandle with which this job was
	 * submitted.
	 * 
	 * @return the jobhandle or null if the job was not submitted
	 */
	public final String getJobhandle() {
		return jobhandle;
	}

	/**
	 * Sets the jobhandle. Only a JobSubmitter should use this method.
	 * 
	 * @param jobhandle
	 *            the (JobSubmitter-specific) job handle
	 */
	public final void setJobhandle(final String jobhandle) {
		this.jobhandle = jobhandle;
	}

	/**
	 * Gets the (along with the users' dn unique) name of the job.
	 * 
	 * @return the jobname
	 */
	@Column(nullable = false)
	public final String getJobname() {
		return jobname;
	}

	/**
	 * Sets the name of this job. Take care that it is unique when combined with
	 * the users' dn.
	 * 
	 * @param jobname
	 *            the jobname
	 */
	private void setJobname(final String jobname) {
		this.jobname = jobname;
	}

	/**
	 * Gets the status of the job. This does not ask the responsible
	 * {@link JobSubmitter} about the status but the database. So take care to
	 * refresh the job status before using this.
	 * 
	 * @return the status of the job
	 */
	@Column(nullable = false)
	public final int getStatus() {
		return status;
	}

	/**
	 * Sets the current status of this job. Only a {@link JobSubmitter} should
	 * use this method.
	 * 
	 * @param status
	 */
	public final void setStatus(final int status) {
		this.status = status;
	}

	// hibernate
	@Id
	@GeneratedValue
	private Long getId() {
		return id;
	}

	// hibernate
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Returns the (JobSubmitter-specific) job description (like rsl for gt4).
	 * 
	 * @return the job description or null if the job was not submitted yet
	 */
	@Column(length = 2550)
	public final String getSubmittedJobDescription() {
		return submittedJobDescription;
	}

	/**
	 * Sets the (JobSubmitter-specific) job description. Only a JobSubmitter
	 * should use this method.
	 * 
	 * @param desc
	 *            the job description in the JobSubmitter-specific format
	 */
	public final void setSubmittedJobDescription(final String desc) {
		this.submittedJobDescription = desc;
	}

	/**
	 * Gets the type of the {@link JobSubmitter} that was used to submit this
	 * job.
	 * 
	 * @return the type of the submitter (like "GT4")
	 */
	public final String getSubmissionType() {
		return submissionType;
	}

	/**
	 * Sets the type of the submitter you want to use to submit this job. grisu
	 * only supports "GT4" at the moment.
	 * 
	 * @param submissionType
	 *            the type of the job submitter
	 */
	public final void setSubmissionType(final String submissionType) {
		this.submissionType = submissionType;
	}

	/**
	 * For hibernate conversion xml-document -> string.
	 * 
	 * @return xml string
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	@Column(length = 2550)
	private String getJsdl() throws TransformerException {

		return SeveralXMLHelpers.toString(jobDescription);
	}

	/**
	 * For hibernate conversion string -> xml-document.
	 * 
	 * @param jsdl_string
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void setJsdl(final String jsdl_string) throws Exception {

		if (jsdl_string == null
				|| jsdl_string
						.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
			return;
		}

		try {
			jobDescription = SeveralXMLHelpers.fromString(jsdl_string);
		} catch (Exception e) {
			myLogger
					.debug("Error saving jsdl for job. That's most probably ok. "
							+ e.getMessage());
			// e.printStackTrace();
			// TODO check what happens here
		}

	}

	public final boolean equals(final Object other) {
		if (!(other instanceof Job)) {
			return false;
		}

		Job otherJob = (Job) other;

		if (this.dn.equals(otherJob.getDn()) 
				&& this.jobname.equals(otherJob.getJobname())) {
			return true;
		} else {
			return false;
		}
	}

	public final int hashCode() {
		return this.dn.hashCode() + this.jobname.hashCode();
	}

	// ---------------------
	// job information
	// most of this will be removed once only the jobproperties map is used
	// ---------------------
//	@CollectionOfElements(fetch = FetchType.EAGER)
//	public final List<String> getInputFiles() {
//		return inputFiles;
//	}
//
//	private void setInputFiles(final List<String> inputFiles) {
//		this.inputFiles = inputFiles;
//	}
//
//	public final void addInputFile(final String inputFile) {
//		this.inputFiles.add(inputFile);
//	}
//
//	public final void removeInputFile(final String inputFile) {
//		this.inputFiles.remove(inputFile);
//	}
//
//	public final String getJob_directory() {
//		return job_directory;
//	}
//
//	public final void setJob_directory(final String job_directory) {
//		this.job_directory = job_directory;
//	}
//
//	public final String getStderr() {
//		return stderr;
//	}
//
//	public final void setStderr(final String stderr) {
//		this.stderr = stderr;
//	}
//
//	public final String getStdout() {
//		return stdout;
//	}
//
//	public final void setStdout(final String stdout) {
//		this.stdout = stdout;
//	}
//
//	public final String getApplication() {
//		return application;
//	}
//
//	public final void setApplication(final String application) {
//		this.application = application;
//	}

	@CollectionOfElements(fetch = FetchType.EAGER)
	@Column(length = 3000)
	public final Map<String, String> getJobProperties() {
		return jobProperties;
	}

	private void setJobProperties(final Map<String, String> jobProperties) {
		this.jobProperties = jobProperties;
	}

	public final void addJobProperty(final String key, final String value) {
		this.jobProperties.put(key, value);
	}

	public final void addJobProperties(final Map<String, String> properties) {
		this.jobProperties.putAll(properties);
	}

	@Transient
	public final String getJobProperty(final String key) {
		return this.jobProperties.get(key);
	}

}
