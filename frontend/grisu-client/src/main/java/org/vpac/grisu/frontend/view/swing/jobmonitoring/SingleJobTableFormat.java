package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.model.job.JobObject;

import au.org.arcs.jcommons.constants.Constants;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class SingleJobTableFormat implements AdvancedTableFormat<JobObject> {

	private static final SingleJobObjectComparator comp = new SingleJobObjectComparator();

	public Class getColumnClass(int column) {

		switch (column) {
		case 0:
			return JobObject.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return Date.class;
		case 4:
			return String.class;
		case 5:
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
		case 2:
			return null;
		case 3:
			return null;
		case 4:
			return null;
		case 5:
			return null;
		}

		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 6;
	}

	public String getColumnName(int column) {

		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Site";
		case 2:
			return "Queue";
		case 3:
			return "Submission time";
		case 4:
			return "Group";
		case 5:
			return "Status";
		}

		throw new IllegalStateException();

	}

	public Object getColumnValue(JobObject baseObject, int column) {

		switch (column) {
		case 0:
			return baseObject;
		case 1:
			return baseObject.getJobProperty(Constants.SUBMISSION_SITE_KEY);
		case 2:
			return baseObject.getJobProperty(Constants.QUEUE_KEY);
		case 3:
			String time = baseObject
					.getJobProperty(Constants.SUBMISSION_TIME_KEY);
			if (StringUtils.isBlank(time)) {
				return null;
			}
			try {
				return new Date(Long.parseLong(time));
			} catch (Exception e) {
				return null;
			}
		case 4:
			return baseObject.getJobProperty(Constants.FQAN_KEY);
		case 5:
			return baseObject.getStatusString(false);
		}

		throw new IllegalStateException();
	}

}
