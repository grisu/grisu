package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.GridFileListListener;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;

public class GridFileManagementPanel extends JPanel implements
		GridFileListListener {
	private JideSplitPane jideSplitPane;

	private final ServiceInterface si;

	private GridFileTreePanel fileListPanel;
	private ListAndPreviewFilePanel listAndPreviewPanel;

	private final List<GridFile> leftRoots;
	private final List<GridFile> rightRoots;

	public GridFileManagementPanel(ServiceInterface si) {
		this(si, null, null);
	}

	public GridFileManagementPanel(ServiceInterface si,
			List<GridFile> leftRoots, List<GridFile> rightRoots) {

		super();
		this.si = si;
		if (leftRoots == null) {
			GridFile gridRoot = GrisuRegistryManager.getDefault(si)
					.getFileManager().getGridRoot();
			GridFile localRoot = GrisuRegistryManager.getDefault(si)
					.getFileManager().getLocalRoot();
			this.leftRoots = new LinkedList<GridFile>();
			this.leftRoots.add(gridRoot);
			this.leftRoots.add(localRoot);
		} else {
			this.leftRoots = leftRoots;
		}

		if (rightRoots == null) {
			GridFile gridRoot = GrisuRegistryManager.getDefault(si)
					.getFileManager().getGridRoot();
			GridFile localRoot = GrisuRegistryManager.getDefault(si)
					.getFileManager().getLocalRoot();
			this.rightRoots = new LinkedList<GridFile>();
			this.rightRoots.add(gridRoot);
			this.rightRoots.add(localRoot);
		} else {
			this.rightRoots = leftRoots;
		}
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

		// for (GridFile f : files) {
		// X.p("Selected file on left panel: " + f.getName());
		// }

	}

	private GridFileTreePanel getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new GridFileTreePanel(si, leftRoots, true);
			fileListPanel.addGridFileListListener(this);
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
			listAndPreviewPanel = new ListAndPreviewFilePanel(si, rightRoots);
		}
		return listAndPreviewPanel;
	}

	public void isLoading(boolean loading) {

		// nothing to do here

	}
}
