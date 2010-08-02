package org.vpac.grisu.backend.model.job.gt5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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

    private static HashMap<String, Integer> statuses = new HashMap<String, Integer>();
    private static HashMap<String, Integer> errors = new HashMap<String, Integer>();
    static final Logger myLogger = Logger.getLogger(Gram5Client.class.getName());

    public Gram5Client() {
        try {
            GassServer gass = new GassServer(LocalProxy.loadGSSCredential(), 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (GlobusCredentialException ex) {
            ex.printStackTrace();
        }
    }

    public String submit(String rsl, String endPoint, GSSCredential cred) {
        GramJob job = new GramJob(rsl);
        job.setCredentials(cred);
        job.addListener(this);
        try {
            job.request(endPoint, false);
            Gram.jobStatus(job);
            return job.getIDAsString();
        } catch (GramException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (GSSException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
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
                java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GSSException ex) {
                java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            int status = job.getStatus();
            return status;
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private String getContactString(String handle) {
        try {
            URL url = new URL(handle);
            return url.getHost();
        } catch (MalformedURLException ex1) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex1);
            return null;
        }
    }

    public int[] getJobStatus(String handle, GSSCredential cred) {

        int[] results = new int[2];

        // we need this to catch quick failure
        Integer status = statuses.get(handle);
        if (status != null && status == GramJob.STATUS_FAILED) {
            results[0] = status;
            results[1] = errors.get(handle);
            return results;
        }

        String contact = getContactString(handle);
        GramJob job = new GramJob(null);
        try {
            // lets try to see if gateway is working first...
            Gram.ping(contact);
        } catch (GramException ex) {
            // have no idea what the status is, gateway is down:
            return new int[] {GramJob.STATUS_UNSUBMITTED,0};
        } catch (GSSException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            job.setID(handle);
            job.setCredentials(cred);
            job.bind();
            Gram.jobStatus(job);
            myLogger.debug("job status is " + job.getStatusAsString());
            myLogger.debug("job error is " + job.getError());
        } catch (GramException ex) {
            if (ex.getErrorCode() == GramException.CONNECTION_FAILED) {
                // maybe the job finished, but maybe we need to kick job manager

                myLogger.debug("restarting job");
                String rsl = "&(restart=" + handle + ")";
                GramJob restartJob = new GramJob(rsl);
                restartJob.setCredentials(cred);
                restartJob.addListener(this);
                try {

                    restartJob.request(contact, false);
                } catch (GramException ex1) {
                    // ok, now we are really done
                    return new int[]{GramJob.STATUS_DONE, 0};
                } catch (GSSException ex1) {
                    throw new RuntimeException(ex1);
                }

                // nope, not done yet. 
                return getJobStatus(handle, cred);
            }
        } catch (GSSException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);

        }
        status = job.getStatus();
        int error = job.getError();
        return new int[]{status, error};
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

            System.out.println("job status is : " + testJob.getStatusAsString());
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
        myLogger.debug("job status changed  "
                + job.getStatusAsString());
        statuses.put(job.getIDAsString(), job.getStatus());
        errors.put(job.getIDAsString(), job.getError());
        myLogger.debug("the job is : " + job.toString());
    }
}
