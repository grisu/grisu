package grisu.backend.model.job.gt4;

import grisu.backend.model.ProxyCredential;
import grisu.backend.model.job.Job;
import grisu.backend.model.job.JobSubmitter;
import grisu.control.JobConstants;
import grisu.settings.ServerPropertiesManager;
import grisu.utils.DebugUtils;
import grisu.utils.SeveralXMLHelpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.globus.exec.client.GramJob;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.exec.utils.rsl.RSLHelper;
import org.globus.exec.utils.rsl.RSLParseException;
import org.globus.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.InformationManager;
import au.org.arcs.jcommons.utils.JsdlHelpers;

/**
 * This class is the connector class between grisu and our GT4 gateways. It
 * translates the jsdl document into the rsl format and also knows how to submit
 * a job to a GT4 endpoint using WS-GRAM.
 * 
 * @author Markus Binsteiner
 * 
 */
public class GT4DummySubmitter extends JobSubmitter {

	static final Logger myLogger = Logger.getLogger(GT4DummySubmitter.class
			.getName());

	private static EndpointReferenceType getFactoryEPR(final String contact,
			final String factoryType) throws Exception {
		final URL factoryUrl = ManagedJobFactoryClientHelper.getServiceURL(
				contact).getURL();

		myLogger.debug("Factory Url: " + factoryUrl);
		return ManagedJobFactoryClientHelper.getFactoryEndpoint(factoryUrl,
				factoryType);
	}

