package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.frontend.model.job.GrisuJob;
import grisu.jcommons.constants.Constants;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class SingleJobTableFormat implements AdvancedTableFormat<GrisuJob> {

	private static final SingleJobObjectComparator comp = new SingleJobObjectComparator();

	public Class getColumnClass(int column) {

		switch (column) {
		case 0:
			return GrisuJob.class;
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

	public Object getColumnValue(GrisuJob baseObject, int column) {

		switch (column) {
		case 0:
			return baseObject;
		case 1:
			try {
				return baseObject.getJobProperty(Constants.APPLICATIONNAME_KEY,
						false);
			} catch (final Exception e) {
				return "n/a";
			}
		case 2:
			try {
				return baseObject.getJobProperty(Constants.SUBMISSION_SITE_KEY,
						false);
			} catch (final Exception e) {
				return "n/a";
			}
		case 3:
			try {
				return baseObject.getJobProperty(Constants.QUEUE_KEY, false);
			} catch (final Exception e) {
				return "n/a";
			}
		case 4:
			String time = null;
			try {
				time = baseObject.getJobProperty(Constants.SUBMISSION_TIME_KEY,
						false);
			} catch (final Exception e) {
				return null;
			}
			if (StringUtils.isBlank(time)) {
				return null;
			}
			try {
				return new Date(Long.parseLong(time));
			} catch (final Exception e) {
				return null;
			}
		case 5:
			try {
				return baseObject.getJobProperty(Constants.FQAN_KEY, false);
			} catch (final Exception e) {
				return "n/a";
			}

		case 6:
			if (baseObject == null) {
				return "n/a";
			}
			return baseObject.getStatusString(false);
		}

		throw new IllegalStateException();
	}

}
