package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;

import org.vpac.grisu.model.dto.DtoBatchJob;

public class DtoBatchJobComparator implements Comparator<DtoBatchJob> {

	public int compare(DtoBatchJob o1, DtoBatchJob o2) {

		return o1.getBatchJobname().compareTo(o2.getBatchJobname());
		
	}

}
