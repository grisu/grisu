package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.MountPoint;
import grisu.model.dto.DtoMountPoints;
import grisu.model.dto.GridFile;

public class LsDir {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.loginCommandline("nesi");
		
		FileManager fm = GrisuRegistryManager.getFileManager(si);
		
		GridFile f = fm.ls("gsiftp://pan.nesi.org.nz/home/mbin029");
		
		DtoMountPoints mps = si.df();
		
		for ( MountPoint mp : mps.getMountpoints() ) {
			mp.isVolatileFileSystem();
		}
				
		

	}

}
