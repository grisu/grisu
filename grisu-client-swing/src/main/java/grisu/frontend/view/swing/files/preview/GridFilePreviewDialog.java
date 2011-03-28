package grisu.frontend.view.swing.files.preview;

import grisu.control.ServiceInterface;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.WindowConstants;


public class GridFilePreviewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final GridFilePreviewDialog dialog = new GridFilePreviewDialog(null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private final ServiceInterface si;

	private GenericGridFileViewer genericFileViewer;

	/**
	 * Create the dialog.
	 */
	public GridFilePreviewDialog(ServiceInterface si) {
		setModal(false);
		this.si = si;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getGenericFileViewer(), BorderLayout.CENTER);
	}


	private GenericGridFileViewer getGenericFileViewer() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericGridFileViewer();
			genericFileViewer.setServiceInterface(si);
		}
		return genericFileViewer;
	}

	public void setFile(GridFile file, File localCacheFile) {
		setTitle("Preview: " + file.getName());
		getGenericFileViewer().setFile(file, localCacheFile);
	}
}
