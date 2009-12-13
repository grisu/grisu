package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoBatchJob;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class BatchJobMonitoringGrid extends JPanel {
	private JScrollPane scrollPane;
	private JXTable table;
	
	
	private EventTableModel<DtoBatchJob> batchJobModel;

	private EventList<DtoBatchJob> batchJobs;
	private SortedList<DtoBatchJob> sortedBatchJobList;
	private final ServiceInterface si;
	
	private final UserEnvironmentManager em;
	
	/**
	 * Create the panel.
	 */
	public BatchJobMonitoringGrid(ServiceInterface si, String application) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
		
		batchJobs = GlazedLists.eventList(em.getBatchJobs(application, false));
		sortedBatchJobList = new SortedList<DtoBatchJob>(batchJobs, new DtoBatchJobComparator());
		batchJobModel = new EventTableModel<DtoBatchJob>(sortedBatchJobList, 
				new GlazedBatchJobTableFormat());
		
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}
	private JXTable getTable() {
		if (table == null) {
			table = new JXTable(batchJobModel);
		}
		return table;
	}
}
