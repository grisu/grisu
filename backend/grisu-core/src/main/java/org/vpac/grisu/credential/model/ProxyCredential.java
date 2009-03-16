/* Copyright 2006 VPAC
 * 
 * This file is part of Grisu.
 * Grisu is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.

 * Girus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Grisu; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.vpac.grisu.credential.model;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.globus.gsi.GlobusCredentialException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.vpac.grisu.control.utils.CertHelpers;
import org.vpac.grisu.js.model.Job;
import org.vpac.security.light.CredentialHelpers;

/**
 * This extends the {@link Credential} abstract class. It uses a normal GSSCredential as credential. Most likely you
 * won't need a different implementation of the {@link Credential} abstract class.
 * 
 * @author Markus Binsteiner
 *
 */
/**
 * @author Markus Binsteiner
 *
 */
public class ProxyCredential {
	
	// the internal "non-raw" credential data
	private GSSCredential gsscredential = null;
	
	/**
	 * This constructor creates a ProxyCredential using a standard GSSCredential as parameter and wraps
	 * all the grisu-credential specific things around it.
	 * @param proxy the GSSCredential
	 * @throws Exception if something goes wrong
	 */
	public ProxyCredential(GSSCredential proxy) throws Exception{
		
		setCredentialData(convertFromGSSCredential(proxy));
	}
	
	/**
	 * This constructor wraps a voms enabled proxy into this ProxyCredential class.
	 * @param proxy a voms proxy
	 * @param fqan the fqan for the above voms proxy
	 * @throws Exception if something is wrong with the proxy
	 */
	public ProxyCredential(GSSCredential proxy, String fqan) throws Exception{
		setCredentialData(convertFromGSSCredential(proxy));
		setFqan(fqan);
	}
	
	// For hibernate only
	protected ProxyCredential() {
	}
	
	// all jobs that use this credential
	private Set<Job> jobs = new HashSet<Job>(); 
	
	// the "raw" credential data
	protected byte[] credentialData = null;
	
	// for hibernate
	private Long id = null;
	
	// the dn of the credential - easier for db queries that way
	protected String dn = null;
	// when will this credential expire - easier for db queries that way
	protected Date expiryDate = null;
	// the (primary) fqan of this certificate (can be null)
	protected String fqan = null;
	// whether this certificate is renewable - not used yet but maybe usefull for MyProxy certificates
	protected Boolean renewable = false;
	
	/**
	 * This should be used only by hibernate.
	 * This only sets the dn from the database. We trust that this is the right dn.
	 * Once the CredentialData is loaded from the database, the dn gets updated.
	 * @param dn the dn
	 */
	protected void setDn(String dn) {
		this.dn = dn;
	}
	
