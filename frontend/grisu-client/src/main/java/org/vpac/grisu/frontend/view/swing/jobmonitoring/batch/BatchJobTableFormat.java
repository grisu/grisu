package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class BatchJobTableFormat implements AdvancedTableFormat<BatchJobObject> {

	private BatchJobObjectComparator batchJobComparator = new BatchJobObjectComparator();

	public Class getColumnClass(int column) {

		switch (column) {
		case 0:
			return Boolean.class;
		case 1:
			return BatchJobObject.class;
		case 2:
			return Integer.class;
		case 3:
			return Integer.class;
		case 4:
			return Integer.class;
		case 5:
			return Integer.class;
		case 6:
			return Integer.class;
		case 7:
			return Integer.class;
		case 8:
			return Double.class;
		}

		throw new IllegalStateException();
	}

	public Comparator getColumnComparator(int column) {

		switch (column) {
		case 0:
			return null;
		case 1:
			return batchJobComparator;
		case 2:
			return null;
		case 3:
			return null;
		case 4:
			return null;
		case 5:
			return null;
		case 6:
			return null;
		case 7:
			return null;
		case 8:
			return null;
		}

		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 9;
	}

	public String getColumnName(int column) {

		switch (column) {
		case 0:
			return "";
		case 1:
			return "Name";
		case 2:
			return "Unsub";
		case 3:
			return "Wait";
		case 4:
			return "Run";
		case 5:
			return "Fail";
		case 6:
			return "Done";
		case 7:
			return "Total";
		case 8:
			return "Status";
		}

		throw new IllegalStateException();
	}

	public Object getColumnValue(BatchJobObject baseObject, int column) {

		switch (column) {
		case 0:
			return baseObject.isRefreshing();
		case 1:
			return baseObject;
		case 2:
			return baseObject.getNumberOfUnsubmittedJobs();
		case 3:
			return baseObject.getNumberOfWaitingJobs();
		case 4:
			return baseObject.getNumberOfRunningJobs();
		case 5:
			return baseObject.getNumberOfFailedJobs();
		case 6:
			return baseObject.getNumberOfSuccessfulJobs();
		case 7:
			return baseObject.getTotalNumberOfJobs();
		case 8:
			return baseObject.getStatus(false);
		}

		throw new IllegalStateException();
	}

}
