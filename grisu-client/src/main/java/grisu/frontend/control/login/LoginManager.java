package grisu.frontend.control.login;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.ServiceInterfaceException;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.GridEnvironment;
import grisu.jcommons.dependencies.ClasspathHacker;
import grisu.jcommons.view.cli.CliHelpers;
import grisu.model.GrisuRegistryManager;
import grisu.settings.ClientPropertiesManager;
import grisu.settings.Environment;
import grisu.utils.GrisuPluginFilenameFilter;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;
import grith.jgrith.cred.ProxyCred;
import grith.jgrith.cred.callbacks.CliCallback;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Date;

public class LoginManager {

	public static int REQUIRED_BACKEND_API_VERSION = 18;
	public static final String DEFAULT_BACKEND = "default";
	public static final int DEFAULT_PROXY_LIFETIME_IN_HOURS = 240;

	static final Logger myLogger = LoggerFactory.getLogger(LoginManager.class
			.getName());

	public static final ImmutableBiMap<String, String> SERVICEALIASES = new ImmutableBiMap.Builder<String, String>()
			.put("local", "Local")
			.put("testbed",
					"https://compute.test.nesi.org.nz/soap/GrisuService")
			.put("bestgrid",
					"https://compute.services.bestgrid.org/soap/GrisuService")
			.put("nesi", "https://compute.nesi.org.nz/soap/GrisuService")
			.put("dev",
					"https://compute.dev.nesi.org.nz/soap/GrisuService")
			.put("bestgrid-test",
					"https://compute-test.services.bestgrid.org/soap/GrisuService")
			.put("local_ws_jetty", "http://localhost:8080/soap/GrisuService")
			.put("local_ws", "http://localhost:8080/grisu-ws/soap/GrisuService")
			.build();

	public static final ImmutableMap<String, String> MYPROXY_SERVERS = new ImmutableMap.Builder<String, String>()
			.put("Local",
					GridEnvironment.getDefaultMyProxyServer() + ":"
							+ GridEnvironment.getDefaultMyProxyPort())
			.put("https://compute.test.nesi.org.nz/soap/GrisuService",
					"myproxy.test.nesi.org.nz:7512")
			.put("https://compute.nesi.org.nz/soap/GrisuService",
					"myproxy.nesi.org.nz:7512")
			.put("https://compute.services.bestgrid.org/soap/GrisuService",
					"myproxy.nesi.org.nz:7512")
			.put("https://compute-dev.services.bestgrid.org/soap/GrisuService",
					"myproxy.nesi.org.nz:7512")
			.put("https://compute-test.services.bestgrid.org/soap/GrisuService",
					"myproxy.nesi.org.nz:7512")
			.put("http://localhost:8080/grisu-ws/soap/GrisuService",
					GridEnvironment.getDefaultMyProxyServer() + ":"
							+ GridEnvironment.getDefaultMyProxyPort())
			.put("http://localhost:8080/soap/GrisuService",
					GridEnvironment.getDefaultMyProxyServer() + ":"
							+ GridEnvironment.getDefaultMyProxyPort()).build();

	private static String CLIENT_NAME = setClientName(null);
	private static String CLIENT_VERSION = setClientVersion(null);
	public static String USER_SESSION = setUserSessionId(null);

	public static void addPluginsToClasspath() throws IOException {

		ClasspathHacker.initFolder(Environment.getGrisuPluginDirectory(),
				new GrisuPluginFilenameFilter());

	}

	public static String getClientName() {
		return CLIENT_NAME;
	}

	public static String getClientVersion() {
		return CLIENT_VERSION;
	}

	public static String getLoginUrl(String alias) {


        if ( ClientPropertiesManager.DEFAULT_DEPLOYMENT_BACKEND.equalsIgnoreCase(alias) ) {
            alias = ClientPropertiesManager.getDefaultBackend();
        }

        String url = null;
        if ( ClientPropertiesManager.DEFAULT_SERVICE_INTERFACE.equals(alias) ) {
            url = SERVICEALIASES.get(ClientPropertiesManager.DEFAULT_DEPLOYMENT_BACKEND.toLowerCase());
        } else {
            url = SERVICEALIASES.get(alias.toLowerCase());
        }

		if (StringUtils.isNotBlank(url)) {
			return url;
		} else {
			return alias;
		}

	}

