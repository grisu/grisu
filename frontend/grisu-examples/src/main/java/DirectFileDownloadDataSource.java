import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import javax.activation.DataSource;

import org.vpac.grisu.control.ServiceInterface;


public class DirectFileDownloadDataSource {
		
	
	
	/**
	 * arg[0] : myproxy username
	 * arg[1] : myproxy password
	 * arg[2] : source file url (gridftp://ng2.vpac.org/home/markus/test.txt)
	 * arg[3] : target directory path (/home/markus)
	 */
	public static void main(String[] args) throws Exception {
		
		
		ServiceInterface serviceInterface = Login.getServiceInterfaceForProductionServer(args[0], args[1].toCharArray());
		System.out.println("Logged in.");

		Date start = new Date();
		System.out.println("Starting transfer.");
		
		DataSource target = serviceInterface.download(args[2]);
		
		String filename = args[2].substring(args[2].lastIndexOf("/") + 1);
		
		File newFile = new File(args[3], filename);
		
		FileOutputStream outputStream = new FileOutputStream(newFile);
		InputStream inputStream = target.getInputStream();

		try {
			byte[] buffer = new byte[4096];
			for (int n; (n = inputStream.read(buffer)) != -1;)
				outputStream.write(buffer, 0, n);
		} finally {
			outputStream.close();
			inputStream.close();
		}

		System.out.println("Transfer finished.");
		Date end = new Date();

		
		System.out.println("Transfer time: "+(end.getTime()-start.getTime()));		
		

	}

}
