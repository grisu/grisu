package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.control.ServiceInterface;
import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.frontend.model.job.BatchJobObject;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class BatchJobMonitoringGrid extends JPanel {

	static final Logger myLogger = LoggerFactory
			.getLogger(BatchJobMonitoringGrid.class.getName());

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private JScrollPane scrollPane;

	private JXTable table;

	private final EventTableModel<BatchJobObject> batchJobModel;
	private final EventList<BatchJobObject> batchJobs;
	private final SortedList<BatchJobObject> sortedBatchJobList;
	private final EventList<BatchJobObject> observedBatchJobs;

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final RunningJobManager rjm;
	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<BatchJobSelectionListener> listeners;
	private JPopupMenu popupMenu;
	private JMenuItem mntmRefreshManually;

	private JMenuItem mntmKillBatchjob;

	/**
	 * Create the panel.
	 */
	public BatchJobMonitoringGrid(ServiceInterface si, String application) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.rjm = RunningJobManager.getDefault(si);

		batchJobs = rjm.getBatchJobs(application);
		final ObservableElementList.Connector<BatchJobObject> bjoConnector = GlazedLists
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
		if (listeners == null) {
			listeners = new Vector<BatchJobSelectionListener>();
		}
		listeners.addElement(l);
	}

	private void batchJobDoubleClickOccured() {

		final int selRow = table.getSelectedRow();
		if (selRow >= 0) {

			final BatchJobObject sel = (BatchJobObject) batchJobModel
					.getValueAt(selRow, 1);
			fireBatchJobSelected(sel);
		}

	}

	private void fireBatchJobSelected(BatchJobObject bj) {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<BatchJobSelectionListener> targets;
			synchronized (this) {
				targets = (Vector<BatchJobSelectionListener>) listeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			for (final BatchJobSelectionListener bjsl : targets) {
				try {
					bjsl.batchJobSelected(bj);
				} catch (final Exception e1) {
					myLogger.error(e1.getLocalizedMessage(), e1);
				}
			}
		}
	}

	private JMenuItem getMntmKillBatchjob() {
		if (mntmKillBatchjob == null) {
			mntmKillBatchjob = new JMenuItem("Kill batchjob(s)");
			mntmKillBatchjob.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					final Set<BatchJobObject> bjs = getSelectedBatchJobs();

					final StringBuffer message = new StringBuffer(
							"Do you really want to kill and clean the following batchjobs?\n\n");

					for (final BatchJobObject bj : bjs) {
						message.append(bj.getJobname() + "\n");
					}

					final int n = JOptionPane.showConfirmDialog(getRootPane(),
							message.toString(), "Kill and clean batchjobs",
							JOptionPane.YES_NO_OPTION);

					if (n == JOptionPane.YES_OPTION) {

						new Thread() {
							@Override
							public void run() {

								for (final BatchJobObject bj : bjs) {
									bj.kill(true, false);
								}

							}
						}.start();
					}
				}
			});
		}
		return mntmKillBatchjob;
	}

	private JMenuItem getMntmRefreshManually() {
		if (mntmRefreshManually == null) {
			mntmRefreshManually = new JMenuItem("Refresh manually");
			mntmRefreshManually.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					for (final BatchJobObject bj : getSelectedBatchJobs()) {
						bj.refresh(false);
					}

				}
			});
		}
		return mntmRefreshManually;
	}

	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getMntmRefreshManually());
			popupMenu.add(getMntmKillBatchjob());
		}
		return popupMenu;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}

	public BatchJobObject getSelectedBatchJob() {

		final int row = getTable().getSelectedRow();

		final BatchJobObject bj = (BatchJobObject) batchJobModel.getValueAt(
				row, 1);

		return bj;

	}

	public Set<BatchJobObject> getSelectedBatchJobs() {

		final Set<BatchJobObject> result = new HashSet<BatchJobObject>();
		final int[] rows = getTable().getSelectedRows();

		for (final int row : rows) {
			final BatchJobObject bj = (BatchJobObject) batchJobModel
					.getValueAt(row, 1);
			result.add(bj);
		}

		return result;
	}

	private JXTable getTable() {
		if (table == null) {
			table = new JXTable(batchJobModel);
			// to fix sorting with glazed lists and jxtable
			table.setColumnControlVisible(true);
			table.setSortable(false);

			table.getColumnExt(0).setSortable(false);

			table.setHighlighters(HighlighterFactory.createAlternateStriping());
			addPopup(table, getPopupMenu());

			table.setDefaultRenderer(BatchJobObject.class,
					new BatchJobNameCellRenderer());
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