	public static synchronized void initGrisuClient(String clientname) {

		Thread.currentThread().setName("main");

		setClientName(clientname);

		setClientVersion(grisu.jcommons.utils.Version.get(clientname));

		grith.jgrith.Environment.initEnvironment();

	}

	public static ServiceInterface login(String backend) throws NoCredentialException, LoginException {
		return login(backend, false);
	}

	public static ServiceInterface login(String backend,
			boolean displayCliProgress) throws NoCredentialException,
			LoginException {

		grith.jgrith.Environment.initEnvironment();

		myLogger.debug("x509 proxy path: " + System.getenv("X509_USER_PROXY"));

		Cred c = null;
		try {
			c = new ProxyCred();
		} catch (Exception e) {
			throw new NoCredentialException();
		}

		if (!c.isValid()) {
			throw new NoCredentialException();
		}

		return login(backend, c, displayCliProgress);

	}

	public static ServiceInterface login(String backend, Cred cred,
			boolean displayCliProgress) throws LoginException {

		if (StringUtils.isBlank(backend)) {
			String defaultUrl = ClientPropertiesManager
					.getDefaultServiceInterfaceUrl();

			if (StringUtils.isBlank(defaultUrl)) {
				defaultUrl = DEFAULT_BACKEND;
			}

			backend = defaultUrl;
		}

		ClientPropertiesManager.setDefaultServiceInterfaceUrl(backend);

		backend = getLoginUrl(backend);
		try {
			if (displayCliProgress) {
				CliHelpers.setIndeterminateProgress(
						"Setting up environment...", true);
			}
			grith.jgrith.Environment.initEnvironment();

			try {
				addPluginsToClasspath();
			} catch (final IOException e2) {
				// TODO Auto-generated catch block
				myLogger.warn(e2.getLocalizedMessage(), e2);
				throw new RuntimeException(e2);
			}

			ServiceInterface si;
			if (displayCliProgress) {
				CliHelpers.setIndeterminateProgress("Logging in to backend...",
						true);
			}
			try {

				si = ServiceInterfaceFactory.createInterface(backend, cred,
						null);
				// loginParams.getHttpProxy(),
				// loginParams.getHttpProxyPort(),
				// loginParams.getHttpProxyUsername(),
				// loginParams.getHttpProxyPassphrase());
			} catch (ServiceInterfaceException e) {
				throw new LoginException("Could not login to backend.", e);
			} catch (Throwable t) {
				t.printStackTrace();
				throw new LoginException("Error while loggin in: "
						+ t.getLocalizedMessage());
			}

			GrisuRegistryManager.registerServiceInterface(si, cred);
			GrisuRegistryManager.getDefault(si).set(Constants.BACKEND, backend);

			return si;
		} finally {
			if (displayCliProgress) {
				CliHelpers.setIndeterminateProgress(false);

			}
		}

	}

	public static ServiceInterface loginCommandline(String backend)
			throws LoginException {

		grith.jgrith.Environment.initEnvironment();

		AbstractCred c = AbstractCred.loadFromConfig(null, new CliCallback());

		return login(backend, c, true);
	}

	public static String setClientName(String name) {

		if (StringUtils.isBlank(name)) {
			name = "n/a";
		}
		CLIENT_NAME = name;
		MDC.put("client", name);

		return name;

	}

	public static String setClientVersion(String version) {

		if (StringUtils.isBlank(version)) {
			version = "n/a";
		}
		CLIENT_VERSION = version;
		MDC.put("client_version", version);

		return version;
	}

	public static String setUserSessionId(String id) {
		if (StringUtils.isBlank(id)) {
			id = System.getProperty("user.name") + "_" + new Date().getTime();
		}
		USER_SESSION = id;
		MDC.put("session", id);

		return id;
	}

}
