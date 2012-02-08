package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.control.ServiceInterface;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.view.swing.files.FileListListener;
import grisu.frontend.view.swing.files.preview.GenericFileViewer;
import grisu.model.files.GlazedFile;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class BatchJobResultPreviewPanel extends JPanel implements
		FileListListener {

	private ServiceInterface si;
	private JSplitPane splitPane;
	private JPanel panel;
	private BatchDownloadResultPanel batchDownloadResultPanel;
	private JPanel panel_1;
	private GenericFileViewer genericFileViewer;

	/**
	 * Create the panel.
	 */
	public BatchJobResultPreviewPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getSplitPane(), BorderLayout.CENTER);

	}

	public void directoryChanged(GlazedFile newDirectory) {
	}

	public void fileDoubleClicked(GlazedFile file) {

		getGenericFileViewer_1().setFile(file, null);

	}

	public void filesSelected(Set<GlazedFile> files) {

	}

	private BatchDownloadResultPanel getBatchDownloadResultPanel() {
		if (batchDownloadResultPanel == null) {
			batchDownloadResultPanel = new BatchDownloadResultPanel(true, true);
			batchDownloadResultPanel.addFileListListener(this);
		}
		return batchDownloadResultPanel;
	}

	private GenericFileViewer getGenericFileViewer_1() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericFileViewer();
		}
		return genericFileViewer;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getBatchDownloadResultPanel(), BorderLayout.CENTER);
		}
		return panel;
	}

	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(getGenericFileViewer_1(), BorderLayout.CENTER);
		}
		return panel_1;
	}

	// public void initialize(BatchJobObject batchJobObject, String[] patterns)
	// {
	// getBatchDownloadResultPanel().setBatchJobAndPatterns(batchJobObject,
	// patterns);
	// }
	//
	// public void setBatchJob(BatchJobObject batchJob) {
	// getBatchDownloadResultPanel().setBatchJob(batchJob);
	// }
	//
	// public void setPatterns(String[] patterns) {
	// getBatchDownloadResultPanel().setPatterns(patterns);
	// }
	//
	// public void setServiceInterface(ServiceInterface si) {
	// this.si = si;
	// getBatchDownloadResultPanel().setServiceInterface(si);
	// }
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getPanel());
			splitPane.setRightComponent(getPanel_1());
		}
		return splitPane;
	}

	public void initialize(BatchJobObject batchJob, String[] patterns) {
		getBatchDownloadResultPanel()
				.setBatchJobAndPatterns(batchJob, patterns);
	}

	public void isLoading(boolean loading) {

		// getGenericFileViewer_1().setFile(null, null);

	}

	public void setServiceInterface(ServiceInterface si2) {
		getBatchDownloadResultPanel().setServiceInterface(si2);
		getGenericFileViewer_1().setServiceInterface(si2);
	}
}
