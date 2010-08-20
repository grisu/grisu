package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.JobObject;

public class SingleJobObjectComparator implements Comparator<JobObject> {

	public int compare(JobObject o1, JobObject o2) {
		if ( (o1 == null) && (o2 == null) ) {
			return 0;
		} else if ( o1 == null ) {
			return -1;
		} else if ( o2 == null ) {
			return 1;
		} else {
			return o1.compareTo(o2);
		}

	}

}
