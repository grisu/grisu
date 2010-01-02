package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.JobDetailPanel;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.SingleJobSelectionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobPanel extends JPanel implements SingleJobSelectionListener {

	private final ServiceInterface si;
	private final BatchJobObject batchJob;
	private JSplitPane splitPane;
	private BatchJobSubJobsGrid batchJobSubJobsGrid;
	private JobDetailPanel jobDetailPanel;
	private BatchJobStatusPanel batchJobControlPanel;
	private BatchJobRestartPanel batchJobRestartPanel;

	/**
	 * Create the panel.
	 */
	public BatchJobPanel(ServiceInterface si, BatchJobObject batchJob) {
		this.si = si;
		this.batchJob = batchJob;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(95dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getBatchJobControlPanel(), "2, 2, fill, fill");
		add(getBatchJobRestartPanel(), "4, 2, fill, fill");
		add(getSplitPane(), "2, 4, 3, 1, fill, fill");
	}

	private BatchJobStatusPanel getBatchJobControlPanel() {
		if (batchJobControlPanel == null) {
			batchJobControlPanel = new BatchJobStatusPanel(batchJob);
		}
		return batchJobControlPanel;
	}

	private BatchJobRestartPanel getBatchJobRestartPanel() {
		if (batchJobRestartPanel == null) {
			batchJobRestartPanel = new BatchJobRestartPanel(batchJob);
		}
		return batchJobRestartPanel;
	}

	private JobDetailPanel getJobDetailPanel() {
		if (jobDetailPanel == null) {
			jobDetailPanel = new JobDetailPanel(si);
		}
		return jobDetailPanel;
	}

	private BatchJobSubJobsGrid getSingleJobsGrid_1() {
		if (batchJobSubJobsGrid == null) {
			batchJobSubJobsGrid = new BatchJobSubJobsGrid(si, batchJob);
			batchJobSubJobsGrid.addJobSelectionListener(this);
		}
		return batchJobSubJobsGrid;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getSingleJobsGrid_1());
			splitPane.setRightComponent(getJobDetailPanel());
			splitPane.setDividerLocation(380 + splitPane.getInsets().left);

		}
		return splitPane;
	}
	public void jobSelected(JobObject job) {

		getJobDetailPanel().setJob(job);

	}
}