	// // this method is just for testing. Do not use!!!
	// protected String submit(String host, String factoryType, Document jsdl,
	// GSSCredential credential) {
	//
	// JobDescriptionType jobDesc = null;
	// String submittedJobDesc = null;
	// try {
	// submittedJobDesc = createJobSubmissionDescription(jsdl);
	// jobDesc = RSLHelper.readRSL(submittedJobDesc);
	//
	// } catch (RSLParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	//
	// /*
	// * Job test parameters (adjust to your needs)
	// */
	// // remote host
	// //String contact = "ng2.vpac.org";
	//
	// // Factory type: Fork, Condor, PBS, LSF
	// //String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.FORK;
	// // String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
	//
	// // Deafult Security: Host authorization + XML encryption
	// Authorization authz = HostAuthorization.getInstance();
	// Integer xmlSecurity = Constants.ENCRYPTION;
	//
	// // Submission mode: batch = will not wait
	// boolean batchMode = true;
	//
	// // a Simple command executable (if no job file)
	// String simpleJobCommandLine = null;
	//
	// // Job timeout values: duration, termination times
	// Date serviceDuration = null;
	// Date serviceTermination = null;
	// int timeout = GramJob.DEFAULT_TIMEOUT;
	//
	// String handle = null;
	// try {
	//
	// if ( credential == null || credential.getRemainingLifetime() < 1 ) {
	// throw new NoValidCredentialException("Credential is not valid.");
	// }
	//
	// GramClient gram = new GramClient(credential);
	//
	// handle = gram.submitRSL(getFactoryEPR(host,factoryType)
	// , simpleJobCommandLine, jobDesc
	// , authz, xmlSecurity
	// , batchMode, false, false
	// , serviceDuration, serviceTermination, timeout );
	//
	// } catch (Exception e) {
	// //TODO handle that
	// e.printStackTrace();
	// }
	//
	// //job.setSubmittedJobDescription(submittedJobDesc);
	//
	// myLogger.debug("Submitted rsl job
	// description:\n--------------------------------");
	// myLogger.debug(submittedJobDesc);
	//
	// return handle;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grisu.js.control.job.JobSubmitter#createJobSubmissionDescription
	 * (org.w3c.dom.Document)
	 */
	private String createJobSubmissionDescription(
			final InformationManager infoManager, final Document jsdl) {

		DebugUtils.jsdlDebugOutput("Before translating into rsl: ", jsdl);

		Document output = null;
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			output = docBuilder.newDocument();
		} catch (final ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Add root element
		final Element job = output.createElement("job");
		output.appendChild(job);

		// Add "executable" node
		final Element executable = output.createElement("executable");
		executable.setTextContent(JsdlHelpers
				.getPosixApplicationExecutable(jsdl));
		job.appendChild(executable);

		// Add "argument"s
		final String[] arguments = JsdlHelpers
				.getPosixApplicationArguments(jsdl);
		for (final String argument : arguments) {
			if (argument != null && !"".equals(argument.trim())) {
				final Element argument_node = output.createElement("argument");
				argument_node.setTextContent(argument);
				job.appendChild(argument_node);
			}
		}

		// Add "directory"
		final Element directory = output.createElement("directory");
		directory.setTextContent(JsdlHelpers.getWorkingDirectory(jsdl));
		job.appendChild(directory);

		// "stdin" element if available
		final String stdinValue = JsdlHelpers.getPosixStandardInput(jsdl);
		if (stdinValue != null && !"".equals(stdinValue)) {
			final Element stdin = output.createElement("stdin");
			stdin.setTextContent(stdinValue);
			job.appendChild(stdin);
		}

		// Add "stdout"
		final Element stdout = output.createElement("stdout");
		stdout.setTextContent(JsdlHelpers.getPosixStandardOutput(jsdl));
		job.appendChild(stdout);

		// Add "stderr"
		final Element stderr = output.createElement("stderr");
		stderr.setTextContent(JsdlHelpers.getPosixStandardError(jsdl));
		job.appendChild(stderr);

		// Add "queue" node
		// TODO change that once I know how to specify queues in jsdl
		String queue = JsdlHelpers.getCandidateHosts(jsdl)[0]; // TODO this
		// always uses
		// the first
		// candidate
		// host - not
		// good
		if (queue.indexOf(":") != -1) {
			queue = queue.substring(0, queue.indexOf(":"));
			final Element queue_node = output.createElement("queue");
			queue_node.setTextContent(queue);
			job.appendChild(queue_node);
		}

		// Add "jobtype" if mpi
		final int processorCount = JsdlHelpers.getProcessorCount(jsdl);

		final Element jobType = output.createElement("jobType");
		final String jobTypeString = JsdlHelpers.getArcsJobType(jsdl);

		if (processorCount > 1) {
			final Element count = output.createElement("count");
			count.setTextContent(new Integer(processorCount).toString());
			job.appendChild(count);
			if (jobTypeString == null) {
				jobType.setTextContent("mpi");
			} else {
				jobType.setTextContent(jobTypeString);
			}
		} else {
			if (jobTypeString == null) {
				jobType.setTextContent("single");
			} else {
				jobType.setTextContent(jobTypeString);
			}

		}
		job.appendChild(jobType);

		// total memory
		Long memory = JsdlHelpers.getTotalMemoryRequirement(jsdl);

		if (memory != null && memory >= 0) {
			final Element totalMemory = output.createElement("maxMemory");
			// convert from bytes to mb
			memory = memory / 1024;
			totalMemory.setTextContent(memory.toString());
			job.appendChild(totalMemory);
		}

		// Add "maxWallTime" node
		final int walltime = JsdlHelpers.getWalltime(jsdl);
		if (walltime > 0) {
			final Element maxWallTime = output.createElement("maxWallTime");
			int wt = new Integer(JsdlHelpers.getWalltime(jsdl));
			// convert to minutes
			wt = wt / 60;
			maxWallTime.setTextContent(new Integer(wt).toString());
			job.appendChild(maxWallTime);
		}

		final Element fileStageIn = output.createElement("fileStageIn");
		// stage ins
		// Map<String, String> stageIns = JsdlHelpers.getStageIns(jsdl);
		// // only append stageIns element if not 0 because globus will reject
		// the job
		// if there is an empyt <stageIns> tag
		// if ( stageIns.size() > 0 ) {
		// for ( String source : stageIns.keySet() ) {
		// Element stageIn = output.createElement("transfer");
		// Element sourceURL = output.createElement("sourceUrl");
		// sourceURL.setTextContent(source);
		// stageIn.appendChild(sourceURL);
		// Element targetURL = output.createElement("destinationUrl");
		// targetURL.setTextContent(stageIns.get(source));
		// stageIn.appendChild(targetURL);
		//
		// fileStageIn.appendChild(stageIn);
		// }
		// job.appendChild(fileStageIn);
		// }
		// Extensions
		final Element extensions = output.createElement("extensions");

		// jobname
		final Element jobname = output.createElement("jobname");
		final String jobname_string = JsdlHelpers.getJobname(jsdl);
		// because of some pbs restrictions we have to keep the jobname to 6
		// chars
		if (jobname_string.length() > 6) {
			jobname.setTextContent(jobname_string.substring(jobname_string
					.length() - 6));
		}
		// jobname.setTextContent(jobname_string);
		extensions.appendChild(jobname);

		// module -- old style
		String[] modules_string = null;
		try {
			modules_string = JsdlHelpers.getModules(jsdl);
		} catch (final Exception e) {
			// doesn't matter
			myLogger.debug(e);
		}
		if (modules_string != null && modules_string.length > 0) {
			for (final String module_string : modules_string) {
				if (!"".equals(module_string)) {
					final Element module = output.createElement("module");
					module.setTextContent(module_string);
					extensions.appendChild(module);
				}
			}
		} else {

			// try to determine module to load from mds -- this will be the
			// default way of doing it later on and the module element will
			// disappear
			// it was stupid in the first place to have it...

			final String application = JsdlHelpers.getApplicationName(jsdl);
			final String version = JsdlHelpers.getApplicationVersion(jsdl);
			final String subLoc = JsdlHelpers.getCandidateHosts(jsdl)[0];

			if (Constants.GENERIC_APPLICATION_NAME.equals(application)) {
				myLogger.debug("\"generic\" application. Not trying to calculate modules...");

			} else if (StringUtils.isNotBlank(application)
					&& StringUtils.isNotBlank(version)
					&& StringUtils.isNotBlank(subLoc)) {
				// if we know application, version and submissionLocation
				final Map<String, String> appDetails = infoManager
						.getApplicationDetails(application, version, subLoc);

				try {
					modules_string = appDetails.get(Constants.MDS_MODULES_KEY)
							.split(",");

					if (modules_string == null || "".equals(modules_string)) {
						myLogger.warn("No module for this application/version/submissionLocation found. Submitting nonetheless...");
					}

				} catch (final Exception e) {
					myLogger.warn("Could not get module for this application/version/submissionLocation: "
							+ e.getLocalizedMessage()
							+ ". Submitting nonetheless...");
				}

				// if we know application and submissionlocation but version
				// doesn't matter
			} else if (application != null && version == null && subLoc != null) {

				final Map<String, String> appDetails = infoManager
						.getApplicationDetails(application, version, subLoc);

				try {
					modules_string = appDetails.get(Constants.MDS_MODULES_KEY)
							.split(",");

					if (modules_string == null || "".equals(modules_string)) {
						myLogger.warn("No module for this application/submissionLocation found. Submitting nonetheless...");
					}

				} catch (final Exception e) {
					myLogger.warn("Could not get module for this application/submissionLocation: "
							+ e.getLocalizedMessage()
							+ ". Submitting nonetheless...");
				}

			} else {
				throw new RuntimeException(
						"Can't determine module because either/or application, version submissionLocation are missing.");
			}

			if (modules_string != null && modules_string.length > 0) {

				for (final String module_string : modules_string) {
					if (!"".equals(module_string)) {
						final Element module = output.createElement("module");
						module.setTextContent(module_string);
						extensions.appendChild(module);
					}
				}
			}

		}

		// email
		final String email = JsdlHelpers.getEmail(jsdl);

		if (email != null && !"".equals(email)) {
			final Element email_address = output.createElement("email_address");
			email_address.setTextContent(email);
			extensions.appendChild(email_address);

			if (JsdlHelpers.getSendEmailOnJobStart(jsdl)) {
				final Element emailonexecution = output
						.createElement("emailonexecution");
				emailonexecution.setTextContent("yes");
				extensions.appendChild(emailonexecution);
			}

			if (JsdlHelpers.getSendEmailOnJobFinish(jsdl)) {
				final Element emailonabort = output
						.createElement("emailonabort");
				emailonabort.setTextContent("yes");
				final Element emailontermination = output
						.createElement("emailontermination");
				emailontermination.setTextContent("yes");
				extensions.appendChild(emailonabort);
				extensions.appendChild(emailontermination);
			}

		}

		final String pbsDebug = JsdlHelpers.getPbsDebugElement(jsdl);
		if (StringUtils.isNotBlank(pbsDebug)) {
			final Element pbsDebugElement = output.createElement("pbsDebug");
			pbsDebugElement.setTextContent(pbsDebug);
			extensions.appendChild(pbsDebugElement);
		}

		job.appendChild(extensions);

		// initialize StreamResult with InputFile object to save to file
		StreamResult result = null;
		try {
			final Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			result = new StreamResult(new StringWriter());
			final DOMSource source = new DOMSource(output);

			transformer.transform(source, result);
		} catch (final TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result.getWriter().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grisu.js.control.job.JobSubmitter#getJobStatus(java.lang.String,
	 * grisu.credential.model.ProxyCredential)
	 */
	@Override
	public final int getJobStatus(final String endPointReference,
			final ProxyCredential cred) {

		String status = null;
		int grisu_status = Integer.MIN_VALUE;
		status = GramClient.getJobStatus(endPointReference,
				cred.getGssCredential());

		grisu_status = translateToGrisuStatus(status);

		return grisu_status;
	}

	@Override
	public final String getServerEndpoint(final String server) {
		return "https://" + server
				+ ":8443/wsrf/services/ManagedJobFactoryService";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.js.control.job.JobSubmitter#killJob(java.lang.String,
	 * grisu.credential.model.ProxyCredential)
	 */
	@Override
	public final int killJob(final String endPointReference,
			final ProxyCredential cred) {

		String status = null;
		int grisu_status = Integer.MIN_VALUE;
		status = GramClient.destroyJob(endPointReference,
				cred.getGssCredential());

		grisu_status = translateToGrisuStatus(status);

		if (grisu_status == JobConstants.NO_SUCH_JOB) {
			return JobConstants.KILLED;
		}

		return grisu_status;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.js.control.job.JobSubmitter#submit(java.lang.String,
	 * grisu.js.model.Job)
	 */
	@Override
	protected final String submit(final InformationManager informationManager,
			final String host, final String factoryType, final Job job) {

		JobDescriptionType jobDesc = null;
		String submittedJobDesc = null;
		try {
			// String site = informationManager.getSiteForHostOrUrl(host);
			submittedJobDesc = createJobSubmissionDescription(
					informationManager, job.getJobDescription());
			jobDesc = RSLHelper.readRSL(submittedJobDesc);

		} catch (final RSLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		/*
		 * Job test parameters (adjust to your needs)
		 */
		// remote host
		// String contact = "ng2.vpac.org";
		// Factory type: Fork, Condor, PBS, LSF
		// String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.FORK;
		// String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
		// Deafult Security: Host authorization + XML encryption
		final Authorization authz = HostAuthorization.getInstance();
		final Integer xmlSecurity = GSIConstants.ENCRYPTION;

		// Submission mode: batch = will not wait
		final boolean batchMode = true;

		// a Simple command executable (if no job file)
		final String simpleJobCommandLine = null;

		// Job timeout values: duration, termination times
		final Date serviceDuration = null;
		final Date serviceTermination = null;
		final int timeout = GramJob.DEFAULT_TIMEOUT;

		final String handle = "DummyHandle" + UUID.randomUUID().toString();
		// try {
		//
		// GSSCredential credential = null;
		// credential = CredentialHelpers.convertByteArrayToGSSCredential(job
		// .getCredential().getCredentialData());
		//
		// if (credential == null || credential.getRemainingLifetime() < 1) {
		// throw new NoValidCredentialException(
		// "Credential associated with job: " + job.getDn()
		// + " / " + job.getJobname() + " is not valid.");
		// }
		//
		// GramClient gram = new GramClient(credential);
		//
		// handle = gram.submitRSL(getFactoryEPR(host, factoryType),
		// simpleJobCommandLine, jobDesc, authz, xmlSecurity,
		// batchMode, false, false, serviceDuration,
		// serviceTermination, timeout);
		//
		// } catch (Exception e) {
		// // TODO handle that
		// e.printStackTrace();
		// if (handle == null) {
		// myLogger.error("Jobhandle is null....");
		// // TODO
		// }
		// }

		job.setSubmittedJobDescription(submittedJobDesc);
		// for debug purposes

		final String uid = handle.substring(handle.indexOf("?") + 1);
		final String hostname = host.substring(0,
				host.indexOf(":8443/wsrf/services/ManagedJobFactoryService"));
		// String eprString =
		// "<ns00:EndpointReferenceType xmlns:ns00=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\">\n"
		// + "<ns00:Address>"
		// + hostname
		// + ":8443/wsrf/services/ManagedExecutableJobService</ns00:Address>\n"
		// +
		// "<ns00:ReferenceProperties><ResourceID xmlns=\"http://www.globus.org/namespaces/2004/10/gram/job\">"
		// + uid
		// + "</ResourceID></ns00:ReferenceProperties>\n"
		// +
		// "<wsa:ReferenceParameters xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\"/>\n"
		// + "</ns00:EndpointReferenceType>";
		try {
			myLogger.debug("Writing out epr file.");
			String vo = job.getFqan();
			if (vo == null || "".equals(vo)) {
				vo = "non_vo";
			} else {
				vo = vo.replace("/", "_");
			}

			// to create debug directory
			ServerPropertiesManager.getDebugModeOn();
			if (!new File(ServerPropertiesManager.getDebugDirectory()).exists()) {
				new File(ServerPropertiesManager.getDebugDirectory()).mkdirs();
			}

			final String uFileName = ServerPropertiesManager
					.getDebugDirectory()
					+ "/"
					+ job.getDn().replace("=", "_").replace(",", "_")
							.replace(" ", "_")
					+ "_"
					+ job.getJobname()
					+ "_"
					+ vo + "_" + job.hashCode();
			// FileWriter fileWriter = new FileWriter(uFileName + ".epr");
			// BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			// buffWriter.write(eprString);
			//
			// buffWriter.close();

			final FileWriter fileWriter2 = new FileWriter(uFileName + ".rsl");
			BufferedWriter buffWriter = new BufferedWriter(fileWriter2);
			buffWriter.write(submittedJobDesc);
			buffWriter.close();

			final FileWriter fileWriter3 = new FileWriter(uFileName + ".jsdl");
			buffWriter = new BufferedWriter(fileWriter3);
			buffWriter
					.write(SeveralXMLHelpers
							.toStringWithoutAnnoyingExceptions(job
									.getJobDescription()));
			buffWriter.close();

		} catch (final Exception e) {
			myLogger.error("Gt4 job submission error: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}

		myLogger.debug("Submitted rsl job description:\n--------------------------------");
		myLogger.debug(submittedJobDesc);

		return handle;

	}

	private int translateToGrisuStatus(final String status) {

		int grisu_status = Integer.MIN_VALUE;
		if ("Done".equals(status)) {
			grisu_status = JobConstants.DONE;
		} else if (status.startsWith("Done")) {
			final int error = Integer.parseInt(status.substring(4));
			grisu_status = JobConstants.DONE + error;
		} else if ("StageIn".equals(status)) {
			grisu_status = JobConstants.STAGE_IN;
		} else if ("Pending".equals(status)) {
			grisu_status = JobConstants.PENDING;
		} else if ("Unsubmitted".equals(status)) {
			grisu_status = JobConstants.UNSUBMITTED;
		} else if ("Active".equals(status)) {
			grisu_status = JobConstants.ACTIVE;
		} else if ("CleanUp".equals(status)) {
			grisu_status = JobConstants.CLEAN_UP;
		} else if ("NoSuchJob".equals(status)) {
			grisu_status = JobConstants.NO_SUCH_JOB;
		} else if (status != null && status.startsWith("Failed")) {
			grisu_status = JobConstants.FAILED;
		} else {
			grisu_status = Integer.MAX_VALUE;
		}
		return grisu_status;

	}

}
