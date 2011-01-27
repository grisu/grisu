package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.layout.FormLayout;

public class FileListPanelGroupView extends JPanel implements FileListPanel {

	/**
	 * Create the panel.
	 */
	public FileListPanelGroupView() {
		setLayout(new FormLayout());
	}

	public void addFileListListener(FileListListener l) {
		// TODO Auto-generated method stub

	}

	public void displayHiddenFiles(boolean display) {
		// TODO Auto-generated method stub

	}

	public GlazedFile getCurrentDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	public JPanel getPanel() {
		return this;
	}

	public Set<GlazedFile> getSelectedFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void removeFileListListener(FileListListener l) {
		// TODO Auto-generated method stub

	}

	public void setContextMenu(FileListPanelContextMenu menu) {
		// TODO Auto-generated method stub

	}

	public void setCurrentUrl(String url) {
		// TODO Auto-generated method stub

	}

	public void setExtensionsToDisplay(String[] extensions) {
		// TODO Auto-generated method stub

	}

	public void setRootUrl(String url) {
		// TODO Auto-generated method stub

	}

}
