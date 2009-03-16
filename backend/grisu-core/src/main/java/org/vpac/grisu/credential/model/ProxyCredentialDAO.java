

package org.vpac.grisu.credential.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.vpac.grisu.control.exceptions.NoSuchCredentialException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.fs.model.User;
import org.vpac.grisu.hibernate.BaseHibernateDAO;
import org.vpac.grisu.hibernate.DatabaseInconsitencyException;
import org.vpac.grisu.js.model.Job;

/**
 * This class manages the storage of credentials into the database using hibernate. Probably won't get used because storing 
 * credentials in a database is kind of ugly...
 * 
 * @author Markus Binsteiner
 *
 */
public class ProxyCredentialDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(ProxyCredentialDAO.class.getName());

	//TODO improve this (check: http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)
	
	private static String getDN(ProxyCredential pc) {
		return pc.getDn();
	}
	
	/**
	 * Looks up the database whether a user with the specified dn is already 
	 * persisted
	 * 
	 * @param dn the dn of the user
	 * @return the list of matching {@link Credential}s 
	 */
	public List<ProxyCredential> findCredentialsByDN(String dn){
		myLogger.debug("Loading credentials with dn: "+dn+" from db.");
		String queryString = "from org.vpac.grisu.credential.model.ProxyCredential as cred where cred.dn = ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		
		List<ProxyCredential> creds = queryObject.list();
		
		return creds;
	}
	
	
	/**
	 * This is just for convenience. Get the credential of a job direct out of the database without having
	 * to load the job first.
	 * @param dn the user's dn
	 * @param jobname the name of the job
	 * @return the (unique) job
	 * @throws NoCredentialFoundException there is no job with this dn and jobname
	 * @throws DatabaseInconsitencyException there are several credentials with this dn and jobname. this is bad.
	 */
	public ProxyCredential findCredentialByDnAndJobname(String dn, String jobname) throws NoSuchJobException {
		myLogger.debug("Loading credential for job with dn: "+dn+" and jobname: "+jobname+" from db.");
		String queryString = "from org.vpac.grisu.js.model.Job as job where job.dn = ? and job.jobname = ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		queryObject.setParameter(1, jobname);
		
		List<Job> jobs = queryObject.list();
		
		if ( jobs.size() == 0 )
			throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname+".");
		
		if ( jobs.size() > 1 )
			throw new DatabaseInconsitencyException(jobs.size()+" job objects found in database for the dn: "+dn+" and the jobname: "+jobname+".");
		
		return jobs.iterator().next().getCredential();
	}
	
	/**
	 * Finds a credential using the id.
	 * @param id the id of the credential
	 * @return the credential
	 * @throws NoCredentialFoundException if no credential was found
	 */
	public ProxyCredential findCredentialByID(User user, Long id) throws NoSuchCredentialException {
			myLogger.debug("Loading credentials with id: "+id+" from db.");
			String queryString = "from org.vpac.grisu.credential.model.ProxyCredential as cred where cred.id = ?";
			Query queryObject = getSession(user.getDn()).createQuery(queryString);
			queryObject.setParameter(0, id);
			
			List<ProxyCredential> creds = queryObject.list();
			
			if ( creds.size() == 0 ) 
				throw new NoSuchCredentialException("Could not find a credential for the id: "+id+".");
			if ( creds.size() > 1 )
				throw new DatabaseInconsitencyException(creds.size()+" job objects found in database for the id: "+id+".");
			return creds.get(0);
		}
	
	

    /**
     * Saves the credential and returns the newly created id.
     * @param transientInstance the credential
     * @return the (newly assigned) id of the credential
     */
    public Long save(ProxyCredential transientInstance) {
        myLogger.debug("saving Credential instance");
        Long id = null;
        try {
        	String dn = getDN(transientInstance);
            id = (Long)getSession(dn).save(transientInstance);
            myLogger.debug("save successful");
            getSession(dn).flush();
//            transientInstance.setId(id);
        } catch (RuntimeException re) {
            myLogger.error("save failed", re);
            throw re;
        }
        return id;
    }
    
	public void delete(ProxyCredential persistentInstance) {
        myLogger.debug("deleting Credential instance");
        try {
        	String dn = getDN(persistentInstance);
            getSession(dn).delete(persistentInstance);
            myLogger.debug("delete successful");
            getSession(dn).flush();
        } catch (RuntimeException re) {
            myLogger.error("delete failed", re);
            throw re;
        }
    }
	
    public ProxyCredential merge(ProxyCredential detachedInstance) {
        myLogger.debug("merging Job instance");
        try {
        	String dn = getDN(detachedInstance);
        	ProxyCredential result = (ProxyCredential) getSession(dn)
                    .merge(detachedInstance);
            myLogger.debug("merge successful");
            getSession(dn).flush();
            return result;
        } catch (RuntimeException re) {
            myLogger.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(ProxyCredential instance) {
        myLogger.debug("attaching dirty Credential instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).saveOrUpdate(instance);
            myLogger.debug("attach successful");
            getSession(dn).flush();
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(ProxyCredential instance) {
        myLogger.debug("attaching clean Job instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).lock(instance, LockMode.NONE);
            myLogger.debug("attach successful");
            getSession(dn).flush();
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }


	
}
