package org.vpac.grisu.js.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.credential.model.ProxyCredential;
import org.vpac.grisu.js.control.job.JobSubmitter;
import org.vpac.grisu.js.model.utils.JsdlHelpers;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class holds all the relevant information about a job. 
 * 
 * @author Markus Binsteiner
 *
 */
public class Job {
	
	static final Logger myLogger = Logger.getLogger(Job.class
			.getName());
	
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
	// this is the job description that was submitted to the gateway (probably a gt4 rsl document)
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
	private String application = null;
	private String job_directory = null;
	private List<String> inputFiles = new ArrayList<String>();
	private Map<String,String> jobProperties = new HashMap<String, String>();
	private String stdout = null;
	private String stderr = null;
	
	//TODO later add requirements
	//private ArrayList<Requirement> requirements = null;
	
	// for hibernate
	public Job() {
	}
	
	/**
	 * If you use this constructor save the Job object straight away to prevent 
	 * duplicate names.
	 * 
	 * @param jobname the (base-)name you want for your job
	 */
	public Job(String dn, String jobname) {
		this.dn = dn;
		this.jobname = jobname;
	}
	
	/**
	 * Creates a Job and associates a jsdl document with it straight away. It parses this jsdl document for the name of the job, calculates the
	 * final name and stores it back into the jsdl document. Try to store it as soon as possible to prevent duplicate jobnames.
	 * 
	 * @param dn the dn of the user who created this job
	 * @param jsdl the job description in jsdl format
	 * @param createJobNameMethod the method how to create the jobname (if you have already a job with the same name)
	 * @throws SAXException if the job description is not valid xml
	 * @throws XPathExpressionException if the job description does not contain a jobname
	 */
	public Job(String dn, Document jsdl) throws SAXException, XPathExpressionException {
		this.dn = dn;
		//if ( ! JsdlHelpers.validateJSDL(jobDescription) ) throw new SAXException("Job description not a valid jsdl document");
		this.jobDescription = jsdl;
		this.jobname = JsdlHelpers.getJobname(jsdl);
		try {
			JsdlHelpers.setJobname(jsdl, this.jobname);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		//TODO change the jobname in the jobDescription
	}

	/**
	 * The dn of the user who created/submits this job.
	 * @return the dn
	 */
	public String getDn() {
		return dn;
	}

	/**
	 * Sets the dn of the user who submits this job. Should be only used by hibernate
	 * @param dn the dn
	 */
	protected void setDn(String dn) {
		this.dn = dn;
	}

	/**
	 * The fqan of the VO/group for which this job is/was submitted.
	 * @return the fqan
	 */
	public String getFqan() {
		return fqan;
	}

	/**
	 * Sets the fqan of the VO/group for which this job is going to be submitted.
	 * @param fqan
	 */
	public void setFqan(String fqan) {
		this.fqan = fqan;
	}

	/**
	 * Connects a job to a credential.
	 * 
	 * @param credential the credential
	 */
	public void setCredential(ProxyCredential credential) {
		this.credential = credential;
	}
	
	/**
	 * Gets the credential for this job which is used to submit it to the endpoint.
	 * @return the credential
	 */
	public ProxyCredential getCredential() {
		return this.credential;
	}
	
	/**
	 * Gets the host to which this job is going to be submitted/was submitted.
	 * @return the hostname
	 */
	public String getSubmissionHost() {
		return submissionHost;
	}
	
	/**
	 * Sets the host to which this job is going to be submitted.
	 * @param host the hostname (like ng2.vpac.org)
	 */
	public void setSubmissionHost(String host) {
		this.submissionHost = host;
	}

	/**
	 * Gets the jsdl job description for this job.
	 * 
	 * @return the jsdl document
	 */
	public Document getJobDescription() {
		//TODO return jobDescription;
		return this.jobDescription;
	}
	
	/**
	 * Sets the job description for this job. Take care that you have got the same jobname within this job description and 
	 * in the jobname property.
	 * 
	 * @param jobDescription the job description as jsdl xml document
	 */
	public void setJobDescription(Document jobDescription) {
		this.jobDescription = jobDescription;
	}

	/**
	 * Gets the (JobSubmitter-specific) jobhandle with which this job was submitted.
	 * @return the jobhandle or null if the job was not submitted
	 */
	public String getJobhandle() {
		return jobhandle;
	}
	
	/**
	 * Sets the jobhandle. Only a JobSubmitter should use this method.
	 * 
	 * @param jobhandle the (JobSubmitter-specific) job handle
	 */
	public void setJobhandle(String jobhandle) {
		this.jobhandle = jobhandle;
	}

	/**
	 * Gets the (along with the users' dn unique) name of the job.
	 * @return the jobname
	 */
	public String getJobname() {
		return jobname;
	}
	
	/**
	 * Sets the name of this job. Take care that it is unique when combined with the users' dn.
	 * @param jobname the jobname
	 */
	private void setJobname(String jobname) {
		this.jobname = jobname;
	}

	/**
	 * Gets the status of the job. This does not ask the responsible {@link JobSubmitter} about the status but the database. So take care
	 * to refresh the job status before using this.
	 * @return the status of the job
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * Sets the current status of this job. Only a {@link JobSubmitter} should use this method.
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	// hibernate
	private Long getId() {
		return id;
	}

	// hibernate
	private void setId(Long id) {
		this.id = id;
	}

	
	/**
	 * Returns the (JobSubmitter-specific) job description (like rsl for gt4).
	 * @return the job description or null if the job was not submitted yet
	 */
	public String getSubmittedJobDescription() {
		return submittedJobDescription;
	}
	
	/**
	 * Sets the (JobSubmitter-specific) job description. Only a JobSubmitter should use this method.
	 * @param desc the job description in the JobSubmitter-specific format
	 */
	public void setSubmittedJobDescription(String desc) {
		this.submittedJobDescription = desc;
	}

	
	/**
	 * Gets the type of the {@link JobSubmitter} that was used to submit this job.
	 * @return the type of the submitter (like "GT4")
	 */
	public String getSubmissionType() {
		return submissionType;
	}

	/**
	 * Sets the type of the submitter you want to use to submit this job. grisu only supports "GT4" at the moment.
	 * @param submissionType the type of the job submitter
	 */
	public void setSubmissionType(String submissionType) {
		this.submissionType = submissionType;
	}
	
	/**
	 * For hibernate conversion xml-document -> string
	 * 
	 * @return xml string
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	private String getJsdl() throws TransformerFactoryConfigurationError, TransformerException {
		
		return SeveralXMLHelpers.toString(jobDescription);
	}

	/**
	 * For hibernate conversion string -> xml-document
	 * @param jsdl_string
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void setJsdl(String jsdl_string) throws Exception {
		
		if ( jsdl_string == null || jsdl_string.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") )
			return;
		
		try {
			jobDescription = SeveralXMLHelpers.fromString(jsdl_string);
		} catch (Exception e) {
			myLogger.error("Error saving jsdl for job. That's most probably ok. "+e.getMessage());
//			e.printStackTrace();
			//TODO check what happens here
		}
		
	}
	
	public boolean equals(Object other) {
		if ( !(other instanceof Job) ) {
			return false;
		}
		
		Job otherJob = (Job)other;
		
		if ( this.dn.equals(otherJob.getDn()) && this.jobname.equals(otherJob.getJobname()) )
			return true;
		else return false;
	}
	
	public int hashCode() {
		return this.dn.hashCode() + this.jobname.hashCode();
	}
	
	// ---------------------
	// job information
	// most of this will be removed once only the jobproperties map is used
	// ---------------------

	public List<String> getInputFiles() {
		return inputFiles;
	}

	private void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public void addInputFile(String inputFile) {
		this.inputFiles.add(inputFile);
	}
	
	public void removeInputFile(String inputFile) {
		this.inputFiles.remove(inputFile);
	}

	public String getJob_directory() {
		return job_directory;
	}

	public void setJob_directory(String job_directory) {
		this.job_directory = job_directory;
	}

	public String getStderr() {
		return stderr;
	}

	public void setStderr(String stderr) {
		this.stderr = stderr;
	}

	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Map<String, String> getJobProperties() {
		return jobProperties;
	}

	private void setJobProperties(Map<String, String> jobProperties) {
		this.jobProperties = jobProperties;
	}
	
	public void addJobProperty(String key, String value) {
		this.jobProperties.put(key, value);
	}
	
	public void addJobProperties(Map<String, String> properties) {
		this.jobProperties.putAll(properties);
	}
	
	public String getJobProperty(String key) {
		return this.jobProperties.get(key);
	}
	


}
