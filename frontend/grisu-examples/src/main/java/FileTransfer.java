import java.io.File;
import java.net.URI;
import java.util.Date;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;


public class FileTransfer {
		
	public static void main(String[] args) throws Exception {
		
		EnvironmentManager em = Login.loginDevelopmentServer(args[0], args[1].toCharArray());
		System.out.println("Logged in.");

		Date start = new Date();
		GrisuFileObject source = em.getFileManager().getFileObject(new File("/home/markus/test.txt").toURI());
		GrisuFileObject target = em.getFileManager().getFileObject(new URI(args[2]));
		System.out.println("Starting transfer.");
		em.getFileTransferManager().addTransfer(new GrisuFileObject[]{source}, target, org.vpac.grisu.client.control.files.FileTransfer.OVERWRITE_EVERYTHING, true);
//		FileManagerTransferHelpers.transferFiles(em.getServiceInterface(), new BackendFileObject[]{source}, target, false);
		System.out.println("Transfer finished.");
		Date end = new Date();

		
		System.out.println("Transfer time: "+(end.getTime()-start.getTime()));

	}

}
