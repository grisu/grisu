package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.control.utils.ApplicationsManager;
import org.vpac.grisu.frontend.model.job.JobObject;

import com.jidesoft.swing.JideTabbedPane;

public class SingleJobTabbedPane extends JPanel implements
		SingleJobSelectionListener {

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
		add(getUpdateButton(), BorderLayout.SOUTH);
		addSingleJobSelectionListener(this);
	}

	// register a listener
	public void addSingleJobSelectionListener(SingleJobSelectionListener l) {
		getGrid().addJobSelectionListener(l);
	}

	private JButton getUpdateButton() {
		if (updateButton == null) {
			updateButton = new JButton("Update");
			updateButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					RunningJobManager.getDefault(si).updateJobList(null);

				}
			});
		}
		return updateButton;
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

	public void jobSelected(JobObject bj) {

		JobDetailPanel temp = panels.get(bj.getJobname());
		if (panels.get(bj.getJobname()) == null) {
			temp = new JobDetailPanelDefault(si, bj);
			panels.put(bj.getJobname(), temp);
		}

		try {
			getJideTabbedPane().setSelectedComponent(temp.getPanel());
		} catch (IllegalArgumentException e) {
			getJideTabbedPane().addTab(bj.getJobname(), temp.getPanel());
			getJideTabbedPane().setSelectedComponent(temp.getPanel());
		}

	}

	// remove a listener
	synchronized public void removeSingleJobSelectionListener(
			SingleJobSelectionListener l) {
		getGrid().removeJobSelectionListener(l);
	}
}
