package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.JobObject;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class SingleJobTableFormat implements AdvancedTableFormat<JobObject> {

	private static final SingleJobObjectComparator comp = new SingleJobObjectComparator();

	public Class getColumnClass(int column) {

		switch (column) {
		case 0:
			return JobObject.class;
		case 1:
			return String.class;
		}

		throw new IllegalStateException();
	}

	public Comparator getColumnComparator(int column) {

		switch (column) {
		case 0:
			return comp;
		case 1:
			return null;
		}

		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {

		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Status";
		}

		throw new IllegalStateException();

	}

	public Object getColumnValue(JobObject baseObject, int column) {

		switch (column) {
		case 0:
			return baseObject;
		case 1:
			return baseObject.getStatusString(false);
		}

		throw new IllegalStateException();
	}

}
