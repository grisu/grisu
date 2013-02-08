package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.frontend.model.job.GrisuJob;

import java.util.Comparator;

public class SingleJobObjectComparator implements Comparator<GrisuJob> {

	public int compare(GrisuJob o1, GrisuJob o2) {
		if ((o1 == null) && (o2 == null)) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			return o1.compareTo(o2);
		}

	}

}
