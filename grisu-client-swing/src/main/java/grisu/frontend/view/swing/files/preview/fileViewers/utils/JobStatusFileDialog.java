package grisu.frontend.view.swing.files.preview.fileViewers.utils;

import grisu.frontend.view.swing.files.preview.fileViewers.JobStatusGridFileViewer;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class JobStatusFileDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JobStatusFileDialog dialog = new JobStatusFileDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();

	private JobStatusGridFileViewer fileViewer;

	/**
	 * Create the dialog.
	 */
	public JobStatusFileDialog() {
		setModal(false);
		setBounds(100, 100, 620, 528);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getCsvFileViewer(), BorderLayout.CENTER);
	}

	private JobStatusGridFileViewer getCsvFileViewer() {
		if (fileViewer == null) {
			fileViewer = new JobStatusGridFileViewer();
		}
		return fileViewer;
	}

	public void setFile(GridFile file, File localCacheFile) {
		setTitle("Job status");
		getCsvFileViewer().setFile(file, localCacheFile);
	}

	public void setFileManagerAndUrl(FileManager fm, String url) {
		getCsvFileViewer().setFileManagerAndUrl(fm, url);
	}

}
