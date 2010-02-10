package org.vpac.grisu.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.utils.ApplicationsManager;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;

public class GrisuNavigationPanel extends JXTaskPaneContainer implements PropertyChangeListener {

	private final ServiceInterface si;

	private final Map<String, JXTaskPane> taskPanes = new HashMap<String, JXTaskPane>();

	private final GrisuCenterPanel centerPanel;

	private final Set<String> applicationsToWatch = new TreeSet<String>();

	private final GrisuMonitorNavigationTaskPane singleTaskPane;
	private final GrisuMonitorNavigationTaskPaneBatch batchTaskPane;

	private static final String JOB_CREATION_TASK_PANE = "Job creation";

	/**
	 * Create the panel.
	 */
	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel) {

		this (si, centerPanel, true, true, true);
	}

	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel, boolean displaySingleJobMonitorItem,
			boolean displayBatchJobMonitorItem, boolean displayFileManagementItem) {

		this(si, centerPanel, displaySingleJobMonitorItem, true, true, displayBatchJobMonitorItem, true, true, displayFileManagementItem);
	}

	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel,
			boolean displaySingleJobMonitorItem,	boolean displaySingleJobAllJobsMenuItem, boolean displaySingleJobAppSpecificMenuItems,
			boolean displayBatchJobMonitorItem, boolean displayBatchJobAllJobsMenuItem, boolean displayBatchJobAppSpecificMenuItems,
			boolean displayFileManagementItem) {

		this.si = si;
		this.centerPanel = centerPanel;
		this.centerPanel.addNavigationPanel(this);

		addTaskPane(JOB_CREATION_TASK_PANE, null);

		singleTaskPane = new GrisuMonitorNavigationTaskPane(si, this, displaySingleJobAllJobsMenuItem, displaySingleJobAppSpecificMenuItems);
		if ( displaySingleJobMonitorItem ) {
			addTaskPane(singleTaskPane);
		}
		batchTaskPane = new GrisuMonitorNavigationTaskPaneBatch(si, this, displayBatchJobAllJobsMenuItem, displayBatchJobAppSpecificMenuItems);
		if ( displayBatchJobMonitorItem ) {
			addTaskPane(batchTaskPane);
		}
		if ( displayFileManagementItem ) {
			addTaskPane(new GrisuFileNavigationTaskPane(si, this));
		}

	}

	private void addApplicationsToWatch(Map<String, JobCreationPanel> applications) {

		for ( JobCreationPanel panel : applications.values() ) {
			if ( ! applicationsToWatch.contains(panel.getSupportedApplication()) ) {

				addTaskPaneItem(JOB_CREATION_TASK_PANE, ApplicationsManager.getPrettyName(panel.getPanelName()),
						ApplicationsManager.getShortDescription(panel.getSupportedApplication()), ApplicationsManager.getIcon(panel.getSupportedApplication()));

				if ( panel.createsBatchJob() ) {
					batchTaskPane.addApplication(panel.getSupportedApplication());
				} else {
					singleTaskPane.addApplication(panel.getSupportedApplication());
				}

			}
		}

	}

	public void addTaskPane(JXTaskPane pane) {

		taskPanes.put(pane.getTitle(), pane);
		add(pane);

	}

	public void addTaskPane(String title, Icon icon) {

		JXTaskPane temp = new JXTaskPane();
		temp.setTitle(title);
		temp.setIcon(icon);

		addTaskPane(temp);
	}

	public void addTaskPaneItem(String taskPane, final String itemTitle, final String itemDescription, final Icon icon) {

		if ( taskPanes.get(taskPane) == null ) {
			addTaskPane(taskPane, null);
		}

		Action tempAction = new AbstractAction() {
			{

				putValue(Action.NAME, itemTitle);

				putValue(Action.SHORT_DESCRIPTION, itemDescription);

				putValue(Action.SMALL_ICON, icon);

			}
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				setNavigationCommand(new String[]{itemTitle});
			}
		};

		taskPanes.get(taskPane).add(tempAction);

	}

	private void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				setEnabled(!lock);
			}

		});

	}

	public void propertyChange(PropertyChangeEvent evt) {

		if ( "availableJobCreationPanels".equals(evt.getPropertyName()) ) {
			addApplicationsToWatch((Map<String, JobCreationPanel>)evt.getNewValue());
		}

	}

	public void setNavigationCommand(final String[] command) {

		new Thread() {
			@Override
			public void run() {

				lockUI(true);

				centerPanel.setNavigationCommand(command);

				lockUI(false);
			}
		}.start();


	}

}
