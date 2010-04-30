package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;
import org.vpac.grisu.frontend.view.swing.login.GrisuSwingClient;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class GrisuMainPanel extends JPanel implements GrisuSwingClient {

	private ServiceInterface si;
	private LoginPanel lp;
	private GrisuNavigationPanel grisuNavigationPanel;
	private GrisuCenterPanel grisuCenterPanel;

	private final Set<JobCreationPanel> jobCreationPanels = new HashSet<JobCreationPanel>();
	private final boolean displaySingleJobMonitorItem;
	private final boolean displaySingleJobAllJobsMenuItem;
	private final Set<String> singleApplicationsToWatch;
	private final boolean displaySingleJobAppSpecificMenuItems;
	private final boolean displayBatchJobAllJobsMenuItem;
	private final boolean displayBatchJobMonitorItem;
	private final Set<String> batchApplicationsToWatch;
	private final boolean displayBatchJobAppSpecificMenuItems;
	private final boolean displayFileManagementItem;

	/**
	 * Create the panel.
	 */
	public GrisuMainPanel(boolean displaySingleJobMonitorItem,
			boolean displaySingleJobAllJobsMenuItem,
			boolean displaySingleJobAppSpecificMenuItems,
			Set<String> singleApplicationsToWatch,
			boolean displayBatchJobMonitorItem,
			boolean displayBatchJobAllJobsMenuItem,
			boolean displayBatchJobAppSpecificMenuItems,
			Set<String> batchApplicationsToWatch,
			boolean displayFileManagementItem) {

		this.displaySingleJobMonitorItem = displaySingleJobMonitorItem;
		this.displaySingleJobAllJobsMenuItem = displaySingleJobAllJobsMenuItem;
		this.singleApplicationsToWatch = singleApplicationsToWatch;
		this.displaySingleJobAppSpecificMenuItems = displaySingleJobAppSpecificMenuItems;
		this.displayBatchJobAllJobsMenuItem = displayBatchJobAllJobsMenuItem;
		this.displayBatchJobMonitorItem = displayBatchJobMonitorItem;
		this.batchApplicationsToWatch = batchApplicationsToWatch;
		this.displayBatchJobAppSpecificMenuItems = displayBatchJobAppSpecificMenuItems;
		this.displayFileManagementItem = displayFileManagementItem;

		setLayout(new BorderLayout(0, 0));
	}

	public GrisuMainPanel(boolean watchSingleApplication,
			Set<String> applicationsToWatch) {
		this(watchSingleApplication, false, false, applicationsToWatch,
				!watchSingleApplication, false, false, applicationsToWatch,
				true);
	}

	public void addJobCreationPanel(JobCreationPanel panel) {

		jobCreationPanels.add(panel);
		if (si != null) {
			getGrisuCenterPanel().addJobCreationPanel(panel);
			panel.setServiceInterface(si);
		}

	}

	private GrisuCenterPanel getGrisuCenterPanel() {
		if (grisuCenterPanel == null) {
			grisuCenterPanel = new GrisuCenterPanel(si);
		}
		return grisuCenterPanel;
	}

	private GrisuNavigationPanel getGrisuNavigationPanel() {
		if (grisuNavigationPanel == null) {
			// grisuNavigationPanel = new GrisuNavigationPanel(this.si,
			// getGrisuCenterPanel());
			grisuNavigationPanel = new GrisuNavigationPanel(this.si,
					getGrisuCenterPanel(), displaySingleJobMonitorItem,
					displaySingleJobAllJobsMenuItem,
					displaySingleJobAppSpecificMenuItems,
					singleApplicationsToWatch, displayBatchJobMonitorItem,
					displayBatchJobAllJobsMenuItem,
					displayBatchJobAppSpecificMenuItems,
					batchApplicationsToWatch, displayFileManagementItem);
		}
		return grisuNavigationPanel;
	}

	public JPanel getRootPanel() {
		return this;
	}

	public void removeAlJobCreationPanelsl() {

		jobCreationPanels.clear();
		if (si != null) {
			getGrisuCenterPanel().removeJobCreationPanels();
		}

	}

	public void setLoginPanel(LoginPanel lp) {
		this.lp = lp;
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		add(getGrisuNavigationPanel(), BorderLayout.WEST);
		add(getGrisuCenterPanel(), BorderLayout.CENTER);

		for (JobCreationPanel panel : jobCreationPanels) {
			getGrisuCenterPanel().addJobCreationPanel(panel);
			panel.setServiceInterface(si);
		}

	}
}
