package org.vpac.grisu.frontend.view.swing.files.preview;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.FileDetailPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListActionPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListListener;
import org.vpac.grisu.frontend.view.swing.files.FileListPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelPlus;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelSimple;
import org.vpac.grisu.model.files.GlazedFile;

public class FileListWithPreviewPanel extends JPanel implements FileListPanel,
		FileListListener {

	private JSplitPane splitPane;
	private GenericFileViewer genericFileViewer;
	private JPanel panel;
	private FileListPanel fileListPanel;
	private FileDetailPanel fileDetailPanel;

	private final ServiceInterface si;
	private final String rootUrl;
	private final String startUrl;
	private FileListActionPanel fileListActionPanel;

	private boolean useFileListPanelPlus = false;
	private boolean displayFileDetailsPanel = false;
	private boolean displayFileActionPanel = false;
	private boolean useSplitPane = true;

	private final int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public FileListWithPreviewPanel(ServiceInterface si) {
		this(si, null, null, false, false, true, true);
	}

	public FileListWithPreviewPanel(ServiceInterface si, String rootUrl,
			String startUrl, boolean useAdvancedFileListPanel,
			boolean displayFileActionPanel, boolean displayFileDetailsPanel,
			boolean useSplitPane) {

		this.si = si;
		this.useFileListPanelPlus = useAdvancedFileListPanel;
		this.displayFileActionPanel = displayFileActionPanel;
		this.displayFileDetailsPanel = displayFileDetailsPanel;
		this.useSplitPane = useSplitPane;
		this.rootUrl = rootUrl;
		this.startUrl = startUrl;

		setLayout(new BorderLayout(0, 0));

		if (useSplitPane) {
			add(getSplitPane(), BorderLayout.CENTER);
		} else {
			add(getRootPanel(), BorderLayout.CENTER);
		}
	}

	public void addFileListListener(FileListListener l) {

		getFileListPanel().addFileListListener(l);

	}

	public void fileDoubleClicked(GlazedFile file) {

		FilePreviewDialog dialog = new FilePreviewDialog(si);
		dialog.setFile(file, null);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

	}

	public void filesSelected(Set<GlazedFile> files) {
	}

	public GlazedFile getCurrentDirectory() {
		return getFileListPanel().getCurrentDirectory();
	}

	private FileDetailPanel getFileDetailPanel() {
		if (fileDetailPanel == null) {
			fileDetailPanel = new FileDetailPanel();
		}
		return fileDetailPanel;
	}

	private FileListActionPanel getFileListActionPanel() {
		if (fileListActionPanel == null) {
			fileListActionPanel = new FileListActionPanel(si,
					getFileListPanel());
		}
		return fileListActionPanel;
	}

	private FileListPanel getFileListPanel() {
		if (fileListPanel == null) {
			if (useFileListPanelPlus) {
				fileListPanel = new FileListPanelPlus(si, rootUrl, startUrl,
						true, false);
			} else {
				fileListPanel = new FileListPanelSimple(si, rootUrl, startUrl,
						true, false);
			}
			if (useSplitPane) {
				fileListPanel.addFileListListener(getGenericFileViewer());
			} else {
				fileListPanel.addFileListListener(this);
			}
			if (displayFileDetailsPanel) {
				fileListPanel.addFileListListener(getFileDetailPanel());
			}
		}
		return fileListPanel;
	}

	private GenericFileViewer getGenericFileViewer() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericFileViewer(si);
		}
		return genericFileViewer;
	}

	public JPanel getPanel() {
		return this;
	}

	private JPanel getRootPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getFileListPanel().getPanel(), BorderLayout.CENTER);
			if (displayFileDetailsPanel) {
				panel.add(getFileDetailPanel(), BorderLayout.SOUTH);
			}
			if (displayFileActionPanel) {
				panel.add(getFileListActionPanel(), BorderLayout.NORTH);
			}
		}
		return panel;
	}

	public Set<GlazedFile> getSelectedFiles() {
		return getFileListPanel().getSelectedFiles();
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setOrientation(splitOrientation);
			splitPane.setRightComponent(getGenericFileViewer());
			splitPane.setLeftComponent(getRootPanel());
			splitPane.setDividerLocation(280 + splitPane.getInsets().left);
		}
		return splitPane;
	}

	public void isLoading(boolean loading) {
	}

	public void refresh() {
		getFileListPanel().refresh();
	}

	public void removeFileListListener(FileListListener l) {
		getFileListPanel().removeFileListListener(l);
	}

	public void setCurrentUrl(String url) {

		getFileListPanel().setCurrentUrl(url);
	}

	public void setRootUrl(String url) {

		getFileListPanel().setRootUrl(url);

	}
}
