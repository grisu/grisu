

package org.vpac.grisu.fs.model;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.vpac.grisu.hibernate.BaseHibernateDAO;

/**
 * Not used yet.
 * 
 * @author Markus Binsteiner
 *
 */
public class FileReservationDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(FileReservationDAO.class.getName());

	//TODO improve this (check: http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)
	
	private static String getDN(FileReservation fr) {
		return fr.getUser().getDn();
	}
	
    public void save(FileReservation transientInstance) {
        myLogger.debug("saving Job instance");
        try {
        	String dn = getDN(transientInstance);
            getSession(dn).save(transientInstance);
            getSession(dn).close();
            myLogger.debug("save successful");
        } catch (RuntimeException re) {
            myLogger.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(FileReservation persistentInstance) {
        myLogger.debug("deleting Job instance");
        try {
        	String dn = getDN(persistentInstance);
            getSession(dn).delete(persistentInstance);
            getSession(dn).close();
            myLogger.debug("delete successful");
        } catch (RuntimeException re) {
            myLogger.error("delete failed", re);
            throw re;
        }
    }
	
    public FileReservation merge(FileReservation detachedInstance) {
        myLogger.debug("merging Job instance");
        try {
        	String dn = getDN(detachedInstance);
            FileReservation result = (FileReservation) getSession(dn)
                    .merge(detachedInstance);
            getSession(dn).close();
            myLogger.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            myLogger.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(FileReservation instance) {
        myLogger.debug("attaching dirty Job instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).saveOrUpdate(instance);
            getSession(dn).close();
            myLogger.debug("attach successful");
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(FileReservation instance) {
        myLogger.debug("attaching clean Job instance");
        try {
        	String dn = getDN(instance);
            getSession(dn).lock(instance, LockMode.NONE);
            getSession(dn).close();
            myLogger.debug("attach successful");
        } catch (RuntimeException re) {
            myLogger.error("attach failed", re);
            throw re;
        }
    }
	
}
