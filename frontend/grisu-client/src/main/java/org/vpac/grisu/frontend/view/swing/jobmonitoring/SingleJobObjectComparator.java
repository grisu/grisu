package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.JobObject;

public class SingleJobObjectComparator implements Comparator<JobObject> {

	public int compare(JobObject o1, JobObject o2) {
		return o1.compareTo(o2);
	}

}
