package grisu.backend.hibernate;

import grisu.backend.model.User;
import grisu.backend.model.job.BatchJob;
import grisu.backend.model.job.Job;
import grisu.control.exceptions.NoSuchJobException;
import grisu.jcommons.constants.Constants;

import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to make it easier to persist (and find {@link Job} objects to/from the
 * database.
 * 
 * @author Markus Binsteiner
 * 
 */
public class BatchJobDAO extends BaseHibernateDAO {

	private static Logger myLogger = LoggerFactory.getLogger(BatchJobDAO.class
			.getName());

	public final void delete(final BatchJob persistentInstance) {
		// myLogger.debug("deleting Job instance");

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().delete(persistentInstance);

			getCurrentSession().getTransaction().commit();

			// myLogger.debug("delete successful");

		} catch (final RuntimeException e) {
			myLogger.error("delete failed", e);
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (final Exception er) {
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
		// myLogger.debug("Loading batchJob with dn: " + dn
		// + " and batchJobname: " + batchJobname + " from dn.");
		final String queryString = "from grisu.backend.model.job.BatchJob as job where job.dn = ? and job.batchJobname = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, batchJobname);

			final BatchJob job = (BatchJob) (queryObject.uniqueResult());

			getCurrentSession().getTransaction().commit();

			if (job == null) {
				throw new NoSuchJobException(
						"Could not find a multiPartJob for the dn: " + dn
								+ " and the batchJobname: " + batchJobname);
			}
			return job;

		} catch (final RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (final Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final List<String> findJobNamesByDn(final String dn) {

		// myLogger.debug("Loading multipartjob with dn: " + dn + " from db.");
		final String queryString = "select batchJobname from grisu.backend.model.job.BatchJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);

			final List<String> jobnames = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobnames;

		} catch (final RuntimeException e) {
			getCurrentSession().getTransaction().rollback();
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}

	public final List<String> findJobNamesPerApplicationByDn(final String dn,
			final String application) {

		// myLogger.debug("Loading multipartjob with dn: " + dn + " from db.");
		final String queryString = "select batchJobname from grisu.backend.model.job.BatchJob as job where job.dn = ? and lower(job.jobProperties['"
				+ Constants.APPLICATIONNAME_KEY + "']) = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, application.toLowerCase());

			final List<String> jobnames = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobnames;

		} catch (final RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (final Exception er) {
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
	public final List<BatchJob> findBatchJobByDN(final String dn) {

		// myLogger.debug("Loading multipart with dn: " + dn + " from db.");
		final String queryString = "from grisu.backend.model.job.BatchJob as job where job.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);

			final List<BatchJob> jobs = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			return jobs;

		} catch (final RuntimeException e) {
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (final Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

	// /**
	// * Searches for jobs from a specific user that start with the specified
	// * jobname.
	// *
	// * @param dn
	// * the dn of the user
	// * @param batchJobname
	// * the start of the jobname
	// * @result a list of jobs that start with the specified jobname
	// * @throws NoJobFoundException
	// * if there is no such job
	// */
	// public final List<BatchJob> getSimilarJobNamesByDN(final String dn,
	// final String batchJobname) throws NoSuchJobException {
	// myLogger.debug("Loading job with dn: " + dn + " and batchJobname: "
	// + batchJobname + " from dn.");
	// String queryString =
	// "from grisu.backend.model.job.MultiPartJob as job where job.dn = ? and job.batchJobname like ?";
	//
	// try {
	// getCurrentSession().beginTransaction();
	//
	// Query queryObject = getCurrentSession().createQuery(queryString);
	// queryObject.setParameter(0, dn);
	// queryObject.setParameter(1, batchJobname + "%");
	//
	// List<BatchJob> jobs = (queryObject.list());
	//
	// getCurrentSession().getTransaction().commit();
	//
	// if (jobs.size() == 0) {
	// throw new NoSuchJobException(
	// "Could not find a job for the dn: " + dn
	// + " and the jobname: " + batchJobname);
	// }
	// return jobs;
	//
	// } catch (RuntimeException e) {
	// try {
	// getCurrentSession().getTransaction().rollback();
	// } catch (Exception er) {
	// myLogger.debug("Rollback failed.", er);
	// }
	// throw e; // or display error message
	// } finally {
	// getCurrentSession().close();
	// }
	//
	// }

	public final synchronized void saveOrUpdate(final BatchJob instance) {

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			// myLogger.debug("saveOrUpdate of multiPartjob successful");

		} catch (final RuntimeException e) {
			myLogger.error("saveOrUpdate failed", e);
			try {
				getCurrentSession().getTransaction().rollback();
			} catch (final Exception er) {
				myLogger.debug("Rollback failed.", er);
			}
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

}
