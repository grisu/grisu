package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;
import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.frontend.control.utils.ApplicationsManager;
import grisu.frontend.model.events.NewJobEvent;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXTaskPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class GrisuMonitorNavigationTaskPane extends JXTaskPane implements
		EventSubscriber<NewJobEvent> {
	
	private class ApplicationComparator implements Comparator<String> {

		public int compare(String arg0, String arg1) {
			
			if (Constants.ALLJOBS_KEY.equals(arg0) && Constants.ALLJOBS_KEY.equals(arg1)) {
				return 0;
			}
			if (Constants.ALLJOBS_KEY.equals(arg0) ) {
				return -1;
			} 
			if (Constants.ALLJOBS_KEY.equals(arg1) ) {
				return 1;
			} 
			return arg0.compareToIgnoreCase(arg1);
		}
		
	}

	static final Logger myLogger = LoggerFactory
			.getLogger(GrisuMonitorNavigationTaskPane.class.getName());

	public static final String SINGLE_JOB_LIST = "single_list";

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final GrisuNavigationPanel navPanel;

	private boolean displayAllJobsItem = true;
	private boolean displayApplicationSpecificItems = true;

	private final Map<String, Action> actions = new HashMap<String, Action>();

	private final Set<String> applicationsToWatch;

	/**
	 * Create the panel.
	 */
	public GrisuMonitorNavigationTaskPane(ServiceInterface si,
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
		setTitle("Monitor jobs");
		
		updateApplications();
		



		EventBus.subscribe(NewJobEvent.class, this);
	}

	public void addApplication(final String application) {

		final String app;

		if (Constants.GENERIC_APPLICATION_NAME
				.equals(application.toLowerCase())) {
			myLogger.debug("Not adding monitor generic application to navigation pane. Using all jobs instead...");
			if (! displayAllJobsItem ) {
				return;
			}
			app = Constants.ALLJOBS_KEY;
		} else {
			app = application.toLowerCase();
		}

		if (Constants.ALLJOBS_KEY.equals(app)) {
			RunningJobManager.getDefault(si).getAllJobs();
		}

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
					myLogger.debug("Action command: " + e.getActionCommand());
					myLogger.debug("Application: " + application);

					navPanel.setNavigationCommand(new String[] {
							SINGLE_JOB_LIST, application });
				}

				public String getApplication() {
					return this.application;
				}
			};
			actions.put(app, temp);
			add(temp);
		}
	}

	public void onEvent(NewJobEvent event) {

		if (displayApplicationSpecificItems) {

			String application = event.getJob().getApplication();

			if (StringUtils.isBlank(application)) {
				try {
					application = si.getJobProperty(
							event.getJob().getJobname(),
							Constants.APPLICATIONNAME_KEY);
				} catch (final Exception e) {
					myLogger.error(e.getLocalizedMessage(), e);
					return;
				}
			}

			if (StringUtils.isBlank(application)) {
				return;
			}
			
			addApplication(application);

		}

	}
	
	private void updateApplications() {
		List<String> applications = Lists.newArrayList();
		
		if (displayAllJobsItem) {
			applications.add(Constants.ALLJOBS_KEY);
		}

		if ((applicationsToWatch != null) && (applicationsToWatch.size() > 0)) {
			for (final String app : applicationsToWatch) {
				applications.add(app);
			}
		} else {

		if (displayApplicationSpecificItems || !displayAllJobsItem) {
			for (final String app : em.getCurrentApplications(true)) {

				if ( ! Constants.GENERIC_APPLICATION_NAME.equals(app) ) {
					applications.add(app);
				}

			}
		}
		}

		Collections.sort(applications, new ApplicationComparator());
		
		for ( String app : applications ) {
			addApplication(app);
		}
	}

//	private void updateApplications() {
//
//		for (final String app : em.getCurrentApplications(true)) {
//
//			if ( ! Constants.GENERIC_APPLICATION_NAME.equals(app) ) {
//				addApplication(app);
//			}
//
//		}
//
//	}

}
