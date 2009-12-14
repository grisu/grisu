package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class BatchJobMonitoringGrid extends JPanel {
	private JScrollPane scrollPane;
	private JXTable table;
	
	
	private EventTableModel<BatchJobObject> batchJobModel;

	private EventList<BatchJobObject> batchJobs;
	private SortedList<BatchJobObject> sortedBatchJobList;
	private final ServiceInterface si;
	
	private final UserEnvironmentManager em;
	private final RunningJobManager rjm;
	
	/**
	 * Create the panel.
	 */
	public BatchJobMonitoringGrid(ServiceInterface si, String application) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
		this.rjm = RunningJobManager.getDefault(si);
		
		batchJobs = GlazedLists.eventList(rjm.getBatchJobs(application));
		ObservableElementList.Connector<BatchJobObject> bjoConnector = GlazedLists.beanConnector(BatchJobObject.class);
		EventList<BatchJobObject> observedCars = new ObservableElementList<BatchJobObject>(batchJobs, bjoConnector);

		sortedBatchJobList = new SortedList<BatchJobObject>(observedCars, new BatchJobObjectComparator());
		batchJobModel = new EventTableModel<BatchJobObject>(sortedBatchJobList, 
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
