package org.vpac.grisu.frontend.examples;

import java.io.File;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class UploadLocalFolder {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager
				.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		FileManager fm = registry.getFileManager();

		fm.cp(new File("/home/markus/sample input files"),
				"gsiftp://ng2.vpac.org/home/acc004/test/", true);

	}

}
