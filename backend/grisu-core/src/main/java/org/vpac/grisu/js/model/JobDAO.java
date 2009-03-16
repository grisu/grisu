

package org.vpac.grisu.js.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.fs.model.User;
import org.vpac.grisu.hibernate.BaseHibernateDAO;
import org.vpac.grisu.hibernate.DatabaseInconsitencyException;

/**
 * Class to make it easier to persist (and find {@link Job} objects to/from the database.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(JobDAO.class.getName());

	//TODO improve this (check: http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)
	
	private static String getDN(Job job) {
		return job.getDn();
	}
	
	/**
	 * Looks up the database whether a user with the specified dn is already 
	 * persisted
	 * 
	 * @param dn the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public List<Job> findJobByDN(String dn){
		myLogger.debug("Loading jobs with dn: "+dn+" from db.");
		String queryString = "from org.vpac.grisu.js.model.Job as job where job.dn = ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		
		List<Job> jobs = queryObject.list();
		
		return jobs;
	}
	
	public List<String> findJobNamesByDn(String dn) {
		
		myLogger.debug("Loading jobs with dn: "+dn+" from db.");
		String queryString = "select jobname from org.vpac.grisu.js.model.Job as job where job.dn = ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		
		List<String> jobNames = queryObject.list();
		
		return jobNames;
		
	}
	
	
	/**
	 * Searches for a job using the user's dn and the name of the job (which should be unique)
	 * @param dn the user's dn
	 * @param jobname the name of the job
	 * @return the (unique) job
	 * @throws NoJobFoundException there is no job with this dn and jobname
	 * @throws DatabaseInconsitencyException there are several jobs with this dn and jobname. this is bad.
	 */
	public Job findJobByDN(String dn, String jobname) throws NoSuchJobException {
		myLogger.debug("Loading job with dn: "+dn+" and jobname: "+jobname+" from dn.");
		String queryString = "from org.vpac.grisu.js.model.Job as job where job.dn = ? and job.jobname = ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		queryObject.setParameter(1, jobname);
		
		Job job = (Job)queryObject.uniqueResult();
		
		/*
		if ( jobs.size() == 0 )
			throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname+".");
		
		if ( jobs.size() > 1 )
			throw new DatabaseInconsitencyException(jobs.size()+" job objects found in database for the dn: "+dn+" and the jobname: "+jobname+".");
		*/
		if ( job == null ) {
			throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname+".");
		}
		return job;
	}
	
	/**
	 * Searches for jobs from a specific user that start with the specified jobname
	 * @param dn the dn of the user
	 * @param jobname the start of the jobname
	 * @result a list of jobs that start with the specified jobname
	 * @throws NoJobFoundException if there is no such job
	 */
	public List<Job> getSimilarJobNamesByDN(String dn, String jobname) throws NoSuchJobException {
		myLogger.debug("Loading job with dn: "+dn+" and jobname: "+jobname+" from dn.");
		String queryString = "from org.vpac.grisu.js.model.Job as job where job.dn = ? and job.jobname like ?";
		Query queryObject = getSession(dn).createQuery(queryString);
		queryObject.setParameter(0, dn);
		queryObject.setParameter(1, jobname+"%");
		
		List<Job> jobs = queryObject.list();
		
		if ( jobs.size() == 0 )
			throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname+".");
		
		return jobs;		  
		
	}
	
    public String save(Job transientInstance) {
        myLogger.debug("saving Job instance");
        try {
        	String dn = getDN(transientInstance);
            getSession(dn).save(transientInstance);
            myLogger.debug("save successful");
//            getSession(dn).flush();
            getSession(dn).close();
        } catch (RuntimeException re) {
            myLogger.error("save failed", re);
            throw re;
        }
        return transientInstance.getJobname();
    }
    
	public void delete(Job persistentInstance) {
        myLogger.debug("deleting Job instance");
        try {
        	String dn = getDN(persistentInstance);
            getSession(dn).delete(persistentInstance);
            myLogger.debug("delete successful");
            getSession(dn).flush();
//            getSession(dn).close();
        } catch (RuntimeException re) {
            myLogger.error("delete failed", re);
            throw re;
        }
    }
	
    public Job merge(Job detachedInstance) {
        myLogger.debug("merging Job instance");
        try {
        	String dn = getDN(detachedInstance);
            Job result = (Job) getSession(dn)
                    .merge(detachedInstance);
            myLogger.debug("merge successful");
            getSession(dn).flush();
//            getSession(dn).close();
            return result;
        } catch (RuntimeException re) {
            myLogger.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Job instance) {
        myLogger.debug("attaching dirty Job instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).saveOrUpdate(instance);
            myLogger.debug("attach successful");
            getSession(dn).flush();
//            getSession(dn).close();
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Job instance) {
        myLogger.debug("attaching clean Job instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).lock(instance, LockMode.NONE);
            myLogger.debug("attach successful");
            getSession(dn).flush();
//            getSession(dn).close();
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }


	
}
