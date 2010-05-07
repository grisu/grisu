package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.JobListDialog;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class Folderdownload {

	public static void main(String[] args) throws Exception {

		//		ServiceInterface si = LoginManager.loginCommandline();
		ServiceInterface si = LoginManager.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		
		FileManager fm = registry.getFileManager();

		fm.downloadUrl("gsiftp://ng2.vpac.org/home/acc004/grisu-jobs", "/home/markus/Desktop/temp", true);
		
		

		System.out.println("Main thread finished.");
	}

}