	/**
	 * This should be used only by hibernate.
	 * This only sets the expiry date from the database. We trust that this is the right expiry date.
	 * Once the CredentialData is loaded from the database, the expiry date gets updated.
	 * @param expiryDate the expiry date
	 */
	protected void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	/**
	 * This should be used only by hibernate.
	 * This only sets the fqan from the database. We trust that this is the right fqan.
	 * Once the CredentialData is loaded from the database, the fqan gets updated.
	 * @param fqan the fqan
	 */
	protected void setFqan(String fqan) {
		this.fqan = fqan;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.credential.model.GenericCredential#getJobs()
	 */
	public Set<Job> getJobs() {
		return jobs;
	}

	/**
	 * Used by hibernate to initiate the credential object when loading it from the database
	 * @param jobs all jobs that are using this credential
	 */
	private void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.credential.model.GenericCredential#addJob(org.vpac.grisu.js.model.Job)
	 */
	public void addJob(Job job){
		job.setCredential(this);
		
		if ( ! jobs.contains(job) ) 
			jobs.add(job);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.credential.model.GenericCredential#remove(org.vpac.grisu.js.model.Job)
	 */
	public void remove(Job job) {
		jobs.remove(job);
	}

	/* (non-Javadoc)
	 * @see org.vpac.grisu.credential.model.GenericCredential#getId()
	 */
	public Long getId() {
		return id;
	}

	/**
	 * For hibernate. Sets the id of the job.
	 * @param id the new id of the job.
	 */
	private void setId(Long id) {
		this.id = id;
	}
	
	
	
	/**
	 * Just a helper method.
	 * @param proxy the proxy in GSS format
	 * @return a byte array holding the credential
	 */
	private byte[] convertFromGSSCredential(GSSCredential proxy){
		
		byte[] data = null;
		try {
			data = CredentialHelpers.convertGSSCredentialToByteArray(proxy);
		} catch (GSSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return data;
		
	}
	
	/**
	 * Just a helper method
	 * @param data a byte array hodlding the credential
	 * @return the credential in GSS format
	 */
	private GSSCredential convertToGSSCredential(byte[] data){
		
		GSSCredential cred = null;
		try {
			cred = CredentialHelpers.convertByteArrayToGSSCredential(data);
		} catch (GSSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return cred;
	}
	
	/**
	 * Returns the credential in GSS format. This is just a convenience method for credentials that are of type
	 * ProxyCredential. It does not exist for the superclass.
	 * @return the credential data in GSS format
	 */
	public GSSCredential getGssCredential() {
		if ( credentialData == null ) return null;
		if ( this.gsscredential == null ) {
			this.gsscredential = convertToGSSCredential(this.credentialData);
		}
		return this.gsscredential;
	}
	
	/**
	 * Returns the credential data as byte[]. The format is dependant on the type of the credential. (But
	 * we are using X509 proxies only - so don't worry about that.) 
	 * @return the "raw" credential data
	 */
	public byte[] getCredentialData(){
		return credentialData;
	}
	
	/**
	 * Sets the credential using a byte[]. This is used by hibernate to load the data back into a credential
	 * object form a database entry.
	 * @param data the credential data
	 */
	protected void setCredentialData(byte[] data) {
		this.credentialData = data;
		this.gsscredential = convertToGSSCredential(data);
		// forcing these two fields to refresh
		this.dn = null;
		this.expiryDate = null;
	}

	/**
	 * The credential type specific way of getting the dn
	 * @return the dn of the credential
	 */
	public String getDn() {
		if ( dn == null ) {
			dn = CertHelpers.getDnInProperFormat(getGssCredential());}
		
		return dn;
	}
	

	/**
	 * The credential type specific way of getting the expiry date
	 * @return the expiry date of the credential
	 */
	public Date getExpiryDate() {
		if ( expiryDate == null ) {
		long fromNow = 0;
		try {
			fromNow = getGssCredential().getRemainingLifetime();
		} catch (GSSException e) {
			// return null in that case
			e.printStackTrace();
			return null;
		}
		
		expiryDate =  new Date(new Date().getTime()+fromNow*1000);
		} 
		return expiryDate;
		
	}

	/**
	 * The credential type specific way of getting the FQAN (VOMS information) out of the credential.
	 * @return the FQAN of this credential or null if there is none
	 */
	public String getFqan() {
		return fqan;
	}
	
	/**
	 * Not used yet
	 * @return
	 */
	public boolean isRenewable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Not used yet
	 * @return
	 */
	public boolean renew() {
		// TODO Auto-generated method stub
		return false;
	}


	
//	public static void main(String[] args) throws GlobusCredentialException, Exception {
//		
//		ProxyCredentialDAO creddao = new ProxyCredentialDAO();
//		ProxyCredential cred = new ProxyCredential(CredentialHelpers.wrapGlobusCredential(CredentialHelpers.loadGlobusCredential(new File("/tmp/x509up_u1000"))));
//		
//		creddao.save(cred);
//		
//		ProxyCredential cred2 = creddao.findCredentialByID(new Long(2));
//		
//		System.out.println(cred2.getExpiryDate());
//		
//	}

	/**
	 * Checks whether this credential is valid
	 * @return true - if valid; false - if not
	 */
	public boolean isValid() {
		try {
			if ( getGssCredential() == null ) return false;
			if ( getGssCredential().getRemainingLifetime() <= 0 ) return false;
			else return true;
		} catch (GSSException e) {
			return false;
		}
	}

	public void destroy() {
		
		try {
			getGssCredential().dispose();
		} catch (GSSException e) {
			// bad luck
		}
		
	}
	


}
