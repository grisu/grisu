package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.util.Comparator;

import org.vpac.grisu.model.dto.DtoBatchJob;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class GlazedBatchJobTableFormat implements
		AdvancedTableFormat<DtoBatchJob> {
	
	private DtoBatchJobComparator batchJobComparator = new DtoBatchJobComparator();

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {

		switch(column) {
		case 0: return "Name";
		case 1: return "Status";
		}
		
		throw new IllegalStateException();
	}

	public Object getColumnValue(DtoBatchJob baseObject, int column) {

		switch(column) {
		case 0: return baseObject.getBatchJobname();
		case 1: return baseObject.getStatus();
		}
		
		throw new IllegalStateException();
	}

	public Class getColumnClass(int column) {

		switch(column) {
		case 0: return DtoBatchJob.class;
		case 1: return Double.class;
		}
		
		throw new IllegalStateException();
	}

	public Comparator getColumnComparator(int column) {

		switch(column) {
		case 0: return batchJobComparator;
		case 1: return null;
		}
		
		throw new IllegalStateException();
	}

}
