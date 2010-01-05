package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
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
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.model.job.JobObject;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

public class SimpleSingleJobsGrid extends JPanel {

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

	private final ServiceInterface si;
	private final EventList<JobObject> jobList;

	private final EventTableModel<JobObject> jobModel;

	private final SortedList<JobObject> sortedJobList;
	private final EventList<JobObject> observedJobs;

	private JPopupMenu popupMenu;

	private boolean enableSingleMouseClick = false;

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<SingleJobSelectionListener> listeners;
	private JMenuItem mntmKillSelectedJobs;
	private JMenuItem mntmKillAndClean;

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

		addPopup(getTable(), getPopupMenu());


	}

	public SimpleSingleJobsGrid(ServiceInterface si, String application) {

		this(si, RunningJobManager.getDefault(si).getJobs(application));

	}

	// register a listener
	synchronized public void addJobSelectionListener(
			SingleJobSelectionListener l) {
		if (listeners == null) {
			listeners = new Vector<SingleJobSelectionListener>();
		}
		listeners.addElement(l);
	}

	private void fireJobSelected(JobObject j) {
		// if we have no mountPointsListeners, do nothing...
		if ((listeners != null) && !listeners.isEmpty()) {

			lockUI(true);
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
			lockUI(false);
		}
	}

	private JMenuItem getMntmKillAndClean() {
		if (mntmKillAndClean == null) {
			mntmKillAndClean = new JMenuItem("Kill and clean selected job(s)");
			mntmKillAndClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						@Override
						public void run() {
							killSelectedJobs(true);
						}
					}.start();


				}
			});
		}
		return mntmKillAndClean;
	}

	private JMenuItem getMntmKillSelectedJobs() {
		if (mntmKillSelectedJobs == null) {
			mntmKillSelectedJobs = new JMenuItem("Kill selected job(s)");
			mntmKillSelectedJobs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						@Override
						public void run() {
							killSelectedJobs(false);
						}
					}.start();

				}
			});
		}
		return mntmKillSelectedJobs;
	}

	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getMntmKillSelectedJobs());
			popupMenu.add(getMntmKillAndClean());
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

	public Set<JobObject> getSelectedJobs() {

		Set<JobObject> selected = new HashSet<JobObject>();
		for (int r : table.getSelectedRows()) {

			if (r >= 0) {
				JobObject sel = (JobObject) jobModel.getValueAt(r, 0);
				selected.add(sel);
			}
		}

		return selected;
	}

	protected JXTable getTable() {
		if (table == null) {
			table = new JXTable(jobModel);
			table.setColumnControlVisible(true);
			table.setHighlighters(HighlighterFactory.createAlternateStriping());

			table.setDefaultRenderer(JobObject.class, new JobNameCellRenderer());

			table.getColumnExt("Site").setVisible(false);
			table.getColumnExt("Queue").setVisible(false);
			table.getColumnExt("Submission time").setVisible(false);
			table.getColumnExt("Group").setVisible(false);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {

					if ( enableSingleMouseClick ) {
						jobDoubleClickOccured();
					} else if (arg0.getClickCount() == 2) {
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

	protected void killSelectedJobs(final boolean clean) {

		StringBuffer message = new StringBuffer("Do you really want to kill ");
		if ( clean ) {
			message.append("and clean ");
		}
		message.append("the following job(s)?\n\n");

		for ( JobObject job : getSelectedJobs() ) {
			message.append(job.getJobname()+"\n");
		}

		int n = JOptionPane.showConfirmDialog(
				getRootPane(),
				message.toString(),
				"Kill and clean job(s)",
				JOptionPane.YES_NO_OPTION);

		if ( n == JOptionPane.YES_OPTION ) {

			lockUI(true);

			for ( JobObject job : getSelectedJobs() ) {
				//				getTable().getSelectionModel().clearSelection();
				job.kill(clean);
				if ( clean ) {
					jobList.remove(job);
				}
			}

			lockUI(false);

		}

	}

	private void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				getTable().setEnabled(!lock);

				if ( ! lock ) {
					getTable().setCursor(Cursor.getDefaultCursor());
				} else {
					getTable().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			}

		});

	}
	// remove a listener
	synchronized public void removeJobSelectionListener(
			SingleJobSelectionListener l) {
		if (listeners == null) {
			listeners = new Vector<SingleJobSelectionListener>();
		}
		listeners.removeElement(l);
	}
	public void setEnableSingleMouseClick(boolean enable) {
		this.enableSingleMouseClick = enable;
	}
}
