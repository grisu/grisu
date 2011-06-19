package grisu.backend.model.job.gt5;

import java.util.HashMap;

import org.globus.gram.GramJob;
import org.globus.gram.GramJobListener;

public class Gram5JobListener implements GramJobListener{
	
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
