package grisu.backend.model.job.gt5;

import java.util.HashMap;

import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;

import org.apache.log4j.Logger;

public class Gram5JobListener implements GramJobListener{

	static final Logger myLogger = Logger.getLogger(GT5Submitter.class
			.getName());
	
	private static Gram5JobListener l;
	private HashMap<String,Integer> statuses; 
	private HashMap<String,Integer> errors; 
	
	private Gram5JobListener(){
		statuses = new HashMap<String,Integer>();
		errors = new HashMap<String,Integer>();
	}
	
	public static Gram5JobListener getJobListener(){
		if (l == null){
			l = new Gram5JobListener();
		}
		return l;
	}


	public void statusChanged(GramJob job) {
        myLogger.debug("job status changed to " + job.getStatus());
		statuses.put( job.getIDAsString(),job.getStatus());
		errors.put(job.getIDAsString(), job.getError());
	}
	
	public Integer getStatus(String handle){
		return statuses.get(handle);
	}
	
	public Integer getError(String handle){
		return errors.get(handle);
	}

}
