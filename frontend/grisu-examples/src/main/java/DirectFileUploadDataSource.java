import java.io.File;
import java.util.Date;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.vpac.grisu.control.ServiceInterface;


public class DirectFileUploadDataSource {
		
	public static void main(String[] args) throws Exception {
		
		
		ServiceInterface serviceInterface = Login.getServiceInterfaceForDevelopmentServer(args[0], args[1].toCharArray());
		System.out.println("Logged in.");

		Date start = new Date();
		DataSource source = new FileDataSource(new File("/home/markus/test.txt"));

		System.out.println("Starting transfer.");
		
		String url = serviceInterface.upload(source, args[2], true);

		System.out.println("Transfer finished.");
		Date end = new Date();

		
		System.out.println("Transfer time: "+(end.getTime()-start.getTime()));		
		

	}

}
