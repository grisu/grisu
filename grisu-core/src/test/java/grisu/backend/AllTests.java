package grisu.backend;

import grisu.backend.model.fs.FileSystemManagerTest;
import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileSystemManagerTest.class })
public class AllTests {

	private static ServiceInterface si = null;

	private static FileManager fm = null;
	private static UserEnvironmentManager uem = null;

	private final static String alias = "Local";
	//	private final static String alias = "BeSTGRID";

	public static FileManager getFileManager() {
		if (fm == null) {
			getServiceInterface();
		}
		return fm;
	}

	public synchronized static ServiceInterface getServiceInterface() {

		if (si == null) {
			System.out.println("Logging in...");
			try {
				si = LoginManager.login(alias);
				fm = GrisuRegistryManager.getDefault(si).getFileManager();
				uem = GrisuRegistryManager.getDefault(si)
						.getUserEnvironmentManager();
			} catch (LoginException e) {
				throw new RuntimeException(e);
			}
		}
		return si;
	}

	public static UserEnvironmentManager getUserEnvironmentManager() {
		if (uem == null) {
			getServiceInterface();
		}
		return uem;
	}

	@BeforeClass
	public static void setUp() {
		System.out.println("Setting up test suite...");
		getServiceInterface();
		getFileManager();
		getUserEnvironmentManager();
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("Tearing down test suite...");
		System.out.println("Logging out...");
		getServiceInterface().logout();
	}


}
