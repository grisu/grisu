package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

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
			return String.class;
		case 4:
			return Date.class;
		case 5:
			return String.class;
		case 6:
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
		case 6:
			return null;
		}

		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 7;
	}

	public String getColumnName(int column) {

		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Application";
		case 2:
			return "Site";
		case 3:
			return "Queue";
		case 4:
			return "Submission time";
		case 5:
			return "Group";
		case 6:
			return "Status";
		}

		throw new IllegalStateException();

	}

	public Object getColumnValue(JobObject baseObject, int column) {

		switch (column) {
		case 0:
			return baseObject;
		case 1:
			try {
				return baseObject.getJobProperty(Constants.APPLICATIONNAME_KEY);
			} catch (Exception e) {
				return "n/a";
			}
		case 2:
			try {
				return baseObject.getJobProperty(Constants.SUBMISSION_SITE_KEY);
			} catch (Exception e) {
				return "n/a";
			}
		case 3:
			try {
				return baseObject.getJobProperty(Constants.QUEUE_KEY);
			} catch (Exception e) {
				return "n/a";
			}
		case 4:
			String time = null;
			try {
				time = baseObject
				.getJobProperty(Constants.SUBMISSION_TIME_KEY);
			} catch (Exception e) {
				return null;
			}
			if (StringUtils.isBlank(time)) {
				return null;
			}
			try {
				return new Date(Long.parseLong(time));
			} catch (Exception e) {
				return null;
			}
		case 5:
			try {
				return baseObject.getJobProperty(Constants.FQAN_KEY);
			} catch (Exception e) {
				return "n/a";
			}

		case 6:
			if ( baseObject == null ) {
				return "n/a";
			}
			return baseObject.getStatusString(false);
		}

		throw new IllegalStateException();
	}

}
