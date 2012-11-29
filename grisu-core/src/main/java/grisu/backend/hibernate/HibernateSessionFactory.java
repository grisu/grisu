package grisu.backend.hibernate;

import grisu.backend.model.User;
import grisu.backend.model.job.BatchJob;
import grisu.backend.model.job.Job;
import grisu.backend.model.job.JobStat;
import grisu.model.MountPoint;
import grisu.model.job.JobSubmissionObjectImpl;
import grisu.settings.Environment;
import grisu.settings.ServerPropertiesManager;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.derby.drda.NetworkServerControl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateSessionFactory {

	public static final String MYSQL_DBTYPE = "mysql";

	public static final String HSQLDB_DBTYPE = "hsqldb";
	public static final String DERBY_DBTYPE = "derby";
	public static String usedDatabase = "unknown";

	static final Logger myLogger = LoggerFactory
			.getLogger(HibernateSessionFactory.class.getName());

	private static boolean startedDerbyNetworkServer = false;
	private static NetworkServerControl server = null;

	private static SessionFactory sessionFactory;

	private static String CUSTOM_HIBERNATE_CONFIG_FILE = null;

	public synchronized static boolean derbyNetworkServerUp() {
		try {
			getNetworkServer().ping();
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public synchronized static void ensureDerbyServerIsUp() {

		if (usingDerbyButNotStartedDerbyServer()) {
			if (!derbyNetworkServerUp()) {
				tryToStartDerbyServer();
			}
		}
	}

	public static NetworkServerControl getNetworkServer() {
		if (server == null) {
			try {
				server = new NetworkServerControl(
						InetAddress.getByName("localhost"), 1527);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		return server;
	}

	public static synchronized SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
			initialize();
		}

		return sessionFactory;
	}

	private static synchronized void initialize() {
		try {

			AnnotationConfiguration configuration = null;
			if (StringUtils.isNotBlank(CUSTOM_HIBERNATE_CONFIG_FILE)) {
				final File grisuHibernateConfigFile = new File(
						CUSTOM_HIBERNATE_CONFIG_FILE);

				if (grisuHibernateConfigFile.exists()) {
					myLogger.debug("Found grisu-hibernate.cfg.xml file in .grisu directory. Using this to configure db connection.");
					// use the user-provided config file
					configuration = new AnnotationConfiguration()
					.configure(grisuHibernateConfigFile);
					if (StringUtils.isBlank(configuration
							.getProperty("hibernate.connection.url"))) {
						// setting default path to hsqldb if necessary
						final String url = "jdbc:hsqldb:file:"
								+ Environment.getVarGrisuDirectory().getPath()
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
				final File grisuJobDir = Environment.getGrisuDirectory();
				final File grisuHibernateConfigFile = new File(grisuJobDir,
						"grisu-hibernate.cfg.xml");

				if (grisuHibernateConfigFile.exists()) {
					myLogger.debug("Found grisu-hibernate.cfg.xml file in .grisu directory. Using this to configure db connection.");
					// use the user-provided config file
					configuration = new AnnotationConfiguration()
					.configure(grisuHibernateConfigFile);
				} else if (!ServerPropertiesManager.useDefaultDatabase()) {
					// check whether something is specified in the
					// grisu-backend.config file
					final String dbType = ServerPropertiesManager
							.getDatabaseType();
					if (MYSQL_DBTYPE.equals(dbType)) {
						usedDatabase = MYSQL_DBTYPE;
						configuration = new AnnotationConfiguration()
						.configure("/grisu-hibernate-default-mysql.cfg.xml");

						final String url = ServerPropertiesManager
								.getDatabaseConnectionUrl();
						final String username = ServerPropertiesManager
								.getDatabaseUsername();
						final String password = ServerPropertiesManager
								.getDatabasePassword();

						if ((url == null) || (url.length() == 0)) {
							throw new RuntimeException(
									"databaseConnectionUrl not specified in grisu-backend.config file. Can't continue...");
						}
						if ((username == null) || (username.length() == 0)) {
							throw new RuntimeException(
									"databaseUsername not specified in grisu-backend.config file. Can't continue...");
						}
						if ((password == null) || (password.length() == 0)) {
							throw new RuntimeException(
									"databasePassword not specified in grisu-backend.config file. Can't continue...");
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

						if ((url == null) || (url.length() == 0)) {
							url = "jdbc:hsqldb:file:"
									+ Environment.getVarGrisuDirectory()
									.getPath() + File.separator
									+ "grisulocaldb";
						}
						if ((username == null) || (username.length() == 0)) {
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

						// use default derby database
						System.setProperty("derby.system.home", Environment
								.getVarGrisuDirectory().getPath()
								+ File.separator + "derby");

						tryToStartDerbyServer();

						configuration = new AnnotationConfiguration()
						.configure("/grisu-hibernate-default-derby.cfg.xml");

						String url = ServerPropertiesManager
								.getDatabaseConnectionUrl();
						String username = ServerPropertiesManager
								.getDatabaseUsername();
						String password = ServerPropertiesManager
								.getDatabasePassword();

						if ((url == null) || (url.length() == 0)) {
							url = "jdbc:derby://localhost:1527/grisu;create=true";

						}
						if ((username == null) || (username.length() == 0)) {
							username = "grisu";
						}
						if (password == null) {
							password = "password";
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
										+ "\" in grisu-backend.config file not recognized. Can't continue. Use either \"hsqldb\" or \"mysql\".");
					}
				} else {
					// use default derby database
					String derby_home = Environment.getVarGrisuDirectory()
							.getPath() + File.separator + "derby";
					System.setProperty("derby.system.home", derby_home);

					usedDatabase = DERBY_DBTYPE;

					tryToStartDerbyServer();

					configuration = new AnnotationConfiguration()
					.configure("/grisu-hibernate-default-derby.cfg.xml");
					// String url = "jdbc:derby:"
					// + Environment.getGrisuDirectory().getPath()
					// + File.separator + "grisulocaldb_derby;create=true";
					final String url = "jdbc:derby://localhost:1527/grisu;create=true";
					configuration.setProperty("hibernate.connection.url", url);
					configuration.setProperty("hibernate.connection.username",
							"grisu");
					configuration.setProperty("hibernate.connection.password",
							"password");
				}
			}

			configuration.addAnnotatedClass(User.class);
			configuration.addAnnotatedClass(Job.class);
			configuration.addAnnotatedClass(JobStat.class);
			configuration.addAnnotatedClass(BatchJob.class);
			configuration.addAnnotatedClass(MountPoint.class);
			configuration.addAnnotatedClass(JobSubmissionObjectImpl.class);
			// configuration.addAnnotatedClass(DtoActionStatus.class);

			sessionFactory = configuration.buildSessionFactory();
		} catch (final Throwable e) {
			myLogger.error(e.getLocalizedMessage(), e);
			// System.err.println("%%%% Error Creating SessionFactory %%%%");
			// e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void setCustomHibernateConfigFile(
			String pathToHibernateConfigFile) {
		if (sessionFactory != null) {
			throw new RuntimeException(
					"Sessionfactory already initialized. No use setting the hibernate config file anymore...");
		}
		CUSTOM_HIBERNATE_CONFIG_FILE = pathToHibernateConfigFile;
	}

	public static void tryToStartDerbyServer() {

		try {

			try {
				getNetworkServer().ping();
			} catch (final Exception e) {
				server.start(null);
				startedDerbyNetworkServer = true;
				sessionFactory = null;
			}
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}
	}

	public static boolean usingDerbyButNotStartedDerbyServer() {
		if (DERBY_DBTYPE.equals(usedDatabase)) {
			return !startedDerbyNetworkServer;
		} else {
			return false;
		}
	}

	private HibernateSessionFactory() {
	}
}
