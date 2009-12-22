package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobPanel extends JPanel implements SingleJobSelectionListener {

	private final ServiceInterface si;
	private final BatchJobObject batchJob;
	private JLabel label;
	private JSplitPane splitPane;
	private SimpleSingleJobsGrid singleJobsGrid_1;
	private JobDetailPanel jobDetailPanel;
	private BatchJobStatusPanel batchJobControlPanel;

	/**
	 * Create the panel.
	 */
	public BatchJobPanel(ServiceInterface si, BatchJobObject batchJob) {
		this.si = si;
		this.batchJob = batchJob;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLabel(), "2, 2");
		add(getBatchJobControlPanel(), "2, 4, fill, fill");
		add(getSplitPane(), "2, 6, fill, fill");
	}

	private BatchJobStatusPanel getBatchJobControlPanel() {
		if (batchJobControlPanel == null) {
			batchJobControlPanel = new BatchJobStatusPanel(batchJob);
		}
		return batchJobControlPanel;
	}

	private JobDetailPanel getJobDetailPanel() {
		if (jobDetailPanel == null) {
			jobDetailPanel = new JobDetailPanel(si);
		}
		return jobDetailPanel;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("New label");
		}
		return label;
	}

	private SimpleSingleJobsGrid getSingleJobsGrid_1() {
		if (singleJobsGrid_1 == null) {
			singleJobsGrid_1 = new SimpleSingleJobsGrid(si, batchJob.getJobs());
			singleJobsGrid_1.addJobSelectionListener(this);
		}
		return singleJobsGrid_1;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getSingleJobsGrid_1());
			splitPane.setRightComponent(getJobDetailPanel());
			splitPane.setDividerLocation(260 + splitPane.getInsets().left);

		}
		return splitPane;
	}

	public void jobSelected(JobObject job) {

		getJobDetailPanel().setJob(job);

	}
}
