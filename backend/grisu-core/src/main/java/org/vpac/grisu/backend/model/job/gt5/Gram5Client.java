package org.vpac.grisu.backend.model.job.gt5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.globus.gram.Gram;
import org.globus.gram.GramException;
import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;
import org.globus.gsi.GlobusCredentialException;
import org.globus.io.gass.server.GassServer;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.vpac.security.light.plainProxy.LocalProxy;

public class Gram5Client implements GramJobListener {

        private static HashMap<String,String> contacts = new HashMap<String,String>();
        private static HashSet<String> failed = new HashSet<String>();

	static final Logger myLogger = Logger
			.getLogger(Gram5Client.class.getName());

	public String submit(String rsl, String endPoint, GSSCredential cred) {
		GramJob job = new GramJob(rsl);
		job.setCredentials(cred);
		job.addListener(this);
		try {
                    job.request(endPoint, true);
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
                    }

                        Gram.jobStatus(job);

                        myLogger.fatal("JOB ERROR IS " + job.getError());
                        myLogger.fatal("JOB STATUS IS " + job.getStatusAsString());
			myLogger.fatal("JOB ID IS " + job.getIDAsString());
                        contacts.put(job.getIDAsString(),endPoint);
			return job.getIDAsString();
		} catch (GramException ex) {

                        myLogger.fatal("REAL JOB ERROR IS " + job.getError());
                        myLogger.fatal("REAL JOB STATUS IS " + job.getStatusAsString());
                        failed.add(job.getIDAsString());
                    	java.util.logging.Logger.getLogger(Gram5Client.class.getName())
					.log(Level.SEVERE, null,ex);
			return null;
		} catch (GSSException ex) {
			java.util.logging.Logger.getLogger(Gram5Client.class.getName())
					.log(Level.SEVERE, null,ex);
			return null;
		}
	}

	public int kill(String handle, GSSCredential cred) {
		try {
			GramJob job = new GramJob(null);
			job.setID(handle);
			job.setCredentials(cred);
			try {
				new Gram().cancel(job);
				// job.signal(job.SIGNAL_CANCEL);
			} catch (GramException ex) {
				java.util.logging.Logger.getLogger(Gram5Client.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (GSSException ex) {
				java.util.logging.Logger.getLogger(Gram5Client.class.getName())
						.log(Level.SEVERE, null, ex);
			}
                        int status = job.getStatus();
			return status;
		} catch (MalformedURLException ex) {
			java.util.logging.Logger.getLogger(Gram5Client.class.getName())
					.log(Level.SEVERE, null, ex);
			throw new RuntimeException("bwahahahahaha");
		}
	}

    public int getJobStatus(String handle, GSSCredential cred) {
        if (failed.contains(handle)){
            return  GramJob.STATUS_FAILED;
        }

        GramJob job = new GramJob(null);
        try {
            job.setID(handle);
            job.setCredentials(cred);
            job.bind();
            Gram.jobStatus(job);
        } catch (GramException ex) {
            myLogger.fatal("job submitted should be ..." + handle);
            if (ex.getErrorCode() == GramException.CONNECTION_FAILED) {
                // maybe the job finished, but maybe we need to kick job manager

                String rsl = "&(restart="+ handle +")";
                GramJob restartJob = new GramJob(rsl);
                restartJob.setCredentials(cred);
                restartJob.addListener(this);
                try {
                    restartJob.request(contacts.get(handle), false);
                } catch (GramException ex1) {
                    // ok, now we are really done
                    contacts.remove(handle);
                    return GramJob.STATUS_DONE;
                } catch (GSSException ex1) {
                    throw new RuntimeException(ex1);
                }

                // nope, not done yet. 
                return getJobStatus(handle,cred);
            }
        } catch (GSSException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);

        }
        myLogger.debug("job status for " + handle + " is "
                + job.getStatusAsString());
        return job.getStatus();
    }

	public static void main(String[] args) {

		try {
			GassServer gass = new GassServer(LocalProxy.loadGSSCredential(), 0);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (GlobusCredentialException ex) {
			ex.printStackTrace();
		}

		String testRSL = args[1];
		String contact = "ng1.canterbury.ac.nz";
		try {

			Gram gram = new Gram();
			gram.ping(contact);

			GramJob testJob = new GramJob(testRSL);
			testJob.setCredentials(LocalProxy.loadGSSCredential());

			Gram5Client gram5 = new Gram5Client();
			testJob.addListener(gram5);

			// testJob.bind();

			testJob.request("ng1.canterbury.ac.nz", true);
			testJob.bind();
			gram.registerListener(testJob);
			gram.jobStatus(testJob);

			System.out
					.println("job status is : " + testJob.getStatusAsString());
			System.out.println("the job is : " + testJob.toString());
			System.out.println("number of currently active jobs : "
					+ gram.getActiveJobs());

			while (true) {
				gram.jobStatus(testJob);
				System.out.println("job status is : "
						+ testJob.getStatusAsString());
				Thread.sleep(1000);
			}

		} catch (GlobusCredentialException gx) {
			gx.printStackTrace();
		} catch (GramException grx) {
			grx.printStackTrace();
		} catch (GSSException gssx) {
			gssx.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void statusChanged(GramJob job) {
		myLogger.fatal("STATUS CHANGED STATUS CHANGED STATUS CHANGED "
				+ job.getStatusAsString());
		myLogger.fatal("the job is : " + job.toString());
	}

}
