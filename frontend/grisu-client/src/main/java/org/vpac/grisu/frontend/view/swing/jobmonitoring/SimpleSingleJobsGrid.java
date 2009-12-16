package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.JobObject;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class SimpleSingleJobsGrid extends JPanel {
	private JScrollPane scrollPane;
	private JXTable table;

	private final ServiceInterface si;
	private final EventList<JobObject> jobList;

	private EventTableModel<JobObject> jobModel;

	private SortedList<JobObject> sortedJobList;
	private EventList<JobObject> observedJobs;

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<SingleJobSelectionListener> listeners;

	/**
	 * Create the panel.
	 */
	public SimpleSingleJobsGrid(ServiceInterface si,
			EventList<JobObject> jobList) {
		this.si = si;
		this.jobList = jobList;

		ObservableElementList.Connector<JobObject> joConnector = GlazedLists
				.beanConnector(JobObject.class);
		observedJobs = new ObservableElementList<JobObject>(jobList,
				joConnector);

		sortedJobList = new SortedList<JobObject>(observedJobs,
				new SingleJobObjectComparator());
		jobModel = new EventTableModel<JobObject>(sortedJobList,
				new SingleJobTableFormat());

		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

	}

	// register a listener
	synchronized public void addJobSelectionListener(
			SingleJobSelectionListener l) {
		if (listeners == null)
			listeners = new Vector<SingleJobSelectionListener>();
		listeners.addElement(l);
	}

	private void fireJobSelected(JobObject j) {
		// if we have no mountPointsListeners, do nothing...
		if (listeners != null && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<SingleJobSelectionListener> targets;
			synchronized (this) {
				targets = (Vector<SingleJobSelectionListener>) listeners
						.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (SingleJobSelectionListener bjsl : targets) {
				try {
					bjsl.jobSelected(j);
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
			table = new JXTable(jobModel);
			table.setColumnControlVisible(true);
			table.setHighlighters(HighlighterFactory.createAlternateStriping());

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if (arg0.getClickCount() == 2) {
						jobDoubleClickOccured();
					}

				}

			});
		}
		return table;
	}

	private void jobDoubleClickOccured() {

		int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			JobObject sel = (JobObject) jobModel.getValueAt(selRow, 0);
			fireJobSelected(sel);
		}

	}

	// remove a listener
	synchronized public void removeJobSelectionListener(
			SingleJobSelectionListener l) {
		if (listeners == null) {
			listeners = new Vector<SingleJobSelectionListener>();
		}
		listeners.removeElement(l);
	}
}
