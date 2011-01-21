package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.control.utils.ApplicationsManager;
import org.vpac.grisu.frontend.model.events.JobCleanedEvent;
import org.vpac.grisu.frontend.model.job.JobObject;

import com.jidesoft.swing.JideTabbedPane;

public class SingleJobTabbedPane extends JPanel implements
		SingleJobSelectionListener, EventSubscriber<JobCleanedEvent> {

	private JideTabbedPane jideTabbedPane;
	private SimpleSingleJobsGrid grid;

	private final Map<String, JobDetailPanel> panels = new HashMap<String, JobDetailPanel>();

	private final ServiceInterface si;
	private final String application;
	private JButton updateButton;

	/**
	 * Create the panel.
	 */
	public SingleJobTabbedPane(ServiceInterface si, String application) {
		this.si = si;
		this.application = application;
		setLayout(new BorderLayout(0, 0));
		add(getJideTabbedPane(), BorderLayout.CENTER);
		addSingleJobSelectionListener(this);
		EventBus.subscribe(JobCleanedEvent.class, this);
	}

	// register a listener
	public void addSingleJobSelectionListener(SingleJobSelectionListener l) {
		getGrid().addJobSelectionListener(l);
	}

	private SimpleSingleJobsGrid getGrid() {

		if (grid == null) {
			grid = new SimpleSingleJobsGrid(si, application);
		}
		return grid;
	}

	private JideTabbedPane getJideTabbedPane() {
		if (jideTabbedPane == null) {
			jideTabbedPane = new JideTabbedPane();
			jideTabbedPane.setHideOneTab(false);
			jideTabbedPane.setShowCloseButtonOnTab(true);
			jideTabbedPane.setCloseTabOnMouseMiddleButton(true);
			jideTabbedPane.setTabClosableAt(0, false);
			String title = null;
			if (StringUtils.isBlank(application)) {
				title = "All jobs";
			} else {
				title = ApplicationsManager.getPrettyName(application)
						+ " jobs";
			}
			jideTabbedPane.addTab(title, getGrid());

		}
		return jideTabbedPane;
	}

	private JButton getUpdateButton() {
		if (updateButton == null) {
			updateButton = new JButton("Update");
			updateButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					RunningJobManager.getDefault(si).updateJobnameList(null);

				}
			});
		}
		return updateButton;
	}

	public void jobSelected(JobObject bj) {

		JobDetailPanel temp = panels.get(bj.getJobname());
		if (panels.get(bj.getJobname()) == null) {
			temp = new JobDetailPanelDefault(si, bj);
			panels.put(bj.getJobname(), temp);
		}

		try {
			getJideTabbedPane().setSelectedComponent(temp.getPanel());
		} catch (final IllegalArgumentException e) {
			getJideTabbedPane().addTab(bj.getJobname(), temp.getPanel());
			getJideTabbedPane().setSelectedComponent(temp.getPanel());
		}

	}

	public void onEvent(JobCleanedEvent arg0) {

		// System.out.println("Removing panel...");
		JobObject bj = arg0.getJob();
		JobDetailPanel temp = panels.get(bj.getJobname());
		X.p("Cleaned: " + arg0.getJob().getJobname());
		if (panels.get(bj.getJobname()) != null) {
			getJideTabbedPane().setSelectedIndex(0);
			panels.remove(bj.getJobname());
			getJideTabbedPane().remove(temp.getPanel());
		}

	}

	// remove a listener
	synchronized public void removeSingleJobSelectionListener(
			SingleJobSelectionListener l) {
		getGrid().removeJobSelectionListener(l);
	}
}
