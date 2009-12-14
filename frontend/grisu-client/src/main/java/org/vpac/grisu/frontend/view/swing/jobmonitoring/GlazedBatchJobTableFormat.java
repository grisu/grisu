package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;

import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.model.dto.DtoBatchJob;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class GlazedBatchJobTableFormat implements
		AdvancedTableFormat<BatchJobObject> {
	
	private BatchJobObjectComparator batchJobComparator = new BatchJobObjectComparator();

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {

		switch(column) {
		case 0: return "Name";
		case 1: return "Refreshing";
		case 2: return "Status";
		}
		
		throw new IllegalStateException();
	}

	public Object getColumnValue(BatchJobObject baseObject, int column) {

		switch(column) {
		case 0: return baseObject.getJobname();
		case 1: return baseObject.isRefreshing();
		case 2: return baseObject.getStatus(false);
		}
		
		throw new IllegalStateException();
	}

	public Class getColumnClass(int column) {

		switch(column) {
		case 0: return DtoBatchJob.class;
		case 1: return Boolean.class;
		case 2: return Double.class;
		}
		
		throw new IllegalStateException();
	}

	public Comparator getColumnComparator(int column) {

		switch(column) {
		case 0: return batchJobComparator;
		case 1: return null;
		case 2: return null;
		}
		
		throw new IllegalStateException();
	}

}
