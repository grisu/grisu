package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
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
	private EventList<BatchJobObject> observedBatchJobs;
	private final ServiceInterface si;

	private final UserEnvironmentManager em;
	private final RunningJobManager rjm;

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<BatchJobSelectionListener> listeners;

	/**
	 * Create the panel.
	 */
	public BatchJobMonitoringGrid(ServiceInterface si, String application) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.rjm = RunningJobManager.getDefault(si);

		batchJobs = rjm.getBatchJobs(application);
		ObservableElementList.Connector<BatchJobObject> bjoConnector = GlazedLists
				.beanConnector(BatchJobObject.class);
		observedBatchJobs = new ObservableElementList<BatchJobObject>(
				batchJobs, bjoConnector);

		sortedBatchJobList = new SortedList<BatchJobObject>(observedBatchJobs,
				new BatchJobObjectComparator());
		batchJobModel = new EventTableModel<BatchJobObject>(sortedBatchJobList,
				new BatchJobTableFormat());

		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

	}

	// register a listener
	synchronized public void addBatchJobSelectionListener(
			BatchJobSelectionListener l) {
		if (listeners == null)
			listeners = new Vector<BatchJobSelectionListener>();
		listeners.addElement(l);
	}

	private void batchJobDoubleClickOccured() {

		int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			BatchJobObject sel = (BatchJobObject) batchJobModel.getValueAt(
					selRow, 1);
			fireBatchJobSelected(sel);
		}

	}

	private void fireBatchJobSelected(BatchJobObject bj) {
		// if we have no mountPointsListeners, do nothing...
		if (listeners != null && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<BatchJobSelectionListener> targets;
			synchronized (this) {
				targets = (Vector<BatchJobSelectionListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (BatchJobSelectionListener bjsl : targets) {
				try {
					bjsl.batchJobSelected(bj);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
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
			table.setColumnControlVisible(true);

			table.getColumnExt(0).setSortable(false);
			// table.getColumnExt(0).setHeaderRenderer()

			table.setHighlighters(HighlighterFactory.createAlternateStriping());

			table.setDefaultRenderer(Boolean.class,
					new BatchJobRefreshRenderer());
			table.setDefaultRenderer(Double.class,
					new BatchJobStatusCellRenderer());

			int vColIndex = 0;
			TableColumn col = table.getColumnModel().getColumn(vColIndex);
			int width = 20;
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 1;
			col = table.getColumnModel().getColumn(vColIndex);
			width = 140;
			col.setPreferredWidth(width);
			col.setMinWidth(100);

			width = 40;
			vColIndex = 2;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 3;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 4;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 5;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 6;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			vColIndex = 7;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);

			width = 80;
			vColIndex = 8;
			col = table.getColumnModel().getColumn(vColIndex);
			col.setPreferredWidth(width);
			col.setMinWidth(60);
			col.setMaxWidth(100);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if (arg0.getClickCount() == 2) {
						batchJobDoubleClickOccured();
					}

				}

			});

		}
		return table;
	}

	// remove a listener
	synchronized public void removeBatchJobSelectionListener(
			BatchJobSelectionListener l) {
		if (listeners == null) {
			listeners = new Vector<BatchJobSelectionListener>();
		}
		listeners.removeElement(l);
	}
}
