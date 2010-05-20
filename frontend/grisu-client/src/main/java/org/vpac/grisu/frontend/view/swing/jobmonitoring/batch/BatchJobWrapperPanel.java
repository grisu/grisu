package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobWrapperPanel extends JPanel {
	private JTabbedPane tabbedPane;
	private BatchJobResultPreviewPanel batchJobResultPreviewPanel;
	private BatchJobPanel batchJobPanel;

	private final ServiceInterface si;
	private final BatchJobObject batchJob;
	private final String[] patterns;

	/**
	 * Create the panel.
	 */
	public BatchJobWrapperPanel(ServiceInterface si, BatchJobObject batchJob,
			String[] patterns) {
		this.si = si;
		this.batchJob = batchJob;
		this.patterns = patterns;
		setLayout(new BorderLayout(0, 0));
		add(getTabbedPane(), BorderLayout.CENTER);

	}

	private BatchJobPanel getBatchJobPanel() {
		if (batchJobPanel == null) {
			batchJobPanel = new BatchJobPanel(si, batchJob);
		}
		return batchJobPanel;
	}

	private BatchJobResultPreviewPanel getBatchJobResultPreviewPanel() {
		if (batchJobResultPreviewPanel == null) {
			batchJobResultPreviewPanel = new BatchJobResultPreviewPanel();
			batchJobResultPreviewPanel.setServiceInterface(si);
			batchJobResultPreviewPanel.initialize(batchJob, patterns);
		}
		return batchJobResultPreviewPanel;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Results", null, getBatchJobResultPreviewPanel(),
					null);
			tabbedPane.addTab("BatchJob", null, getBatchJobPanel(), null);
		}
		return tabbedPane;
	}
}
