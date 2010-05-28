package org.vpac.grisu.frontend.examples;

import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;

public class FileListingTest {

	public static void main(final String[] args) throws Exception {

		Date start = new Date();
		// String username = args[0];
		// char[] password = args[1].toCharArray();

		// LoginParams loginParams = new LoginParams(
		// // "http://localhost:8080/xfire-backend/services/grisu",
		// //
		// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
		// // "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
		// "Local", username, password);

		// final ServiceInterface si = LoginManager.shiblogin("markus", args[0]
		// .toCharArray(), "VPAC", false);

		final ServiceInterface si = LoginManager.loginCommandline();

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		final FileManager fileManager = registry.getFileManager();

		DtoFolder folder = fileManager
				.ls(
						"gsiftp://ng2.vpac.org/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_VPAC_CN_Markus_Binsteiner_qTrDzHY7L1aKo3WSy8623-7bjgM/grid-test-jobs/blast_2.2.21_d4fd6c4c-25e6-4cf0-8898-93cef219e996",
						1);

		for (DtoFile file : folder.getChildrenFiles()) {
			System.out.println("Name: " + file.getName() + ", size: "
					+ file.getSize());
		}

		// DtoFolder folder = si
		// .ls(
		// "gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner",
		// 0);

		// DtoMountPoints mps = si.df();
		// for ( MountPoint mp : mps.getMountpoints() ) {
		// System.out.println(mp.getAlias());
		// }
		// for ( String file : folder.listOfAllFilesUnderThisFolder() ) {
		// System.out.println(file);
		// }

		// final ExecutorService executor1 = Executors.newFixedThreadPool(4);
		//
		// for (final MountPoint mp : si.df().getMountpoints()) {
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// System.out.println("Deleting: " + mp.getRootUrl());
		// try {
		// fileManager.deleteFile(mp.getRootUrl());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// };
		// executor1.execute(thread);
		// }
		//
		// executor1.shutdown();
		//
		// executor1.awaitTermination(3600 * 48, TimeUnit.SECONDS);

		System.out.println("Started: " + start);
		System.out.println("Finished: " + new Date().toString());

	}

}
