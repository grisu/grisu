package org.vpac.grisu.js.control;

import java.util.Iterator;
import java.util.List;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobCreationException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.js.model.Job;
import org.vpac.grisu.js.model.JobDAO;


/**
 * The JobNameManager takes care that a user does not submit a job with the same name twice.
 * Before you create a Job, take the jobname you want to give your job and pass it to the {@link #getJobname(String, String, int)}
 * method. This will suggest a jobname according to the auto-naming scheme you have chosen.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobNameManager {
	

	public final String ITERATOR_SEPERATOR = "_";
	
	private static JobDAO jobdao = new JobDAO();

	/**
	 * Checks whether the same jobname is already in the db and suggests a new one according to the 
	 * auto-naming scheme you have chosen.
	 * 
	 * @param dn the dn of the user
	 * @param jobname the suggested name of the job
	 * @param createJobNameMethod the auto-naming scheme (have a look at {@link Job#ALWAYS_INCREMENT_JOB_NAME}, {@link Job#ALWAYS_TIMESTAMP_JOB_NAME},
	 * {@link Job#ONLY_INCREMENT_JOB_NAME_IF_JOB_EXISTS_WITH_SAME_NAME} and {@link Job#ONLY_TIMESTAMP_JOB_NAME_IF_JOB_EXISTS_WITH_SAME_NAME}.
	 * @return the new (suggested) jobname
	 */
	public static String getJobname(String dn, String proposedJobname, int createJobNameMethod) throws JobCreationException {
		
		proposedJobname = proposedJobname.replaceAll("\\s|;|'|\"|,|\\$|\\?|#", "_");
		
		switch (createJobNameMethod) {
		
		case JobConstants.ALWAYS_INCREMENT_JOB_NAME:
			List<Job> jobs = null;
			try {
					// gets all jobnames that start with "jobname"
					jobs = jobdao.getSimilarJobNamesByDN(dn, proposedJobname);
				} catch (NoSuchJobException e) {
					// means we only have to return the jobname +0
					return proposedJobname+"_0";
				}
				return proposedJobname+"_"+(highestJobnameNumber(jobs, proposedJobname)+1);

		case JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME:
			
			if ( proposedJobname == null || "".equals(proposedJobname) || "null".equals(proposedJobname) ) {
				throw new JobCreationException("Could not create job: no valid jobname specified.");
			}
			
			Job job = null;
			
			if ( createJobNameMethod == JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME ) {
				try {
					job = jobdao.findJobByDN(dn, proposedJobname);
					throw new JobCreationException("Could not create job: job with the same jobname already exists.");
				} catch (NoSuchJobException e) {
					// that's actually good. No job  with this jobname exists jet/anymore.
					//TODO look whether there's a job directory in one of the grisu-job directory with this name
				}
			}
			return proposedJobname;
		
		};

			
		// only JobConstants.ALWAYS_INCREMENT_JOB_NAME is implemented at the moment
		return null;
	}

	/**
	 * Looks up the job with the "highest" jobname, something like jobname_01, jobname_02, jobname_09 would
	 * return 9. Applies to the auto-naming schemes {@link Job#ALWAYS_INCREMENT_JOB_NAME} and Job#ONLY_INCREMENT_JOB_NAME_IF_JOB_EXISTS_WITH_SAME_NAME}.
	 * @param jobs the list of jobs to be looked at
	 * @param jobname the jobname in question
	 * @return the "highest" jobname
	 */
	private static int highestJobnameNumber(List<Job> jobs, String jobname) {
	
		int max = 0;

		for (Iterator i=jobs.iterator();i.hasNext();) {
			int value = 0;
			try {
				String tempName = ((Job)i.next()).getJobname();
				value = new Integer(tempName.substring(jobname.length()+1));
			} catch (Exception e) {
				//e.printStackTrace();
				// this error is ok.
			}
			if ( value > max ) max = value;
		}
		
		
		return max;
	}

}
