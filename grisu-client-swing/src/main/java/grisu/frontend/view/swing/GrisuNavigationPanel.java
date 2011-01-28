package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;
import grisu.frontend.control.utils.ApplicationsManager;
import grisu.frontend.view.swing.jobcreation.JobCreationPanel;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

public class GrisuNavigationPanel extends JXTaskPaneContainer implements
		PropertyChangeListener {

	static final Logger myLogger = Logger.getLogger(GrisuNavigationPanel.class
			.getName());

	private final ServiceInterface si;

	private final Map<String, JXTaskPane> taskPanes = new HashMap<String, JXTaskPane>();
	private final Map<String, Set<Action>> taskPaneActions = new HashMap<String, Set<Action>>();

	private final GrisuCenterPanel centerPanel;

	private final Set<String> applicationsToWatch = new TreeSet<String>();

	private GrisuMonitorNavigationTaskPane singleTaskPane;
	private GrisuMonitorNavigationTaskPaneBatch batchTaskPane;

	private GrisuFileNavigationTaskPane fileNaviagaionTaskPane;

	private static final String JOB_CREATION_TASK_PANE = "Create job";

	/**
	 * Create the panel.
	 */
	public GrisuNavigationPanel(ServiceInterface si,
			GrisuCenterPanel centerPanel) {

		this(si, centerPanel, true, true, true);
	}

	public GrisuNavigationPanel(ServiceInterface si,
			GrisuCenterPanel centerPanel, boolean displaySingleJobMonitorItem,
			boolean displayBatchJobMonitorItem,
			boolean displayFileManagementItem) {

		this(si, centerPanel, displaySingleJobMonitorItem, true, true, null,
				displayBatchJobMonitorItem, true, true, null);
	}

	public GrisuNavigationPanel(ServiceInterface si,
			GrisuCenterPanel centerPanel, boolean displaySingleJobMonitorItem,
			boolean displaySingleJobAllJobsMenuItem,
			boolean displaySingleJobAppSpecificMenuItems,
			Set<String> singleApplicationsToWatch,
			boolean displayBatchJobMonitorItem,
			boolean displayBatchJobAllJobsMenuItem,
			boolean displayBatchJobAppSpecificMenuItems,
			Set<String> batchApplicationsToWatch) {

		this.si = si;
		this.centerPanel = centerPanel;
		this.centerPanel.addNavigationPanel(this);

		addTaskPane(JOB_CREATION_TASK_PANE, null);

		if (displaySingleJobMonitorItem) {
			singleTaskPane = new GrisuMonitorNavigationTaskPane(si, this,
					displaySingleJobAllJobsMenuItem,
					displaySingleJobAppSpecificMenuItems,
					singleApplicationsToWatch);
			addTaskPane(singleTaskPane);
		}
		if (displayBatchJobMonitorItem) {
			batchTaskPane = new GrisuMonitorNavigationTaskPaneBatch(si, this,
					displayBatchJobAllJobsMenuItem,
					displayBatchJobAppSpecificMenuItems,
					batchApplicationsToWatch);
			addTaskPane(batchTaskPane);
		}

	}

	private void addApplicationsToWatch(
			Map<String, JobCreationPanel> applications) {

		for (final JobCreationPanel panel : applications.values()) {
			if (!applicationsToWatch.contains(panel.getSupportedApplication())) {

				addTaskPaneItem(
						JOB_CREATION_TASK_PANE,
						ApplicationsManager.getPrettyName(panel.getPanelName()),
						// addTaskPaneItem(JOB_CREATION_TASK_PANE,
						// panel.getPanelName(),
						ApplicationsManager.getShortDescription(panel
								.getSupportedApplication()),
						ApplicationsManager.getIcon(panel
								.getSupportedApplication()));

				if (panel.createsBatchJob()) {
					if (batchTaskPane == null) {
						myLogger.warn("Can't add batch job "
								+ panel.getPanelName()
								+ " item because batchjob pane is not displayed...");
						batchTaskPane.addApplication(panel
								.getSupportedApplication());
					}

				}
				if (panel.createsSingleJob()) {
					if (singleTaskPane == null) {
						myLogger.warn("Can't add single job "
								+ panel.getPanelName()
								+ " item because single job pane is not displayed...");
					}
					singleTaskPane.addApplication(panel
							.getSupportedApplication());
				}

			}
		}

	}

	public void addDefaultFileManagementPanel() {
		getFileNavigationTaskPane().addDefaultFileManagementPanel();
	}

	public void addGroupFileManagementPanel() {
		getFileNavigationTaskPane().addGroupFileManagementPanel();
	}

	public void addTaskPane(JXTaskPane pane) {

		taskPanes.put(pane.getTitle(), pane);
		add(pane);

	}

	public void addTaskPane(String title, Icon icon) {

		final JXTaskPane temp = new JXTaskPane();
		temp.setTitle(title);
		temp.setIcon(icon);

		addTaskPane(temp);
	}

	public void addTaskPaneItem(String taskPane, final String itemTitle,
			final String itemDescription, final Icon icon) {

		if (taskPanes.get(taskPane) == null) {
			addTaskPane(taskPane, null);
		}

		if ((taskPaneActions.get(taskPane) != null)
				&& taskPaneActions.get(taskPane).contains(itemTitle)) {
			return;
		}

		final Action tempAction = new AbstractAction() {
			{

				putValue(Action.NAME, itemTitle);

				putValue(Action.SHORT_DESCRIPTION, itemDescription);

				putValue(Action.SMALL_ICON, icon);

			}

			public void actionPerformed(ActionEvent e) {

				setNavigationCommand(new String[] { itemTitle });
			}
		};

		taskPanes.get(taskPane).add(tempAction);
		Set<Action> temp = taskPaneActions.get(taskPane);
		if (temp == null) {
			temp = new HashSet<Action>();
			taskPaneActions.put(taskPane, temp);
		}
		temp.add(tempAction);

	}

	private GrisuFileNavigationTaskPane getFileNavigationTaskPane() {
		if (fileNaviagaionTaskPane == null) {
			fileNaviagaionTaskPane = new GrisuFileNavigationTaskPane(si, this);
			addTaskPane(fileNaviagaionTaskPane);
		}
		return fileNaviagaionTaskPane;
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

		if ("availableJobCreationPanels".equals(evt.getPropertyName())) {

			final Map<String, JobCreationPanel> temp = (Map<String, JobCreationPanel>) evt
					.getNewValue();

			taskPanes.get(JOB_CREATION_TASK_PANE).removeAll();

			addApplicationsToWatch(temp);
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
