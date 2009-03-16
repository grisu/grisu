

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
public class FileTransferDAO extends BaseHibernateDAO {
	
	private static Logger myLogger = Logger.getLogger(FileTransferDAO.class.getName());

	//TODO improve this (check: http://www.hibernate.org/hib_docs/v3/reference/en/html/objectstate.html#objectstate-modifying)

	private static String getDN(FileTransfer ft) {
		return ft.getUser().getDn();
	}
	
    public void save(FileTransfer transientInstance) {
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
    
	public void delete(FileTransfer persistentInstance) {
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
	
    public FileTransfer merge(FileTransfer detachedInstance) {
        myLogger.debug("merging Job instance");
        try {
        	String dn = getDN(detachedInstance);
            FileTransfer result = (FileTransfer) getSession(dn)
                    .merge(detachedInstance);
            myLogger.debug("merge successful");
            getSession(dn).close();
            return result;
        } catch (RuntimeException re) {
            myLogger.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(FileTransfer instance) {
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
    
    public void attachClean(FileTransfer instance) {
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
