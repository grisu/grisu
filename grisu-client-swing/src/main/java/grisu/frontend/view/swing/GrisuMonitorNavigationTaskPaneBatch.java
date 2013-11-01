package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;
import grisu.frontend.control.utils.ApplicationsManager;
import grisu.frontend.model.events.NewBatchJobEvent;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GrisuMonitorNavigationTaskPaneBatch extends JXTaskPane implements
		EventSubscriber<NewBatchJobEvent> {

	public static final String BATCH_JOB_LIST = "batch_list";

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final Map<String, Action> actions = new HashMap<String, Action>();

	private boolean displayAllJobsItem = true;
	private boolean displayApplicationSpecificItems = true;

	private final Set<String> applicationsToWatch;

	private final GrisuNavigationPanel navPanel;

	/**
	 * Create the panel.
	 */
	public GrisuMonitorNavigationTaskPaneBatch(ServiceInterface si,
			GrisuNavigationPanel navPanel, boolean displayAllJobsItem,
			boolean displayApplicationSpecificItems,
			Set<String> applicationsToWatch) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();

		this.applicationsToWatch = applicationsToWatch;
		this.navPanel = navPanel;
		this.displayAllJobsItem = displayAllJobsItem;
		this.displayApplicationSpecificItems = displayApplicationSpecificItems;
		setTitle("Monitor batchjobs");

		if (displayAllJobsItem) {
			addApplication(Constants.ALLJOBS_KEY);
		}

		if (displayApplicationSpecificItems) {
			updateApplications();
		}

		if ((applicationsToWatch != null) && (applicationsToWatch.size() > 0)) {
			for (final String app : applicationsToWatch) {
				addApplication(app);
			}
		}
	}

	public void addApplication(final String application) {

		final String app = application.toLowerCase();

//		if (Constants.ALLJOBS_KEY.equals(app)) {
//			RunningJobManagerOld.getDefault(si).getAllBatchJobs();
//		}

		if (actions.get(app) == null) {
			final Action temp = new AbstractAction() {

				private final String application = app;
				{
					putValue(Action.NAME,
							ApplicationsManager.getPrettyName(app));
					putValue(Action.SHORT_DESCRIPTION,
							ApplicationsManager.getShortDescription(app));
					putValue(Action.SMALL_ICON,
							ApplicationsManager.getIcon(app));
				}

				public void actionPerformed(ActionEvent e) {
					navPanel.setNavigationCommand(new String[] {
							BATCH_JOB_LIST, application });
				}

				public String getApplication() {
					return this.application;
				}
			};
			actions.put(app, temp);
			add(temp);
		}
	}

	public void onEvent(NewBatchJobEvent event) {

		if (displayApplicationSpecificItems) {
			addApplication(event.getBatchJob().getApplication());
		}

	}

	private void updateApplications() {

		for (final String app : em.getCurrentApplicationsBatch(true)) {

			addApplication(app);

		}

	}

}
