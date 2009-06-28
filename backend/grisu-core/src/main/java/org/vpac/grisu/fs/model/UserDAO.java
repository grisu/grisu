

package org.vpac.grisu.fs.model;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.vpac.grisu.hibernate.BaseHibernateDAO;

/**
 * This class takes care of storing users and their properties into the a database using hibernate.
 * 
 * @author Markus Binsteiner
 *
 */
public class UserDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(UserDAO.class.getName());

	//TODO improve this (check: http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)
	
//	private static String getDN(User user) {
//		return user.getDn();
//	}
	
	/**
	 * Looks up the database whether a user with the specified dn is already 
	 * persisted
	 * 
	 * @param dn the dn of the user
	 * @return the {@link User} or null if not found
	 */
	public User findUserByDN(String dn){
		myLogger.debug("Loading user with dn: "+dn+" from db.");
		String queryString = "from org.vpac.grisu.fs.model.User as user where user.dn = ?";
		
		try {
		    getCurrentSession().beginTransaction();

		    Query queryObject = getCurrentSession().createQuery(queryString);

		    try {
		    	User user = (User)queryObject.uniqueResult();
				getCurrentSession().getTransaction().commit();
				return user;
		        
		    } catch (QueryException qe) {
		    	// means user not in db yet.
		    	myLogger.debug("User "+dn+" not found in database...");
		    	return null;
		    }
			
		} catch (RuntimeException e) {
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}

	}
	
	
    
	public void delete(User persistentInstance) {
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

    public void saveOrUpdate(User instance) {
        myLogger.debug("attaching dirty Job instance");
        
		try {
		    getCurrentSession().beginTransaction();

		    getCurrentSession().saveOrUpdate(instance);
		    
			getCurrentSession().getTransaction().commit();
			
			myLogger.debug("saveOrUpdate successful");
		} catch (RuntimeException e) {
			 myLogger.error("saveOrUpdate failed", e);
		    getCurrentSession().getTransaction().rollback();
		    throw e; // or display error message
		} finally {
			getCurrentSession().close();
		}
    }
	
}
