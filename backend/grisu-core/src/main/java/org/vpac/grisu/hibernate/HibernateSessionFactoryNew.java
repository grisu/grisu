package org.vpac.grisu.hibernate;

import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.vpac.grisu.fs.model.User;
import org.vpac.grisu.js.model.Job;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.settings.ServerPropertiesManager;

public class HibernateSessionFactoryNew {

	static final Logger myLogger = Logger
			.getLogger(HibernateSessionFactoryNew.class.getName());

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	static {
			try {
				
				File grisuJobDir = Environment.getGrisuDirectory();
				File grisuHibernateConfigFile = new File(grisuJobDir, "grisu-hibernate.cfg.xml");
				
				AnnotationConfiguration configuration = null;
				if ( grisuHibernateConfigFile.exists() ) {
					myLogger.debug("Found grisu-hibernate.cfg.xml file in .grisu directory. Using this to configure db connection.");
					// use the user-provided config file
					configuration = new AnnotationConfiguration().configure(grisuHibernateConfigFile);
				} else if ( ! ServerPropertiesManager.useDefaultDatabase() ) {
					// check whether something is specified in the grisu-server.config file
					String dbType = ServerPropertiesManager.getDatabaseType();
					if ( "mysql".equals(dbType) ) {
						configuration = new AnnotationConfiguration().configure("/grisu-hibernate-default-mysql.cfg.xml");
						
						String url = ServerPropertiesManager.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager.getDatabaseUsername();
						String password = ServerPropertiesManager.getDatabasePassword();
						
						if ( url == null || url.length() == 0 ) {
							throw new RuntimeException("databaseConnectionUrl not specified in grisu-server.config file. Can't continue...");
						}
						if ( username == null || username.length() == 0 ) {
							throw new RuntimeException("databaseUsername not specified in grisu-server.config file. Can't continue...");
						}
						if ( password == null || password.length() == 0 ) {
							throw new RuntimeException("databasePassword not specified in grisu-server.config file. Can't continue...");
						}
						
						configuration.setProperty("hibernate.connection.url", url);
						configuration.setProperty("hibernate.connection.username", username);
						configuration.setProperty("hibernate.connection.password", password);
						
					} else if ( "hsqldb".equals(dbType) ) {
						configuration = new AnnotationConfiguration().configure("/grisu-hibernate-default-hsqldb.cfg.xml");
						
						String url = ServerPropertiesManager.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager.getDatabaseUsername();
						String password = ServerPropertiesManager.getDatabasePassword();
						
						if ( url == null || url.length() == 0 ) {
							url = "jdbc:hsqldb:file:" + Environment.GRISU_DIRECTORY + File.separator + "grisulocaldb";
						} 
						if ( username == null || username.length() == 0 ) {
							username = "sa";
						}
						if ( password == null ) {
							password = "";
						}
						
						configuration.setProperty("hibernate.connection.url", url);
						configuration.setProperty("hibernate.connection.username", username);
						configuration.setProperty("hibernate.connection.password", password);
						
					} else {
						throw new RuntimeException("DatabaseType \""+dbType+"\" in grisu-server.config file not recognized. Can't continue. Use either \"hsqldb\" or \"mysql\".");
					}
				} else {
					// use default hsqld database
					configuration = new AnnotationConfiguration().configure("/grisu-hibernate-default-hsqldb.cfg.xml"); 
					String url = "jdbc:hsqldb:file:" + Environment.GRISU_DIRECTORY + File.separator + "grisulocaldb";
					configuration.setProperty("hibernate.connection.url", url);
					configuration.setProperty("hibernate.connection.username", "sa");
					configuration.setProperty("hibernate.connection.password", "");
				}
				
				configuration.addAnnotatedClass(User.class);
				configuration.addAnnotatedClass(Job.class);
				configuration.addAnnotatedClass(MountPoint.class);
				configuration.addAnnotatedClass(JobSubmissionObjectImpl.class);
				
				sessionFactory = configuration.buildSessionFactory();
			} catch (Exception e) {
				System.err.println("%%%% Error Creating SessionFactory %%%%");
				e.printStackTrace();
			}
		}
}
