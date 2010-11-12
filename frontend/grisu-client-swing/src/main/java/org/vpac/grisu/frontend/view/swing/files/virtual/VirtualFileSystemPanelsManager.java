package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.GridFileListListener;
import org.vpac.grisu.model.dto.GridFile;

import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;

public class VirtualFileSystemPanelsManager extends JPanel implements
		GridFileListListener {
	private JideSplitPane jideSplitPane;

	private final ServiceInterface si;

	private VirtualFileSystemTreeTablePanel fileListPanel;
	private ListAndPreviewFilePanel listAndPreviewPanel;

	public VirtualFileSystemPanelsManager(ServiceInterface si) {
		super();
		this.si = si;
		setLayout(new BorderLayout(0, 0));
		add(getJideSplitPane(), BorderLayout.CENTER);
	}

	public void directoryChanged(GridFile newDirectory) {
		// nothing to do
	}

	public void fileDoubleClicked(GridFile file) {
		getListAndPreviewPanel().displayFile(file);
	}

	public void filesSelected(Set<GridFile> files) {

		for (GridFile f : files) {
			X.p("Selected file on left panel: " + f.getName());
		}

	}

	private VirtualFileSystemTreeTablePanel getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new VirtualFileSystemTreeTablePanel(si);
			fileListPanel.addFileListListener(this);
		}
		return fileListPanel;
	}

	private JideSplitPane getJideSplitPane() {
		if (jideSplitPane == null) {
			jideSplitPane = new JideSplitPane(JideSplitPane.HORIZONTAL_SPLIT);
			jideSplitPane.setProportionalLayout(true);
			jideSplitPane.add(getFileListPanel(), JideBoxLayout.FLEXIBLE);
			jideSplitPane.add(getListAndPreviewPanel(), JideBoxLayout.FLEXIBLE);
		}
		return jideSplitPane;
	}

	private ListAndPreviewFilePanel getListAndPreviewPanel() {
		if (listAndPreviewPanel == null) {
			listAndPreviewPanel = new ListAndPreviewFilePanel(si);
		}
		return listAndPreviewPanel;
	}

	public void isLoading(boolean loading) {

		// nothing to do here

	}
}
