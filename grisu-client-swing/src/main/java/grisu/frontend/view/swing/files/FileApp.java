package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.files.preview.FileListWithPreviewPanel;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FileApp {



	private JFrame frame;

	private FileListPanelPlus fileListPanelPlus;
	private FileListWithPreviewPanel flwpp;
	private final ServiceInterface si;

	private final String startUrl;

	/**
	 * Create the application.
	 */
	public FileApp(ServiceInterface si, String startUrl) {
		this.si = si;
		this.startUrl = startUrl;

		initialize();
	}

	private FileListPanelPlus getFileListPanelPlus() {
		if (fileListPanelPlus == null) {
			fileListPanelPlus = new FileListPanelPlus(si, startUrl, true, true);
		}
		return fileListPanelPlus;
	}

	private FileListWithPreviewPanel getFileListWithPreviewPanel() {
		if (flwpp == null) {
			flwpp = new FileListWithPreviewPanel(si, null, startUrl, true,
					false, false, false, true);
		}
		return flwpp;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		// frame.getContentPane().add(getFileListPanelPlus(),
		// BorderLayout.CENTER);
		frame.getContentPane().add(getFileListWithPreviewPanel(),
				BorderLayout.CENTER);
	}
}
