package org.vpac.grisu.backend.model.job.gt4;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.globus.exec.client.GramJob;
import org.globus.exec.client.GramJobListener;
import org.globus.exec.generated.FaultType;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.StateEnumeration;
import org.globus.exec.utils.FaultUtils;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.exec.utils.rsl.RSLHelper;
import org.globus.gsi.GSIConstants;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.ietf.jgss.GSSCredential;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.faults.BaseFaultTypeDescription;
import org.vpac.grisu.control.serviceInterfaces.AbstractServiceInterface;
import org.vpac.grisu.settings.Environment;
import org.vpac.security.light.plainProxy.LocalProxy;

/**
 * A Custom GRAM Client. For GT4 Based on the GlobusRun command from Globus
 * WS-GRAM implementation GT4 WSRF/libraries are quired to compile this stuff
 * plus the following VM arguments must be used:
 * -Daxis.ClientConfigFile=/opt/gt-4.0.1/client-config.wsdd
 * -DGLOBUS_LOCATION=/opt/gt-4.0.1
 * 
 * @author Vladimir Silva
 * 
 */
public class GramClient
// Listen for job status messages
		implements GramJobListener {

	static final Logger logger = Logger
			.getLogger(AbstractServiceInterface.class.getName());

	// Amount of time to wait for job status changes
	private static final long STATE_CHANGE_BASE_TIMEOUT_MILLIS = 60000;

	private static String destroyJob(final GramJob job) {

		try {
			job.destroy();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getJobStatus(job);

	}

	/**
	 * destroys the job WSRF resource Precondition: job ! =null &&
	 * job.isRequested() && !job.isLocallyDestroyed()
	 */
	public static String destroyJob(final String gramJobHandle,
			final GSSCredential cred) {

		GramJob job;
		try {
			job = retrieveGramJob(gramJobHandle, cred);
			job.refreshStatus();
		} catch (final NoSuchGT4JobException e) {
			return "NoSuchJob";
		} catch (final Exception e) {
			return "NoSuchJob";
		}
		destroyJob(job);
		try {
			job.refreshStatus();
		} catch (final Exception e) {
			return "NoSuchJob";
		}
		return getJobStatus(job);

	}

	private static EndpointReferenceType getFactoryEPR(final String contact,
			final String factoryType) throws Exception {
		final URL factoryUrl = ManagedJobFactoryClientHelper.getServiceURL(
				contact).getURL();

		logger.debug("Factory Url: " + factoryUrl);
		return ManagedJobFactoryClientHelper.getFactoryEndpoint(factoryUrl,
				factoryType);
	}

	public static String getJobStatus(final GramJob job) {

		String condition = null;
		boolean error = false;
		String errorMessage = "NONE";

		try {

			// Get the state of our job.
			final StateEnumeration jobState = job.getState();
			condition = jobState.getValue();

			// Checking for faults...
			final FaultType myFaultType = job.getFault();
			if (myFaultType != null) {
				final BaseFaultTypeDescription[] myFaultArray = job.getFault()
						.getDescription();
				for (final BaseFaultTypeDescription currFault : myFaultArray) {
					logger.error(currFault.get_value());
					condition += "\nReason: " + currFault.get_value();
				}
			}

		} catch (final Exception e) {
			// TODO do something here
			e.printStackTrace();
			error = true;
			errorMessage = e.getMessage();
		}

		return condition;
	}

	/**
	 * Get the status of our job, as well as any faults that may have occurred.
	 * Returns a String containing the status and any faults.
	 * 
	 * @param eprFile
	 *            The EPR file for this job
	 * 
	 * @return String indicating the status of this job
	 */
	public static String getJobStatus(final String gramJobHandle,
			final GSSCredential cred) {
		GramJob job;
		try {
			job = retrieveGramJob(gramJobHandle, cred);
			job.refreshStatus();

			final int error = job.getError();

		} catch (final NoSuchGT4JobException e) {
			return "NoSuchJob";
		} catch (final Exception e) {
			return "NoSuchJob";
		}

		String status = getJobStatus(job);

		if ("Done".equals(status)) {
			final int error = job.getExitCode();
			status = status + error;
		}

		return status;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		/*
		 * Job test parameters (adjust to your needs)
		 */
		// remote host
		final String contact = "ng2.vpac.org";

		// Factory type: Fork, Condor, PBS, LSF
		// String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.FORK;

		// Job XML
		final File rslFile = new File("/tmp/simple.xml");

		System.setProperty("GLOBUS_LOCATION",
				"/home/markus/workspace/grisu-core/globus");
		System.setProperty("axis.ClientConfigFile",
				"/home/markus/workspace/grisu-core/globus/client-config.wsdd");

		// Deafult Security: Host authorization + XML encryption
		final Authorization authz = HostAuthorization.getInstance();
		final Integer xmlSecurity = GSIConstants.ENCRYPTION;

		// Submission mode: batch = will not wait
		final boolean batchMode = false;

		// a Simple command executable (if no job file)
		final String simpleJobCommandLine = null;

		// Job timeout values: duration, termination times
		final Date serviceDuration = null;
		final Date serviceTermination = null;
		final int timeout = GramJob.DEFAULT_TIMEOUT;

		try {
			final GramClient gram = new GramClient(
					LocalProxy.loadGSSCredential());
			// gram.submitRSL(getFactoryEPR(contact,factoryType)
			// , simpleJobCommandLine, rslFile
			// , authz, xmlSecurity
			// , batchMode, false, false
			// , serviceDuration, serviceTermination, timeout );

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static GramJob retrieveGramJob(final String gramJobHandle,
			final GSSCredential cred) throws NoSuchGT4JobException {
		// TODO: Remove these? These error things are kinda useless if not
		// returned.

		final GramJob job = new GramJob();

		try {

			System.setProperty("GLOBUS_LOCATION", Environment.getGlobusHome());
			System.setProperty("axis.ClientConfigFile",
					Environment.getAxisClientConfig());

			// Find our job, and refresh its status.
			job.setHandle(gramJobHandle);
			job.setCredentials(cred);
		} catch (final Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			// means no such job
			throw new NoSuchGT4JobException("Could not find job: "
					+ gramJobHandle);
		}

		return job;
	}

	private GSSCredential credential = null;

	// proxy credential
	// private String proxyPath = null;

	/**
	 * Job submission member variables.
	 */
	private GramJob job;
	// completed if Done or Failed
	private boolean jobCompleted = false;
	// Batch runs will not wait for the job to complete
	private boolean batch;

	// Delegation
	private final boolean limitedDelegation = true;

	private final boolean delegationEnabled = true;

	// Don't print messages by default
	private boolean quiet = false;

	/**
	 * Application error state.
	 */
	private final boolean noInterruptHandling = false;

	private final boolean isInterrupted = true;

	private final boolean normalApplicationEnd = false;

	public GramClient(final GSSCredential credential) {
		this.credential = credential;

		System.setProperty("GLOBUS_LOCATION", Environment.getGlobusHome());
		System.setProperty("axis.ClientConfigFile",
				Environment.getAxisClientConfig());

	}

	/**
	 * Print error message with prefix.
	 */
	private void printError(final String message) {
		System.err.println(message);
	}

	private void printJobFault(final GramJob job) {
		final BaseFaultType fault = job.getFault();
		if (fault != null) {
			printMessage("Fault:\n" + FaultUtils.faultToString(fault));
		}
	}

	private void printJobState(final StateEnumeration jobState,
			final boolean holding) {
		String holdString = "";
		if (holding) {
			holdString = "HOLD ";
		}
		printMessage("Job State: " + holdString + jobState.getValue());
	}

	/*
	 * private String convertEPRtoString(EndpointReferenceType endpoint) throws
	 * Exception { return ObjectSerializer.toString( endpoint,
	 * org.apache.axis.message.addressing.Constants. QNAME_ENDPOINT_REFERENCE);
	 * }
	 */

	/**
	 * Print message to user if not in quiet mode.
	 * 
	 * @param message
	 *            the message to send to stdout.
	 */
	private void printMessage(final String message) {
		if (!this.quiet) {
			logger.info(message);
		}
	}

	/**
	 * Submit the GRAM Job.
	 * 
	 * @param job
	 * @param factoryEndpoint
	 * @param batch
	 * @throws Exception
	 */
	private String processJob(final GramJob job,
			final EndpointReferenceType factoryEndpoint, final boolean batch)
			throws Exception {
		// load custom proxy (if any)
		// if (proxyPath != null) {
		// try {
		// ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
		// .getInstance();
		// String handle = "X509_USER_PROXY=" + proxyPath.toString();
		//
		// GSSCredential proxy = manager.createCredential(handle
		// .getBytes(),
		// ExtendedGSSCredential.IMPEXP_MECH_SPECIFIC,
		// GSSCredential.DEFAULT_LIFETIME, null,
		// GSSCredential.INITIATE_AND_ACCEPT);
		// job.setCredentials(proxy);
		// } catch (Exception e) {
		// logger.debug("Exception while obtaining user proxy: ", e);
		// printError("error obtaining user proxy: " + e.getMessage());
		// // don't exit, but resume using default proxy instead
		// }
		// }

		// assign the credential
		job.setCredentials(credential);

		// Generate a Job ID
		final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
		final String submissionID = "uuid:" + uuidgen.nextUUID();

		printMessage("Submission ID: " + submissionID);

		if (!batch) {
			job.addListener(this);
		}

		boolean submitted = false;
		int tries = 0;

		// while (!submitted) {
		tries++;

		try {
			job.submit(factoryEndpoint, batch, this.limitedDelegation,
					submissionID);
			submitted = true;
		} catch (final Exception e) {
			logger.debug("Exception while submitting the job request: ", e);
			throw new IOException("Job request error: " + e);
		}
		// }

		if (batch) {
			printMessage("CREATED MANAGED JOB SERVICE WITH HANDLE:");
			printMessage(job.getHandle());
		}

		if (logger.isDebugEnabled()) {
			final long millis = System.currentTimeMillis();
			BigDecimal seconds = new BigDecimal(((double) millis) / 1000);
			seconds = seconds.setScale(3, BigDecimal.ROUND_HALF_DOWN);
			logger.debug("Submission time (secs) after: " + seconds.toString());
			logger.debug("Submission time in milliseconds: " + millis);
		}

		if (!batch) {
			printMessage("WAITING FOR JOB TO FINISH");

			waitForJobCompletion(STATE_CHANGE_BASE_TIMEOUT_MILLIS);

			try {
				GramClient.destroyJob(this.job);
			} catch (final Exception e) {
				printError("coudl not destroy");
			}

			if (this.job.getState().equals(StateEnumeration.Failed)) {
				printJobFault(this.job);
			}
		}
		return job.getHandle();
	}

	/**
	 * Callback as a GramJobListener. Will not be called in batch mode.
	 */
	public final void stateChanged(final GramJob job) {
		final StateEnumeration jobState = job.getState();
		final boolean holding = job.isHolding();
		printMessage("========== State Notification ==========");
		printJobState(jobState, holding);
		printMessage("========================================");

		synchronized (this) {
			if (jobState.equals(StateEnumeration.Done)
					|| jobState.equals(StateEnumeration.Failed)) {

				printMessage("Exit Code: "
						+ Integer.toString(job.getExitCode()));

				this.jobCompleted = true;
			}

			notifyAll();

			// if we a running an interractive job,
			// prevent a hold from hanging the client
			if (holding && !batch) {
				logger.debug("Automatically releasing hold for interactive job");
				try {
					job.release();
				} catch (final Exception e) {
					final String errorMessage = "Unable to release job from hold";
					logger.debug(errorMessage, e);
					printError(errorMessage + " - " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Submit a WS-GRAM Job (GT4).
	 * 
	 * @param factoryEndpoint
	 *            Factory endpoint reference
	 * @param simpleJobCommandLine
	 *            Executable (null to use a job file)
	 * @param rslFileJob
	 *            XML file (null to use a command line)
	 * @param authorization
	 *            Authorizarion: Host, Self, Identity
	 * @param xmlSecurity
	 *            XML Sec: Encryption or signature
	 * @param batchMode
	 *            Submission mode: batch will not wait for completion
	 * @param dryRunMode
	 *            Used to parse RSL
	 * @param quiet
	 *            Messages/NO messages
	 * @param duration
	 *            Duartion date
	 * @param terminationDate
	 *            Termination date
	 * @param timeout
	 *            Job timeout (ms)
	 */
	public final String submitRSL(final EndpointReferenceType factoryEndpoint,
			final String simpleJobCommandLine,
			final JobDescriptionType rslFile,
			final Authorization authorization, final Integer xmlSecurity,
			final boolean batchMode, final boolean dryRunMode,
			final boolean quiet, final Date duration,
			final Date terminationDate, final int timeout) throws Exception {
		System.setProperty("GLOBUS_LOCATION", Environment.getGlobusHome());
		System.setProperty("axis.ClientConfigFile",
				Environment.getAxisClientConfig());

		this.quiet = quiet;
		this.batch = batchMode || dryRunMode; // in single job only.
		// In multi-job, -batch is not allowed. Dryrun is.

		if (batchMode) {
			printMessage("Warning: Will not wait for job completion, "
					+ "and will not destroy job service.");
		}

		if (rslFile != null) {
			try {
				this.job = new GramJob(rslFile);
			} catch (final Exception e) {
				final String errorMessage = "Unable to parse RSL from file "
						+ rslFile;
				logger.debug(errorMessage, e);
				throw new IOException(errorMessage + " - " + e.getMessage());
			}
		} else {
			this.job = new GramJob(
					RSLHelper.makeSimpleJob(simpleJobCommandLine));
		}

		job.setTimeOut(timeout);
		job.setAuthorization(authorization);
		job.setMessageProtectionType(xmlSecurity);
		job.setDelegationEnabled(this.delegationEnabled);
		final long now = new Date().getTime();
		// long future = 3600 * 1000 * 24 * 45;
		// long future = 3888000000l;
		final long future = 31536000000L;
		final long newTime = now + future;
		logger.debug("Add time: " + future + " ms.");
		logger.debug("Old time: " + now + " ms since 1970. (Now)");
		logger.debug("New time: " + newTime + " ms since 1970 (Termination).");
		final Date newDate = new Date(newTime);
		logger.debug("Old date: " + new Date(now).toString());
		logger.debug("New date: " + newDate.toString());
		// job.setDuration(newDate);
		job.setTerminationTime(newDate);
		// job.setDuration(new Date(ts));
		// job.setTerminationTime(terminationDate);
		// job.setDuration(duration);
		// job.setTerminationTime(null);
		// job.setDuration(null);

		// return null;
		return this.processJob(job, factoryEndpoint, batch);
	}

	/**
	 * Since messaging is assumed to be unreliable (i.e. a notification could
	 * very well be lost), we implement policy of pulling the remote state when
	 * a given waited-for notification has not has been received after a
	 * timeout. Note: this could however have the side-effect of hiding bugs in
	 * the service-side notification implementation.
	 * 
	 * The base delay in parameter is doubled each time the wait times out
	 * (binary exponential backoff). When a state change notification is
	 * received, the time out delay is reset to the base value.
	 * 
	 * @param maxWaitPerStateNotificationMillis
	 *            long base timeout for each state transition before pulling the
	 *            state from the service
	 */
	private synchronized void waitForJobCompletion(
			final long maxWaitPerStateNotificationMillis) throws Exception {

		long durationToWait = maxWaitPerStateNotificationMillis;
		long startTime;
		StateEnumeration oldState = job.getState();

		// prints one more state initially (Unsubmitted)
		// but cost extra remote call for sure. Null test below instead
		while (!this.jobCompleted) {
			if (logger.isDebugEnabled()) {
				logger.debug("Job not completed - waiting for state change "
						+ "(timeout before pulling: " + durationToWait
						+ " ms).");
			}

			startTime = System.currentTimeMillis(); // (re)set start time
			try {
				wait(durationToWait); // wait for a state change notif
			} catch (final InterruptedException ie) {
				final String errorMessage = "interrupted thread waiting for job to finish";
				logger.debug(errorMessage, ie);
				printError(errorMessage); // no exiting...
			}

			// now let's determine what stopped the wait():

			final StateEnumeration currentState = job.getState();
			// A) New job state change notification (good!)
			if (currentState != null && !currentState.equals(oldState)) {
				oldState = currentState; // wait for next state notif
				durationToWait = maxWaitPerStateNotificationMillis; // reset
			} else {
				final long now = System.currentTimeMillis();
				final long durationWaited = now - startTime;

				// B) Timeout when waiting for a notification (bad)
				if (durationWaited >= durationToWait) {
					logger.warn("Did not receive any new notification of "
							+ "job state change after a delay of "
							+ durationToWait + " ms.\nPulling job state.");
					// pull state from remote job and print the
					// state only if it is a new state
					// refreshJobStatus();
					job.refreshStatus();

					// binary exponential backoff
					durationToWait = 2 * durationToWait;
					// C) Some other reason
				} else {
					// wait but only for remainder of timeout duration
					durationToWait = durationToWait - durationWaited;
				}
			}
		}
	}
}
