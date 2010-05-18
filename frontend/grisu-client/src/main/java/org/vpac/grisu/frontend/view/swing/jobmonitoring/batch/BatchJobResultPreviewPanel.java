package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobResultPreviewPanel extends JPanel {

	private BatchDownloadResultPanel batchDownloadResultPanel;

	private ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public BatchJobResultPreviewPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), }));
		add(getBatchDownloadResultPanel(), "2, 2, fill, fill");

	}

	private BatchDownloadResultPanel getBatchDownloadResultPanel() {
		if (batchDownloadResultPanel == null) {
			batchDownloadResultPanel = new BatchDownloadResultPanel(true, true);
		}
		return batchDownloadResultPanel;
	}

	public void setBatchJob(BatchJobObject batchJob) {
		getBatchDownloadResultPanel().setBatchJob(batchJob);
	}

	public void setPatterns(String[] patterns) {
		getBatchDownloadResultPanel().setPatterns(patterns);
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		getBatchDownloadResultPanel().setServiceInterface(si);
	}
}
