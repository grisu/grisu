package org.vpac.grisu.backend.model.job.gt5;

import java.net.MalformedURLException;
import java.util.*;

import java.util.logging.Level;
import org.vpac.security.light.plainProxy.LocalProxy;
import org.globus.gsi.GlobusCredentialException;

import org.globus.gram.GramJob;
import org.globus.gram.Gram;
import org.globus.gram.GramException;
import org.globus.gram.GramJobListener;
import org.ietf.jgss.GSSException;

import org.globus.io.gass.server.GassServer;
import java.io.IOException;
import java.io.File;

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

import org.vpac.grisu.utils.SeveralXMLHelpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.InformationManager;
import au.org.arcs.jcommons.utils.JsdlHelpers;

import java.io.StringWriter;
import org.apache.log4j.Logger;
import org.vpac.grisu.utils.DebugUtils;
import org.apache.commons.lang.StringUtils;

import org.globus.rsl.*;
import org.ietf.jgss.GSSCredential;




public class Gram5Client implements GramJobListener {

   static final Logger myLogger = Logger.getLogger(Gram5Client.class
			.getName());

   public String submit(String rsl,String endPoint,GSSCredential cred){
    GramJob job = new GramJob(rsl);
    job.setCredentials(cred);
    job.addListener(this);
    try {
        job.request(endPoint, true);
        System.out.println(job.getIDAsString());
        return job.getIDAsString();
    } catch (GramException ex) {
           java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
           return null;
    } catch (GSSException ex) {
          java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
          return null;
    }
   }

   public int kill(String handle,GSSCredential cred){
        try {
            GramJob job = new GramJob(null);
            job.setID(handle);
            job.setCredentials(cred);
            try {
                new Gram().cancel(job);
                //job.signal(job.SIGNAL_CANCEL);
            } catch (GramException ex) {
                java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GSSException ex) {
                java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            return job.getStatus();
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("bwahahahahaha");
        }
   }

   public int getJobStatus(String handle, GSSCredential cred){
       GramJob job = new GramJob(null);
        try {
            job.setID(handle);
            job.setCredentials(cred);
            job.bind();
            Gram.jobStatus(job);
        } catch (GramException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GSSException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(Gram5Client.class.getName()).log(Level.SEVERE, null, ex);

        }
       System.out.println("job status for " + handle  + " is " + job.getStatusAsString());
       return job.getStatus();
   }


    public static void main(String[] args) {

        try {
            GassServer gass = new GassServer(LocalProxy.loadGSSCredential(),0);
        } catch (IOException ex){
            ex.printStackTrace();
        } catch (GlobusCredentialException ex){
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
            
            testJob.request("ng1.canterbury.ac.nz",true);
            testJob.bind();
            gram.registerListener(testJob);
            gram.jobStatus(testJob);
            
            System.out.println("job status is : " + testJob.getStatusAsString());
            System.out.println("the job is : " + testJob.toString());
            System.out.println("number of currently active jobs : " + gram.getActiveJobs());

            while (true){
                gram.jobStatus(testJob);
                System.out.println("job status is : " + testJob.getStatusAsString());
                Thread.sleep(1000);
            }

        } catch (GlobusCredentialException gx) {
            gx.printStackTrace();
        } catch (GramException grx) {
            grx.printStackTrace();
        } catch (GSSException gssx) {
            gssx.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void statusChanged(GramJob job) {
        System.out.println("STATUS CHANGED STATUS CHANGED STATUS CHANGED " + job.getStatusAsString());
        System.out.println("the job is : " + job.toString());
    }


}
