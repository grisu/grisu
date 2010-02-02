package org.vpac.grisu.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXTaskPane;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.utils.ApplicationsManager;
import org.vpac.grisu.frontend.model.events.NewJobEvent;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

import au.org.arcs.jcommons.constants.Constants;

public class GrisuMonitorNavigationTaskPane extends JXTaskPane implements EventSubscriber<NewJobEvent>{

	public static final String SINGLE_JOB_LIST = "single_list";

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final GrisuNavigationPanel navPanel;

	private boolean displayAllJobsItem = true;
	private boolean displayApplicationSpecificItems = true;

	private final Map<String, Action> actions = new HashMap<String, Action>();

	/**
	 * Create the panel.
	 */
	public GrisuMonitorNavigationTaskPane(ServiceInterface si, GrisuNavigationPanel navPanel, boolean displayAllJobsItem, boolean displayApplicationSpecificItems) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();
		this.navPanel = navPanel;
		this.displayAllJobsItem = displayAllJobsItem;
		this.displayApplicationSpecificItems = displayApplicationSpecificItems;
		setTitle("Running jobs");

		if ( displayAllJobsItem ) {
			addApplication(Constants.ALLJOBS_KEY);
		}

		if ( displayApplicationSpecificItems ) {
			updateApplications();
		}
	}

	public void addApplication(final String app) {

		if ( actions.get(app) == null ) {
			Action temp = new AbstractAction() {

				private final String application = app;
				{
					putValue(Action.NAME, ApplicationsManager.getPrettyName(app));
					putValue(Action.SHORT_DESCRIPTION, ApplicationsManager.getShortDescription(app));
					putValue(Action.SMALL_ICON, ApplicationsManager.getIcon(app));
				}

				public void actionPerformed(ActionEvent e) {
					System.out.println("Action command: "+e.getActionCommand());
					System.out.println("Application: "+application);

					navPanel.setNavigationCommand(new String[]{SINGLE_JOB_LIST, application});
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

		if ( displayApplicationSpecificItems ) {
			addApplication(event.getJob().getApplication());
		}

	}

	private void updateApplications() {

		for ( final String app : em.getCurrentApplications(true) ) {

			addApplication(app);

		}

	}

}
