/* Copyright 2007, 2008 ARCS
 *
 * This file is part of Grisu.
 * Grisu is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.

 * Grisu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Grisu; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.vpac.grisu.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

/**
 * Configures and provides access to Hibernate sessions, tied to the
 * current thread of execution.  Follows the Thread Local Session
 * pattern, see {@link http://hibernate.org/42.html }.
 */
public class HibernateSessionFactory {
	
	static final Logger myLogger = Logger
	.getLogger(HibernateSessionFactory.class.getName());
	
	private static Map<String, Session> sessions = new HashMap<String, Session>();
	

    /**
     * Location of hibernate.cfg.xml file.
     * Location should be on the classpath as Hibernate uses 
     * #resourceAsStream style lookup for its configuration file.
     * The default classpath location of the hibernate config file is
     * in the default package. Use #setConfigFile() to update
     * the location of the configuration file for the current session.   
     */
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
   private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
    private  static Configuration configuration = new Configuration();
    private static org.hibernate.SessionFactory sessionFactory;
    private static String configFile = CONFIG_FILE_LOCATION;

   static {
	   rebuildSessionFactory();
    }
    private HibernateSessionFactory() {
    }
    
    /**
     * Gets a session for a user. Every dn get's its own session. Sessions are managed in a static map
     * using the dn as key and the session as value.
     * @param dn the dn of the user
     * @return the hibernate session of the user
     * @throws HibernateException if something is wrong with hibernate/the hibernate config
     */
    public static Session getSession(String dn) throws HibernateException {
    	
    	Session session = sessions.get(dn);
    	
    	if ( session == null || !session.isOpen() ) {
    		if ( sessionFactory == null ) {
    			rebuildSessionFactory();
    		}
    		session = (sessionFactory != null) ? sessionFactory.openSession() : null;
    		
    		if ( session != null )
    			sessions.put(dn, session);
    	}
    	return session;
    }
   
//   /**
//     * Returns the ThreadLocal Session instance.  Lazy initialize
//     * the <code>SessionFactory</code> if needed.
//     *
//     *  @return Session
//     *  @throws HibernateException
//     */
//    public static Session getSession() throws HibernateException {
//        Session session = (Session) threadLocal.get();
//
//      if (session == null || !session.isOpen()) {
//         if (sessionFactory == null) {
//            rebuildSessionFactory();
//         }
//         session = (sessionFactory != null) ? sessionFactory.openSession()
//               : null;
//         threadLocal.set(session);
//      }
//
//        return session;
//    }

   /**
     *  Rebuild hibernate session factory
     *
     */
   public static void rebuildSessionFactory() {
      try {
         configuration.configure(configFile);
         myLogger.debug("Hibernate configuration read.");
         if (configuration.getProperty("dialect").equals("org.hibernate.dialect.HSQLDialect")) {
        	 configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:" + System.getProperty("user.home") + "/.grisu/grisulocaldb");
        	 myLogger.debug("hsql dialect found. Changed settings to use database file "+System.getProperty("user.home")+"/.grisu/grisulocaldb");
         }
         sessionFactory = configuration.buildSessionFactory();
      } catch (Exception e) {
         System.err
               .println("%%%% Error Creating SessionFactory %%%%");
         e.printStackTrace();
      }
   }

   /**
     *  Close the single hibernate session instance.
     *
     *  @throws HibernateException
     */
    public static void closeSession() throws HibernateException {
        Session session = (Session) threadLocal.get();
        threadLocal.set(null);

        if (session != null) {

            session.close();
        }
    }

   /**
     *  return session factory
     *
     */
   public static org.hibernate.SessionFactory getSessionFactory() {
      return sessionFactory;
   }

   /**
     *  return session factory
     *
     *   session factory will be rebuilded in the next call
     */
   public static void setConfigFile(String configFile) {
      HibernateSessionFactory.configFile = configFile;
      sessionFactory = null;
   }

   /**
     *  return hibernate configuration
     *
     */
   public static Configuration getConfiguration() {
      return configuration;
   }
}
