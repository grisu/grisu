package grisu.backend.hibernate;

import grisu.backend.model.User;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of storing users and their properties into the a
 * database using hibernate.
 * 
 * @author Markus Binsteiner
 * 
 */
public class UserDAO extends BaseHibernateDAO {

	private static Logger myLogger = LoggerFactory.getLogger(UserDAO.class
			.getName());

	// TODO improve this (check:
	// http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)

	// private static String getDN(User user) {
	// return user.getDn();
	// }

	public final synchronized void delete(final User persistentInstance) {
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
	 * Looks up the database whether a user with the specified dn is already
	 * persisted.
	 * 
	 * @param dn
	 *            the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public final List<User> findAllUsers() {
		// myLogger.debug("Loading user with dn: " + dn + " from db.");
		final String queryString = "from grisu.backend.model.User as user";
//		final String queryString = "select * from users";

		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createQuery(
					queryString);
			
//			final Query queryObject = getCurrentSession().createSQLQuery(queryString);

			try {
				final List<User> users = (queryObject.list());
				getCurrentSession().getTransaction().commit();
				return users;

			} catch (final QueryException qe) {
				// means user not in db yet.
				myLogger.debug(qe.getLocalizedMessage());
				return null;
			}

		} catch (final RuntimeException e) {
			myLogger.error(e.getLocalizedMessage(), e);
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
		// myLogger.debug("Loading user with dn: " + dn + " from db.");
		final String queryString = "from grisu.backend.model.User as user where user.dn = ?";

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
			myLogger.error(e.getLocalizedMessage(), e);
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
		// myLogger.debug("attaching dirty Job instance");

		try {
			getCurrentSession().beginTransaction();

			getCurrentSession().saveOrUpdate(instance);

			getCurrentSession().getTransaction().commit();

			// myLogger.debug("saveOrUpdate successful");
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
	
	
	public List<String> findAllUserDNs(){
		//final String queryString = "select dn from users";
		final String queryString = "select u.dn from users u where exists(select 1 from JobStat j where u.dn=j.dn)";
		try {
			getCurrentSession().beginTransaction();

			final Query queryObject = getCurrentSession().createSQLQuery(queryString);

			try {
				final List<String> userDNs = (queryObject.list());
				getCurrentSession().getTransaction().commit();
				return userDNs;

			} catch (final QueryException qe) {
				// means user not in db yet.
				myLogger.debug(qe.getLocalizedMessage());
				return null;
			}

		} catch (final RuntimeException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
	}

	public static void main(String[] args){
		UserDAO u = new UserDAO();
		System.out.println("start:"+System.currentTimeMillis());
		long start = System.currentTimeMillis();
		List<String> userdns = u.findAllUserDNs();
		//u.findAllUsers();
		System.out.println("end:" + (System.currentTimeMillis()-start));
		
		
	}
	
}
