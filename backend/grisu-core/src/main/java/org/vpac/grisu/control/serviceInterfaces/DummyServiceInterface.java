package org.vpac.grisu.control.serviceInterfaces;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.globus.myproxy.CredentialInfo;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.ietf.jgss.GSSException;
import org.vpac.grisu.backend.model.ProxyCredential;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.backend.utils.CertHelpers;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.grisu.settings.ServerPropertiesManager;
import org.vpac.grisu.settings.ServiceTemplateManagement;
import org.vpac.security.light.myProxy.MyProxy_light;
import org.vpac.security.light.plainProxy.LocalProxy;
import org.vpac.security.light.voms.VO;
import org.vpac.security.light.voms.VOManagement.VOManagement;

public class DummyServiceInterface extends AbstractServiceInterface implements
		ServiceInterface {

	private ProxyCredential credential = null;
	private String myproxy_username = null;
	private char[] passphrase = null;

	private User user;

	@Override
	protected final ProxyCredential getCredential() {

		long oldLifetime = -1;
		try {
			if (credential != null) {
				oldLifetime = credential.getGssCredential()
						.getRemainingLifetime();
			}
		} catch (GSSException e2) {
			myLogger.debug("Problem getting lifetime of old certificate: " + e2);
			credential = null;
		}
		if (oldLifetime < ServerPropertiesManager
				.getMinProxyLifetimeBeforeGettingNewProxy()) {
			myLogger.debug("Credential reached minimum lifetime. Getting new one from myproxy. Old lifetime: "
					+ oldLifetime);
			this.credential = null;
			// user.cleanCache();
		}

		if ((credential == null) || !credential.isValid()) {

			if ((myproxy_username == null) || (myproxy_username.length() == 0)) {
				if ((passphrase == null) || (passphrase.length == 0)) {
					// try local proxy
					try {
						credential = new ProxyCredential(
								LocalProxy.loadGSSCredential());
					} catch (Exception e) {
						throw new NoValidCredentialException(
								"Could not load credential/no valid login data.");
					}
					if (!credential.isValid()) {
						throw new NoValidCredentialException(
								"Local proxy is not valid anymore.");
					}
				}
			} else {
				// get credential from myproxy
				String myProxyServer = MyProxyServerParams.getMyProxyServer();
				int myProxyPort = MyProxyServerParams.getMyProxyPort();

				try {
					// this is needed because of a possible round-robin myproxy
					// server
					myProxyServer = InetAddress.getByName(myProxyServer)
							.getHostAddress();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					throw new NoValidCredentialException(
							"Could not download myproxy credential: "
									+ e1.getLocalizedMessage());
				}

				try {
					credential = new ProxyCredential(
							MyProxy_light.getDelegation(myProxyServer,
									myProxyPort, myproxy_username, passphrase,
									3600));
					if (getUser() != null) {
						getUser().cleanCache();
					}
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new NoValidCredentialException(
							"Could not get myproxy credential: "
									+ e.getLocalizedMessage());
				}
				if (!credential.isValid()) {
					throw new NoValidCredentialException(
							"MyProxy credential is not valid.");
				}
			}
		}

		return credential;

	}

	public final long getCredentialEndTime() {

		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		int myProxyPort = MyProxyServerParams.getMyProxyPort();

		try {
			// this is needed because of a possible round-robin myproxy server
			myProxyServer = InetAddress.getByName(myProxyServer)
					.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new NoValidCredentialException(
					"Could not download myproxy credential: "
							+ e1.getLocalizedMessage());
		}

		MyProxy myproxy = new MyProxy(myProxyServer, myProxyPort);
		CredentialInfo info = null;
		try {
			info = myproxy.info(getCredential().getGssCredential(),
					myproxy_username, new String(passphrase));
		} catch (MyProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info.getEndTime();

	}

	public final String getTemplate(final String name)
			throws NoSuchTemplateException {
		File file = new File(Environment.getAvailableTemplatesDirectory(), name
				+ ".template");

		String temp;
		try {
			temp = FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return temp;
	}

	@Override
	protected final synchronized User getUser() {

		if (user == null) {
			this.user = User.createUser(getCredential(), this);
		}

		user.setCred(getCredential());

		return user;
	}

	protected int kill(final String jobname) {

		Job job;
		try {
			job = jobdao.findJobByDN(getUser().getDn(), jobname);
		} catch (NoSuchJobException e) {
			return JobConstants.NO_SUCH_JOB;
		}

		return JobConstants.KILLED;
	}

	@Override
	public void kill(final String jobname, final boolean clear)
			throws RemoteFileSystemException, NoSuchJobException {

		Job job;

		job = jobdao.findJobByDN(getUser().getDn(), jobname);

		kill(jobname);

		if (clear) {
			jobdao.delete(job);
		}

	}

	public final String[] listHostedApplicationTemplates() {
		return ServiceTemplateManagement.getAllAvailableApplications();
	}

	public final void login(final String username, final String password) {

		this.myproxy_username = username;
		this.passphrase = password.toCharArray();

		try {
			getCredential();
		} catch (Exception e) {
			// e.printStackTrace();
			throw new NoValidCredentialException("No valid credential: "
					+ e.getLocalizedMessage());
		}
	}

	public final String logout() {
		Arrays.fill(passphrase, 'x');
		return null;
	}

	public void stageFiles(final String jobname)
			throws RemoteFileSystemException, NoSuchJobException {

		myLogger.debug("Dummy staging files...");
	}

	// public boolean mkdir(final String url) throws RemoteFileSystemException {
	//
	// myLogger.debug("Dummy. Not creating folder: " + url + "...");
	// return true;
	//
	// }

	@Override
	public void submitJob(final String jobname) throws JobSubmissionException {

		myLogger.info("Submitting job: " + jobname + " for user " + getDN());
		Job job;
		try {
			job = getJobFromDatabase(jobname);
		} catch (NoSuchJobException e1) {
			throw new JobSubmissionException("Job: " + jobname
					+ " could not be found in the grisu job database.");
		}

		try {
			myLogger.debug("Preparing job environment...");
			prepareJobEnvironment(job);
			myLogger.debug("Staging files...");
			stageFiles(jobname);
		} catch (Exception e) {
			throw new JobSubmissionException(
					"Could not access remote filesystem: "
							+ e.getLocalizedMessage());
		}

		if (job.getFqan() != null) {
			VO vo = VOManagement.getVO(getUser().getFqans().get(job.getFqan()));
			try {
				job.setCredential(CertHelpers.getVOProxyCredential(vo,
						job.getFqan(), getCredential()));
			} catch (Exception e) {
				throw new JobSubmissionException(
						"Could not create credential to use to submit the job: "
								+ e.getLocalizedMessage());
			}
		} else {
			job.setCredential(getCredential());
		}

		String handle = null;
		myLogger.debug("Submitting job to endpoint...");
		try {
			handle = getUser().getSubmissionManager().submit("GT4Dummy", job);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new JobSubmissionException(
					"Job submission to endpoint failed: "
							+ e.getLocalizedMessage());
		}

		if (handle == null) {
			throw new JobSubmissionException(
					"Job apparently submitted but jobhandle is null for job: "
							+ jobname);
		}

		job.addJobProperty("submissionTime",
				Long.toString(new Date().getTime()));

		// we don't want the credential to be stored with the job in this case
		// TODO or do we want it to be stored?
		job.setCredential(null);
		jobdao.saveOrUpdate(job);
		myLogger.info("Jobsubmission for job " + jobname + " and user "
				+ getDN() + " successful.");

	}

	public String upload(final DataHandler source, final String filename,
			final boolean return_absolute_url) throws RemoteFileSystemException {

		return "DummyHandle";
	}

	@Override
	public String getInterfaceInfo() {
		return "Local dummy serviceinterface to debug rsl files.";
	}
}
