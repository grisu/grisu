package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.frontend.model.job.BatchJobObject;

import java.util.Comparator;

public class BatchJobObjectComparator implements Comparator<BatchJobObject> {

	public int compare(BatchJobObject o1, BatchJobObject o2) {

		return o1.getJobname().compareTo(o2.getJobname());

	}

}
