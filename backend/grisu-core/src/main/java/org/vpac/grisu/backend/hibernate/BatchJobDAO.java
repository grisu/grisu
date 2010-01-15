package org.vpac.grisu.backend.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.backend.model.job.BatchJob;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.control.exceptions.NoSuchJobException;

import au.org.arcs.jcommons.constants.Constants;

/**
 * Class to make it easier to persist (and find {@link Job} objects to/from the
 * database.
 * 
 * @author Markus Binsteiner
 * 
 */
public class BatchJobDAO extends BaseHibernateDAO {

	private static Logger myLogger = Logger.getLogger(BatchJobDAO.class
			.getName());

	public final void delete(final BatchJob persistentInstance) {
		myLogger.debug("deleting Job instance");

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().delete(persistentInstance);

			getCurrentSession().getTransaction().commit();

			myLogger.debug("delete successful");

		} catch (RuntimeException e) {
			myLogger.error("delete failed", e);
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

	/**
	 * Searches for a job using the user's dn and the name of the job (which
	 * should be unique).
	 * 
	 * @param dn
	 *            the user's dn
	 * @param batchJobname
	 *            the name of the job
	 * @return the (unique) job
	 * @throws NoJobFoundException
	 *             there is no job with this dn and jobname
	 * @throws DatabaseInconsitencyException
	 *             there are several jobs with this dn and jobname. this is bad.
	 */
	public final BatchJob findJobByDN(final String dn, final String batchJobname)
	throws NoSuchJobException {
		myLogger.debug("Loading batchJob with dn: " + dn
				+ " and batchJobname: " + batchJobname + " from dn.");
		String queryString = "from org.vpac.grisu.backend.model.job.BatchJob as job where job.dn = ? and job.batchJobname = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, batchJobname);

			BatchJob job = (BatchJob) (queryObject.uniqueResult());

			getCurrentSession().getTransaction().commit();

			if (job == null) {
				throw new NoSuchJobException(
						"Could not find a multiPartJob for the dn: " + dn
						+ " and the batchJobname: " + batchJobname);
			}
			return job;

		} catch (RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final List<String> findJobNamesByDn(final String dn) {

		myLogger.debug("Loading multipartjob with dn: " + dn + " from db.");
		String queryString = "select batchJobname from org.vpac.grisu.backend.model.job.BatchJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<String> jobnames = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobnames;

		} catch (RuntimeException e) {
			getCurrentSession().getTransaction().rollback();
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final List<String> findJobNamesPerApplicationByDn(final String dn,
			final String application) {

		myLogger.debug("Loading multipartjob with dn: " + dn + " from db.");
		String queryString = "select batchJobname from org.vpac.grisu.backend.model.job.BatchJob as job where job.dn = ? and job.jobProperties['"
			+ Constants.APPLICATIONNAME_KEY + "'] = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, application);

			List<String> jobnames = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobnames;

		} catch (RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	/**
	 * Looks up the database whether a user with the specified dn is already
	 * persisted.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final List<BatchJob> findMultiPartJobByDN(final String dn) {

		myLogger.debug("Loading multipart with dn: " + dn + " from db.");
		String queryString = "from org.vpac.grisu.backend.model.job.BatchJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<BatchJob> jobs = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobs;

		} catch (RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

	/**
	 * Searches for jobs from a specific user that start with the specified
	 * jobname.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param batchJobname
	 *            the start of the jobname
	 * @result a list of jobs that start with the specified jobname
	 * @throws NoJobFoundException
	 *             if there is no such job
	 */
	public final List<BatchJob> getSimilarJobNamesByDN(final String dn,
			final String batchJobname) throws NoSuchJobException {
		myLogger.debug("Loading job with dn: " + dn + " and batchJobname: "
				+ batchJobname + " from dn.");
		String queryString = "from org.vpac.grisu.backend.model.job.MultiPartJob as job where job.dn = ? and job.batchJobname like ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, batchJobname + "%");

			List<BatchJob> jobs = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			if (jobs.size() == 0) {
				throw new NoSuchJobException(
						"Could not find a job for the dn: " + dn
						+ " and the jobname: " + batchJobname);
			}
			return jobs;

		} catch (RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final synchronized void saveOrUpdate(final BatchJob instance) {

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			myLogger.debug("saveOrUpdate of multiPartjob successful");

		} catch (RuntimeException e) {
			myLogger.error("saveOrUpdate failed", e);
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

}
