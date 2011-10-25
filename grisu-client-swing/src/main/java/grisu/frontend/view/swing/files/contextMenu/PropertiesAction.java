package grisu.frontend.view.swing.files.contextMenu;

import grisu.frontend.control.fileTransfers.FileTransactionManager;
import grisu.frontend.view.swing.files.GridFileListPanel;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PropertiesAction extends AbstractAction {
	private final GridFileListPanel fileList;
	private final FileManager fm;
	private final FileTransactionManager ftm;

	public PropertiesAction(GridFileListPanel fileList) {
		this.fileList = fileList;
		this.fm = GrisuRegistryManager.getDefault(
				fileList.getServiceInterface()).getFileManager();
		this.ftm = FileTransactionManager.getDefault(fileList
				.getServiceInterface());
		putValue(NAME, "Properties");
		putValue(SHORT_DESCRIPTION, "Display file properties");
	}

	public void actionPerformed(final ActionEvent e) {

		final Set<GridFile> files = fileList.getSelectedFiles();
		if ((files == null) || (files.size() <= 0)) {
			return;
		}

		for (final GridFile f : files) {

			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					JFrame parent = null;
					try {
						final Component c = (Component) e.getSource();
						parent = (JFrame) SwingUtilities.getRoot(c);
					} catch (final Exception eee) {
					}

					final GridFilePropertiesDialog dialog = new GridFilePropertiesDialog(
							parent);
					dialog.setGridFile(f);

				}
			});

		}
	}
}
