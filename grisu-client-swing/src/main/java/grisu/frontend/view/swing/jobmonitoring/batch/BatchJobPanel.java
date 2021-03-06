package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.control.ServiceInterface;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.GrisuJob;
import grisu.frontend.view.swing.jobmonitoring.single.JobDetailPanel;
import grisu.frontend.view.swing.jobmonitoring.single.SingleJobSelectionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
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
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(95dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC, }));
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
			// jobDetailPanel = new JobDetailPanelSmall();
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
			splitPane.setRightComponent(getJobDetailPanel().getPanel());
			splitPane.setDividerLocation(380 + splitPane.getInsets().left);

		}
		return splitPane;
	}

	public void jobSelected(GrisuJob job) {

		getJobDetailPanel().setJob(job);

	}
}
