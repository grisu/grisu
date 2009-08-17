package org.vpac.grisu.backend.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.backend.model.job.MultiPartJob;
import org.vpac.grisu.control.exceptions.NoSuchJobException;

/**
 * Class to make it easier to persist (and find {@link Job} objects to/from the
 * database.
 * 
 * @author Markus Binsteiner
 * 
 */
public class MultiPartJobDAO extends BaseHibernateDAO {

	private static Logger myLogger = Logger.getLogger(MultiPartJobDAO.class.getName());

	/**
	 * Looks up the database whether a user with the specified dn is already
	 * persisted.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final List<MultiPartJob> findMultiPartJobByDN(final String dn) {

		myLogger.debug("Loading multipart with dn: " + dn + " from db.");
		String queryString = "from org.vpac.grisu.backend.model.job.MultiPartJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<MultiPartJob> jobs = (List<MultiPartJob>) (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobs;

		} catch (RuntimeException e) {
			getCurrentSession().getTransaction().rollback();
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

	public final List<String> findJobNamesByDn(final String dn) {

		myLogger.debug("Loading multipartjob with dn: " + dn + " from db.");
		String queryString = "select multiPartJobId from org.vpac.grisu.backend.model.job.MultiPartJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);

			List<String> jobnames = (List<String>) (queryObject.list());

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
	 * Searches for a job using the user's dn and the name of the job (which
	 * should be unique).
	 * 
	 * @param dn
	 *            the user's dn
	 * @param multiPartJobId
	 *            the name of the job
	 * @return the (unique) job
	 * @throws NoJobFoundException
	 *             there is no job with this dn and jobname
	 * @throws DatabaseInconsitencyException
	 *             there are several jobs with this dn and jobname. this is bad.
	 */
	public final MultiPartJob findJobByDN(final String dn, final String multiPartJobId) throws NoSuchJobException {
		myLogger.debug("Loading multipartJob with dn: " + dn + " and multipartjobid: "
				+ multiPartJobId + " from dn.");
		String queryString = "from org.vpac.grisu.backend.model.job.MultiPartJob as job where job.dn = ? and job.multiPartJobId = ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, multiPartJobId);

			MultiPartJob job = (MultiPartJob) (queryObject.uniqueResult());

			getCurrentSession().getTransaction().commit();

			if (job == null) {
				throw new NoSuchJobException(
						"Could not find a multiPartJob for the dn: " + dn
								+ " and the multiPartJobId: " + multiPartJobId);
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
	 * Searches for jobs from a specific user that start with the specified
	 * jobname.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param multiPartJobId
	 *            the start of the jobname
	 * @result a list of jobs that start with the specified jobname
	 * @throws NoJobFoundException
	 *             if there is no such job
	 */
	public final List<MultiPartJob> getSimilarJobNamesByDN(final String dn, final String multiPartJobId)
			throws NoSuchJobException {
		myLogger.debug("Loading job with dn: " + dn + " and multiPartJobId: "
				+ multiPartJobId + " from dn.");
		String queryString = "from org.vpac.grisu.backend.model.job.MultiPartJob as job where job.dn = ? and job.multiPartJobId like ?";

		try {
			getCurrentSession().beginTransaction();

			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, multiPartJobId + "%");

			List<MultiPartJob> jobs = (List<MultiPartJob>) (queryObject.list());

			getCurrentSession().getTransaction().commit();

			if (jobs.size() == 0) {
				throw new NoSuchJobException(
						"Could not find a job for the dn: " + dn
								+ " and the jobname: " + multiPartJobId);
			}
			return jobs;

		} catch (RuntimeException e) {
			getCurrentSession().getTransaction().rollback();
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final void delete(final MultiPartJob persistentInstance) {
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

	public final void saveOrUpdate(final MultiPartJob instance) {

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			myLogger.debug("saveOrUpdate of multiPartjob successful");

		} catch (RuntimeException e) {
			myLogger.error("saveOrUpdate failed", e);
			getCurrentSession().getTransaction().rollback();
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

}
