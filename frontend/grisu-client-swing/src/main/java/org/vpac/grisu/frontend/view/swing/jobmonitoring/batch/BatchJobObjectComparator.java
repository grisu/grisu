package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobObjectComparator implements Comparator<BatchJobObject> {

	public int compare(BatchJobObject o1, BatchJobObject o2) {

		return o1.getJobname().compareTo(o2.getJobname());

	}

}
