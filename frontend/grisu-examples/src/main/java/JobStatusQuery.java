import java.io.File;
import java.net.URI;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.control.FileHelpers;


public class JobStatusQuery {
		
	public static void main(String[] args) throws Exception {
		
		EnvironmentManager em = Login.login(args[0], args[1].toCharArray());
		
		String jobname = args[2];
		GrisuJobMonitoringObject job = em.getJobManager().getJob(jobname);
		
		System.out.println("Status of job "+jobname+" is: "+job.getStatus());
		
		// now let's see which files are in the job directory
		System.out.println("Job directory files:");
		GrisuFileObject jobDirectory = job.getJobDirectoryObject();
		for ( GrisuFileObject file : jobDirectory.getChildren() ) {
			System.out.println(file.getName()+" ("+file.getSize(false)+")");
		}
		

		// another way to get the stdout file:
		GrisuFileObject stdout = em.getFileManager().getFileObject(new URI(job.getStdout()));
		File cachedLocalFile = stdout.getLocalRepresentation(false);
		
		String stdoutText = FileHelpers.readFromFile(cachedLocalFile);
		System.out.println("Content of stdout:\n"+stdoutText);
		
		// now we could check whether the job is still running and if so, we'll kill it
		// and delete the job directory on the server
//		int status = job.getStatusAsInt();
//		
//		if ( status < JobConstants.FINISHED_EITHER_WAY ) {
//			job.killAndClean();
//		}
	}

}
