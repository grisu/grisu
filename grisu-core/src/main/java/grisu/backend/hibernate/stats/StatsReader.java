package grisu.backend.hibernate.stats;

import grisu.backend.hibernate.JobStatDAO;
import grisu.backend.model.job.JobStat;

import java.util.List;

public class StatsReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JobStatDAO jobstatdao = new JobStatDAO();
		
		List<JobStat> jobs = jobstatdao.findAllJobs();
		
		for ( JobStat j : jobs ) {
			System.out.println(j.getJobname()+" : "+j.isActive());
			System.out.println("PROPERTIES");
			for ( String key : j.getProperties().keySet() ) {
				System.out.println("\t"+key+": "+j.getProperties().get(key));
			}
			System.out.println("MESSAGES");
			for ( String msg : j.getLogMessages().values() ) {
				System.out.println("\t"+msg);
			}
			
			System.out.println("\t"+j.getSubmittedJobDescription());
			System.out.println("\t"+j.getJsdl());
		}
		
		
	}

}
