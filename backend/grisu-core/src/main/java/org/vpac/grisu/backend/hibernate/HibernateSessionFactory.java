package org.vpac.grisu.backend.hibernate;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.vpac.grisu.backend.model.User;
import org.vpac.grisu.backend.model.job.Job;
import org.vpac.grisu.backend.model.job.BatchJob;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.settings.ServerPropertiesManager;

public final class HibernateSessionFactory {

	private HibernateSessionFactory() {
	}

	public static final String MYSQL_DBTYPE = "mysql";
	public static final String HSQLDB_DBTYPE = "hsqldb";
	public static final String DERBY_DBTYPE = "derby";
	
	public static String usedDatabase = "unknown";
	
	static final Logger myLogger = Logger
			.getLogger(HibernateSessionFactory.class.getName());

	private static SessionFactory sessionFactory;

	private static String CUSTOM_HIBERNATE_CONFIG_FILE = null;

	public static SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
			initialize();
		}
		return sessionFactory;
	}

	public static void setCustomHibernateConfigFile(
			String pathToHibernateConfigFile) {
		if (sessionFactory != null) {
			throw new RuntimeException(
					"Sessionfactory already initialized. No use setting the hibernate config file anymore...");
		}
		CUSTOM_HIBERNATE_CONFIG_FILE = pathToHibernateConfigFile;
	}

	private static void initialize() {
		try {

			AnnotationConfiguration configuration = null;
			if (StringUtils.isNotBlank(CUSTOM_HIBERNATE_CONFIG_FILE)) {
				File grisuHibernateConfigFile = new File(
						CUSTOM_HIBERNATE_CONFIG_FILE);

				if (grisuHibernateConfigFile.exists()) {
					myLogger
							.debug("Found grisu-hibernate.cfg.xml file in .grisu directory. Using this to configure db connection.");
					// use the user-provided config file
					configuration = new AnnotationConfiguration()
							.configure(grisuHibernateConfigFile);
					if (StringUtils.isBlank(configuration
							.getProperty("hibernate.connection.url"))) {
						// setting default path to hsqldb if necessary
						String url = "jdbc:hsqldb:file:"
								+ Environment.getGrisuDirectory().getPath()
								+ File.separator + "grisulocaldb";
						configuration.setProperty("hibernate.connection.url",
								url);
					}
				} else {
					throw new RuntimeException(
							"Could not find hibernate config file: "
									+ CUSTOM_HIBERNATE_CONFIG_FILE);
				}

			} else {
				File grisuJobDir = Environment.getGrisuDirectory();
				File grisuHibernateConfigFile = new File(grisuJobDir,
						"grisu-hibernate.cfg.xml");

				if (grisuHibernateConfigFile.exists()) {
					myLogger
							.debug("Found grisu-hibernate.cfg.xml file in .grisu directory. Using this to configure db connection.");
					// use the user-provided config file
					configuration = new AnnotationConfiguration()
							.configure(grisuHibernateConfigFile);
				} else if (!ServerPropertiesManager.useDefaultDatabase()) {
					// check whether something is specified in the
					// grisu-server.config file
					String dbType = ServerPropertiesManager.getDatabaseType();
					if (MYSQL_DBTYPE.equals(dbType)) {
						usedDatabase = MYSQL_DBTYPE;
						configuration = new AnnotationConfiguration()
								.configure("/grisu-hibernate-default-mysql.cfg.xml");

						String url = ServerPropertiesManager
								.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager
								.getDatabaseUsername();
						String password = ServerPropertiesManager
								.getDatabasePassword();

						if (url == null || url.length() == 0) {
							throw new RuntimeException(
									"databaseConnectionUrl not specified in grisu-server.config file. Can't continue...");
						}
						if (username == null || username.length() == 0) {
							throw new RuntimeException(
									"databaseUsername not specified in grisu-server.config file. Can't continue...");
						}
						if (password == null || password.length() == 0) {
							throw new RuntimeException(
									"databasePassword not specified in grisu-server.config file. Can't continue...");
						}

						configuration.setProperty("hibernate.connection.url",
								url);
						configuration.setProperty(
								"hibernate.connection.username", username);
						configuration.setProperty(
								"hibernate.connection.password", password);

					} else if (HSQLDB_DBTYPE.equals(dbType)) {
						usedDatabase = HSQLDB_DBTYPE;
						configuration = new AnnotationConfiguration()
								.configure("/grisu-hibernate-default-hsqldb.cfg.xml");

						String url = ServerPropertiesManager
								.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager
								.getDatabaseUsername();
						String password = ServerPropertiesManager
								.getDatabasePassword();

						if (url == null || url.length() == 0) {
							url = "jdbc:hsqldb:file:"
									+ Environment.getGrisuDirectory().getPath()
									+ File.separator + "grisulocaldb";
						}
						if (username == null || username.length() == 0) {
							username = "sa";
						}
						if (password == null) {
							password = "";
						}

						configuration.setProperty("hibernate.connection.url",
								url);
						configuration.setProperty(
								"hibernate.connection.username", username);
						configuration.setProperty(
								"hibernate.connection.password", password);

					} else if (DERBY_DBTYPE.equals(dbType)) {
						
						usedDatabase = DERBY_DBTYPE;
						
						configuration = new AnnotationConfiguration()
								.configure("/grisu-hibernate-default-derby.cfg.xml");

						String url = ServerPropertiesManager
								.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager
								.getDatabaseUsername();
						String password = ServerPropertiesManager
								.getDatabasePassword();

						if (url == null || url.length() == 0) {
							url = "jdbc:derby:"
									+ Environment.getGrisuDirectory().getPath()
									+ File.separator + "grisulocaldb_derby;create=true";
						}
						if (username == null || username.length() == 0) {
							username = "sa";
						}
						if (password == null) {
							password = "";
						}

						configuration.setProperty("hibernate.connection.url",
								url);
						configuration.setProperty(
								"hibernate.connection.username", username);
						configuration.setProperty(
								"hibernate.connection.password", password);
					} else {
						throw new RuntimeException(
								"DatabaseType \""
										+ dbType
										+ "\" in grisu-server.config file not recognized. Can't continue. Use either \"hsqldb\" or \"mysql\".");
					}
				} else {
					// use default hsqld database
					configuration = new AnnotationConfiguration()
							.configure("/grisu-hibernate-default-derby.cfg.xml");
					String url = "jdbc:derby:"
						+ Environment.getGrisuDirectory().getPath()
						+ File.separator + "grisulocaldb_derby;create=true";
					configuration.setProperty("hibernate.connection.url", url);
					configuration.setProperty("hibernate.connection.username",
							"sa");
					configuration.setProperty("hibernate.connection.password",
							"");
				}
			}

			configuration.addAnnotatedClass(User.class);
			configuration.addAnnotatedClass(Job.class);
			configuration.addAnnotatedClass(BatchJob.class);
			configuration.addAnnotatedClass(MountPoint.class);
			configuration.addAnnotatedClass(JobSubmissionObjectImpl.class);
//			configuration.addAnnotatedClass(DtoActionStatus.class);
			
			sessionFactory = configuration.buildSessionFactory();
		} catch (Throwable e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
