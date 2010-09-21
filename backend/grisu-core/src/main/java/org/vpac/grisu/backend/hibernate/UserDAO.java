package org.vpac.grisu.backend.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.vpac.grisu.backend.model.User;

/**
 * This class takes care of storing users and their properties into the a
 * database using hibernate.
 * 
 * @author Markus Binsteiner
 * 
 */
public class UserDAO extends BaseHibernateDAO {

	private static Logger myLogger = Logger.getLogger(UserDAO.class.getName());

	// TODO improve this (check:
	// http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)

	// private static String getDN(User user) {
	// return user.getDn();
	// }

	public final synchronized void delete(final User persistentInstance) {
		myLogger.debug("deleting Job instance");

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().delete(persistentInstance);

			getCurrentSession().getTransaction().commit();

			myLogger.debug("delete successful");
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
	 * Looks up the database whether a user with the specified dn is already
	 * persisted.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final User findUserByDN(final String dn) {
		myLogger.debug("Loading user with dn: " + dn + " from db.");
		final String queryString = "from org.vpac.grisu.backend.model.User as user where user.dn = ?";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			queryObject.setParameter(0, dn);
			try {
				final User user = (User) queryObject.uniqueResult();
				getCurrentSession().getTransaction().commit();
				return user;

			} catch (final QueryException qe) {
				// means user not in db yet.
				myLogger.debug("User " + dn + " not found in database...");
				return null;
			}

		} catch (final RuntimeException e) {
			e.printStackTrace();
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

	public final synchronized void saveOrUpdate(final User instance) {
		myLogger.debug("attaching dirty Job instance");

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			myLogger.debug("saveOrUpdate successful");
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
