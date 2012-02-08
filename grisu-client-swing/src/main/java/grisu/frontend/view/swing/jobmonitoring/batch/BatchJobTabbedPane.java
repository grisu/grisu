package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.utils.ApplicationsManager;
import grisu.frontend.model.job.BatchJobObject;
import grisu.jcommons.constants.Constants;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import com.jidesoft.swing.JideTabbedPane;

public class BatchJobTabbedPane extends JPanel implements
		BatchJobSelectionListener {

	private JideTabbedPane jideTabbedPane;
	private BatchJobMonitoringGrid grid;

	private final Map<String, JPanel> panels = new HashMap<String, JPanel>();

	private final ServiceInterface si;
	private final String application;
	private BatchJobResultPreviewPanel batchJobResultPreviewPanel;

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

		JPanel temp = panels.get(bj.getJobname());
		if (panels.get(bj.getJobname()) == null) {

			String patternString = null;
			try {
				patternString = si.getJobProperty(bj.getJobname(),
						Constants.JOB_RESULT_FILENAME_PATTERNS);
			} catch (final NoSuchJobException e) {
				// doesn't matter
			}

			if (StringUtils.isNotBlank(patternString)) {
				final String[] patterns = patternString.split(",");
				temp = new BatchJobWrapperPanel(si, bj, patterns);
			} else {
				temp = new BatchJobPanel(si, bj);
			}

			panels.put(bj.getJobname(), temp);
		}

		try {
			getJideTabbedPane().setSelectedComponent(temp);
		} catch (final IllegalArgumentException e) {
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
			jideTabbedPane.setHideOneTab(false);
			jideTabbedPane.setShowCloseButtonOnTab(true);
			jideTabbedPane.setCloseTabOnMouseMiddleButton(true);
			jideTabbedPane.setTabClosableAt(0, false);

			String title = null;
			if (StringUtils.isBlank(application)) {
				title = "All batchjobs";
			} else {
				title = ApplicationsManager.getPrettyName(application)
						+ " jobs";
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
