package grisu.backend.hibernate;

import grisu.backend.model.User;
import grisu.backend.model.job.Job;
import grisu.backend.model.job.JobStat;
import grisu.control.exceptions.NoSuchJobException;
import grisu.jcommons.constants.Constants;
import grisu.settings.ServerPropertiesManager;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Class to make it easier to persist (and find {@link JobStat} objects to/from the
 * database.
 * 
 * @author Markus Binsteiner
 * 
 */
public class JobStatDAO extends BaseHibernateDAO {

	private static Logger myLogger = LoggerFactory.getLogger(JobStatDAO.class
			.getName());



	/**
	 * Looks up the database whether a user with the specified dn is already
	 * persisted.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final List<JobStat> findJobByDN(final String dn,
			final boolean includeMultiPartJobs) {

		// myLogger.debug("Loading jobs with dn: " + dn + " from db.");
		String queryString;

		if (includeMultiPartJobs) {
			queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ?";
		} else {
			queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ? and job.batchJob = false";
		}

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);

			final List<JobStat> jobs = (queryObject.list());

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
	
	public final JobStat findJobByHibernateId(Long id)
			throws NoSuchJobException {

		final String queryString = "from grisu.backend.model.job.JobStat as job where job.jobHibernateId = :hibId";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter("hibId", id);

			final JobStat job = (JobStat) (queryObject.uniqueResult());

			getCurrentSession().getTransaction().commit();

			if (job == null) {
				throw new NoSuchJobException(
						"Could not find a job for the id: " + id);

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

	/**
	 * Searches for a job using the user's dn and the name of the job (which
	 * should be unique).
	 * 
	 * @param dn
	 *            the user's dn
	 * @param jobname
	 *            the name of the job
	 * @return the (unique) job
	 * @throws NoJobFoundException
	 *             there is no job with this dn and jobname
	 * @throws DatabaseInconsitencyException
	 *             there are several jobs with this dn and jobname. this is bad.
	 */
	public final JobStat findJobByDN(final String dn, final String jobname)
			throws NoSuchJobException {
		// myLogger.debug("Loading job with dn: " + dn + " and jobname: "
		// + jobname + " from dn.");
		final String queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ? and job.jobname = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, jobname);

			final JobStat job = (JobStat) (queryObject.uniqueResult());

			getCurrentSession().getTransaction().commit();

			if (job == null) {
				throw new NoSuchJobException(
						"Could not find a job for the dn: " + dn
								+ " and the jobname: " + jobname);
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

	/**
	 * Looks up the database whether a user with the specified dn is already
	 * persisted. Filters with the specified application.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final List<JobStat> findJobByDNPerApplication(final String dn,
			final String application, final boolean includeMultiPartJobs) {

		// if (StringUtils.isBlank(application)
		// || Constants.ALLJOBS_KEY.equals(application)
		// || Constants.ALLJOBS_INCL_BATCH_KEY.equals(application)) {
		// return findJobByDN(dn, includeMultiPartJobs);
		// }

		// myLogger.debug("Loading jobs with dn: " + dn + " from db.");

		String queryString;
		if (includeMultiPartJobs) {
			queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ? and lower(job.jobProperties['"
					+ Constants.APPLICATIONNAME_KEY + "']) = ?";
		} else {
			queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ? and lower(job.jobProperties['"
					+ Constants.APPLICATIONNAME_KEY
					+ "']) = ? and batchJob = false";
		}

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, application.toLowerCase());

			final List<JobStat> jobs = (queryObject.list());

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

	public final List<String> findJobNamesByDn(final String dn,
			final boolean includeMultiPartJobs) {

		// myLogger.debug("Loading jobs with dn: " + dn + " from db.");

		String queryString;
		if (includeMultiPartJobs) {
			queryString = "select jobname from grisu.backend.model.job.JobStat as job where job.dn = ?";
		} else {
			queryString = "select jobname from grisu.backend.model.job.JobStat as job where job.dn = ? and job.batchJob = false";
		}

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);

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

	public final List<String> findJobNamesPerApplicationByDn(final String dn,
			final String application, final boolean includeMultiPartJob) {

		// if (StringUtils.isBlank(application)) {
		// return findJobNamesByDn(dn, includeMultiPartJob);
		// }
		// myLogger.debug("Loading jobs with dn: " + dn + " from db.");

		String queryString;
		if (includeMultiPartJob) {
			queryString = "select jobname from grisu.backend.model.job.JobStat as job where job.dn = ? and lower(job.jobProperties['"
					+ Constants.APPLICATIONNAME_KEY + "']) = ?";
		} else {
			queryString = "select jobname from grisu.backend.model.job.JobStat as job where job.dn = ? and lower(job.jobProperties['"
					+ Constants.APPLICATIONNAME_KEY
					+ "']) = ? and job.batchJob = false";
		}

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
	 * Searches for jobs from a specific user that start with the specified
	 * jobname.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @param jobname
	 *            the start of the jobname
	 * @result a list of jobs that start with the specified jobname
	 * @throws NoJobFoundException
	 *             if there is no such job
	 */
	public final List<JobStat> getSimilarJobNamesByDN(final String dn,
			final String jobname) throws NoSuchJobException {
		// myLogger.debug("Loading job with dn: " + dn + " and jobname: "
		// + jobname + " from dn.");
		final String queryString = "from grisu.backend.model.job.JobStat as job where job.dn = ? and job.jobname like ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			queryObject.setParameter(1, jobname + "%");

			final List<JobStat> jobs = (queryObject.list());

			getCurrentSession().getTransaction().commit();

			if (jobs.size() == 0) {
				throw new NoSuchJobException(
						"Could not find a job for the dn: " + dn
								+ " and the jobname: " + jobname);
			}
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
	
	public final synchronized void saveOrUpdate(final Job job, final boolean active) {
		
		boolean stats = ServerPropertiesManager.isUseStatistics();
		
		if ( ! stats ) {
			return;
		}

		JobStat instance;
		try {
			long id = job.getId();
			
			instance = findJobByHibernateId(id);
			

		} catch (NoSuchJobException e) {
			
			instance = new JobStat();
			
			instance.setJobHibernateId(job.getId());
			instance.setDn(job.getDn());
			instance.setBatchJob(job.isBatchJob());
			instance.setJobname(job.getJobname());
			instance.setFqan(job.getFqan());
			instance.setSubmissionType(job.getSubmissionType());
			try {
				instance.setJsdl(job.getJsdl());
			} catch (Exception e1) {
				instance.setJsdl("Could not get jsdl: "+e.getLocalizedMessage());
			}
		}
		
		if ( instance.isActive() ) {
			myLogger.error("JobStat instance with job hibernate id "+instance.getJobHibernateId()+"is already inactive, should not be updated again...");
		}
		
		instance.setStatus(job.getStatus());
		instance.setProperties(Maps.newHashMap(job.getJobProperties()));
		instance.setLogMessages(Maps.newHashMap(job.getLogMessages()));
		instance.setSubmittedJobDescription(job.getSubmittedJobDescription());
		
		instance.setActive(active);
		
		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			// myLogger.debug("saveOrUpdate of job successful");

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
			// System.out.println("CLOSED. VALUE: "+instance.getStatus());
		}
	}

	public final synchronized void saveOrUpdate(final JobStat instance) {

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			// myLogger.debug("saveOrUpdate of job successful");

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
			// System.out.println("CLOSED. VALUE: "+instance.getStatus());
		}
	}

	public List<JobStat> findAllJobs() {
		// TODO Auto-generated method stub
		String queryString;
			queryString = "from grisu.backend.model.job.JobStat as job";


		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);

			final List<JobStat> jobs = (queryObject.list());

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

}
