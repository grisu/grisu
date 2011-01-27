package org.vpac.grisu.frontend.view.swing.files.preview;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.files.GlazedFile;

public class FilePreviewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final FilePreviewDialog dialog = new FilePreviewDialog(null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private final ServiceInterface si;

	private GenericFileViewer genericFileViewer;

	/**
	 * Create the dialog.
	 */
	public FilePreviewDialog(ServiceInterface si) {
		setModal(false);
		this.si = si;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getGenericFileViewer(), BorderLayout.CENTER);
	}

	private GenericFileViewer getGenericFileViewer() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericFileViewer();
			genericFileViewer.setServiceInterface(si);
		}
		return genericFileViewer;
	}

	public void setFile(GlazedFile file, File localCacheFile) {
		getGenericFileViewer().setFile(file, localCacheFile);
	}
}
