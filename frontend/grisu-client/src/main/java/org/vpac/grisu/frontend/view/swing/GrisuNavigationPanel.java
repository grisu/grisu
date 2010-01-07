package org.vpac.grisu.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.vpac.grisu.control.ServiceInterface;

public class GrisuNavigationPanel extends JXTaskPaneContainer {

	private final ServiceInterface si;

	private final Map<String, JXTaskPane> taskPanes = new HashMap<String, JXTaskPane>();

	private final GrisuCenterPanel centerPanel;

	/**
	 * Create the panel.
	 */
	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel) {

		this (si, centerPanel, true, true, true);
	}

	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel, boolean displaySingleJobMonitorItem,
			boolean displayBatchJobMonitorItem, boolean displayFileManagementItem) {

		this(si, centerPanel, displaySingleJobMonitorItem, false, true, displayBatchJobMonitorItem, false, true, displayFileManagementItem);
	}

	public GrisuNavigationPanel(ServiceInterface si, GrisuCenterPanel centerPanel,
			boolean displaySingleJobMonitorItem,	boolean displaySingleJobAllJobsMenuItem, boolean displaySingleJobAppSpecificMenuItems,
			boolean displayBatchJobMonitorItem, boolean displayBatchJobAllJobsMenuItem, boolean displayBatchJobAppSpecificMenuItems,
			boolean displayFileManagementItem) {

		this.si = si;
		this.centerPanel = centerPanel;
		if ( displaySingleJobMonitorItem ) {
			addTaskPane(new GrisuMonitorNavigationTaskPane(si, this, displaySingleJobAllJobsMenuItem, displaySingleJobAppSpecificMenuItems));
		}
		if ( displayBatchJobMonitorItem ) {
			addTaskPane(new GrisuMonitorNavigationTaskPaneBatch(si, this, displayBatchJobAllJobsMenuItem, displayBatchJobAppSpecificMenuItems));
		}
		if ( displayFileManagementItem ) {
			addTaskPane(new GrisuFileNavigationTaskPane(si, this));
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
