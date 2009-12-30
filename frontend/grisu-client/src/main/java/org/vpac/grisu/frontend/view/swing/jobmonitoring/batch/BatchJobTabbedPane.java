package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

import com.jidesoft.swing.JideTabbedPane;

public class BatchJobTabbedPane extends JPanel implements
		BatchJobSelectionListener {

	private JideTabbedPane jideTabbedPane;
	private BatchJobMonitoringGrid grid;

	private Map<String, BatchJobPanel> panels = new HashMap<String, BatchJobPanel>();

	private final ServiceInterface si;
	private final String application;

	/**
	 * Create the panel.
	 */
	public BatchJobTabbedPane(ServiceInterface si, String application) {
		this.si = si;
		this.application = application;
		setLayout(new BorderLayout(0, 0));
		add(getJideTabbedPane(), BorderLayout.CENTER);
		addBatchJobSelectionListener(this);
	}

	// register a listener
	public void addBatchJobSelectionListener(BatchJobSelectionListener l) {
		getGrid().addBatchJobSelectionListener(l);
	}

	public void batchJobSelected(BatchJobObject bj) {

		BatchJobPanel temp = panels.get(bj.getJobname());
		if (panels.get(bj.getJobname()) == null) {
			temp = new BatchJobPanel(si, bj);
			panels.put(bj.getJobname(), temp);
		}

		try {
			getJideTabbedPane().setSelectedComponent(temp);
		} catch (IllegalArgumentException e) {
			getJideTabbedPane().addTab(bj.getJobname(), temp);
			getJideTabbedPane().setSelectedComponent(temp);
		}

	}

	private BatchJobMonitoringGrid getGrid() {

		if (grid == null) {
			grid = new BatchJobMonitoringGrid(si, application);
		}
		return grid;
	}

	private JideTabbedPane getJideTabbedPane() {
		if (jideTabbedPane == null) {
			jideTabbedPane = new JideTabbedPane();
			jideTabbedPane.setHideOneTab(true);
			jideTabbedPane.setShowCloseButtonOnTab(true);
			jideTabbedPane.setCloseTabOnMouseMiddleButton(true);
			jideTabbedPane.setTabClosableAt(0, false);
			String title = null;
			if (StringUtils.isBlank(application)) {
				title = "All batchjobs";
			} else {
				title = application + " jobs";
			}
			jideTabbedPane.addTab(title, getGrid());

		}
		return jideTabbedPane;
	}

	// remove a listener
	synchronized public void removeBatchJobSelectionListener(
			BatchJobSelectionListener l) {
		getGrid().removeBatchJobSelectionListener(l);
	}
}
