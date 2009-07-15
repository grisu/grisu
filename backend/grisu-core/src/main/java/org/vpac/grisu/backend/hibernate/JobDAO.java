

package org.vpac.grisu.backend.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.control.exceptions.NoSuchJobException;

/**
 * Class to make it easier to persist (and find {@link Job} objects to/from the database.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(JobDAO.class.getName());

		/**
	 * Looks up the database whether a user with the specified dn is already 
	 * persisted
	 * 
	 * @param dn the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public List<Job> findJobByDN(String dn){

		myLogger.debug("Loading jobs with dn: "+dn+" from db.");
		String queryString = "from org.vpac.grisu.backend.model.job.Job as job where job.dn = ?";
		
		try {
		    getCurrentSession().beginTransaction();

		    Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<Job> jobs = (List<Job>)(queryObject.list());
			
			getCurrentSession().getTransaction().commit();
			
			return jobs;
	        
		} catch (RuntimeException e) {
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}
	
	public List<String> findJobNamesByDn(String dn) {
		
		myLogger.debug("Loading jobs with dn: "+dn+" from db.");
		String queryString = "select jobname from org.vpac.grisu.backend.model.job.Job as job where job.dn = ?";
		
		try {
		    getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<String> jobnames = (List<String>)(queryObject.list());
			
			getCurrentSession().getTransaction().commit();
			
			return jobnames;
	        
		} catch (RuntimeException e) {
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
		
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
		String queryString = "from org.vpac.grisu.backend.model.job.Job as job where job.dn = ? and job.jobname = ?";
		
		try {
		    getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, jobname);

			Job job = (Job)(queryObject.uniqueResult());
			
			getCurrentSession().getTransaction().commit();
			
			if ( job == null ) {
				throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname);
			}
			return job;
	        
		} catch (RuntimeException e) {
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
		
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
		String queryString = "from org.vpac.grisu.backend.model.job.Job as job where job.dn = ? and job.jobname like ?";
		
		try {
		    getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, jobname+"%");

			List<Job> jobs = (List<Job>)(queryObject.list());
			
			getCurrentSession().getTransaction().commit();
			
			if ( jobs.size() == 0 ) {
				throw new NoSuchJobException("Could not find a job for the dn: "+dn+" and the jobname: "+jobname);
			}
			return jobs;
	        
		} catch (RuntimeException e) {
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}
    
	public void delete(Job persistentInstance) {
        myLogger.debug("deleting Job instance");

        
		try {
		    getCurrentSession().beginTransaction();

		    getCurrentSession().delete(persistentInstance);
		    
			getCurrentSession().getTransaction().commit();

			myLogger.debug("delete successful");
			
	        
		} catch (RuntimeException e) {
            myLogger.error("delete failed", e);
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
    }

    public void saveOrUpdate(Job instance) {

		try {
		    getCurrentSession().beginTransaction();

		    getCurrentSession().saveOrUpdate(instance);
		    
			getCurrentSession().getTransaction().commit();

			myLogger.debug("saveOrUpdate of job successful");
			
	        
		} catch (RuntimeException e) {
            myLogger.error("saveOrUpdate failed", e);
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
    }
    


	
}
