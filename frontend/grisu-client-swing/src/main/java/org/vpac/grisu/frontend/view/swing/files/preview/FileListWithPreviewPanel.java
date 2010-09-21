package org.vpac.grisu.frontend.view.swing.files.preview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.FileDetailPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListActionPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListListener;
import org.vpac.grisu.frontend.view.swing.files.FileListPanel;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelContextMenu;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelPlus;
import org.vpac.grisu.frontend.view.swing.files.FileListPanelSimple;
import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileListWithPreviewPanel extends JPanel implements FileListPanel,
		FileListListener {

	static final Logger myLogger = Logger
			.getLogger(FileListWithPreviewPanel.class.getName());

	public static final String PREVIEW = "Preview";
	public static final String FILE_LIST = "File list";

	private JSplitPane splitPane;
	private GenericFileViewer genericFileViewer;
	private JPanel panel;
	private FileListPanel fileListPanel;
	private FileDetailPanel fileDetailPanel;
	private JPanel rightPanel;
	private JPanel rightCardPanel;

	private final ServiceInterface si;
	private final String rootUrl;
	private final String startUrl;
	private FileListActionPanel fileListActionPanel;
	private FileListActionPanel fileListActionPanelRight;

	private FileListPanel fileListPanelRight;
	private JPanel rightFileListWrapperPanel;

	private boolean useFileListPanelPlus = false;
	private boolean displayFileDetailsPanel = false;
	private boolean displayFileActionPanel = false;
	private boolean useSplitPane = true;

	private final int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private JLabel label;
	private JComboBox comboBox;

	/**
	 * @wbp.parser.constructor
	 */
	public FileListWithPreviewPanel(ServiceInterface si) {
		this(si, null, null, false, false, true, true, true);
	}

	/**
	 * @param si
	 * @param rootUrl
	 * @param startUrl
	 * @param useAdvancedFileListPanel
	 * @param displayFileActionPanel
	 * @param displayFileDetailsPanel
	 * @param useSplitPane
	 * @param startWithRightFileList
	 *            only applies when useSplitPane is true
	 */
	public FileListWithPreviewPanel(ServiceInterface si, String rootUrl,
			String startUrl, boolean useAdvancedFileListPanel,
			boolean displayFileActionPanel, boolean displayFileDetailsPanel,
			boolean useSplitPane, boolean startWithRightFileList) {

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
			if (startWithRightFileList) {
				getComboBox().setSelectedItem(FILE_LIST);
			} else {
				getComboBox().setSelectedItem(PREVIEW);
			}
		} else {
			add(getRootPanel(), BorderLayout.CENTER);
		}

	}

	public void addFileListListener(FileListListener l) {

		getFileListPanel().addFileListListener(l);

	}

	public void directoryChanged(GlazedFile newDirectory) {

	}

	public void displayHiddenFiles(boolean display) {
		fileListPanel.displayHiddenFiles(display);
		fileListPanelRight.displayHiddenFiles(display);
	}

	public void fileDoubleClicked(final GlazedFile file) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				if (useSplitPane) {
					getComboBox().setSelectedItem(PREVIEW);
				} else {

					final FilePreviewDialog dialog = new FilePreviewDialog(si);
					dialog.setFile(file, null);
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});
	}

	public void filesSelected(Set<GlazedFile> files) {
		revalidate();
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {

					if (ItemEvent.SELECTED == e.getStateChange()) {
						setRightPanel((String) comboBox.getSelectedItem());
					}

				}
			});
			comboBox.setModel(new DefaultComboBoxModel(new String[] { PREVIEW,
					FILE_LIST }));
		}
		return comboBox;
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

	private FileListActionPanel getFileListActionPanelRight() {
		if (fileListActionPanelRight == null) {
			fileListActionPanelRight = new FileListActionPanel(si,
					getFileListPanelRight());
		}
		return fileListActionPanelRight;
	}

	private FileListPanel getFileListPanel() {
		if (fileListPanel == null) {
			if (useFileListPanelPlus) {
				fileListPanel = new FileListPanelPlus(si, startUrl, true, false);
			} else {
				fileListPanel = new FileListPanelSimple(si, rootUrl, startUrl,
						true, false);
			}
			if (useSplitPane) {
				fileListPanel.addFileListListener(getGenericFileViewer());
			}
			fileListPanel.addFileListListener(this);

			if (displayFileDetailsPanel) {
				fileListPanel.addFileListListener(getFileDetailPanel());
			}
		}
		return fileListPanel;
	}

	private FileListPanel getFileListPanelRight() {
		if (fileListPanelRight == null) {
			if (useFileListPanelPlus) {
				fileListPanelRight = new FileListPanelPlus(si, startUrl, true,
						false);
			} else {
				fileListPanelRight = new FileListPanelSimple(si, rootUrl,
						startUrl, true, false);
			}

		}
		return fileListPanelRight;
	}

	private GenericFileViewer getGenericFileViewer() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericFileViewer();
			genericFileViewer.setServiceInterface(si);
		}
		return genericFileViewer;
	}

	public JPanel getPanel() {
		return this;
	}

	public JPanel getRightCardPanel() {

		if (rightCardPanel == null) {

			rightCardPanel = new JPanel();
			rightCardPanel.setLayout(new CardLayout());
			rightCardPanel.add(getGenericFileViewer(), PREVIEW);
			rightCardPanel.add(getRightFileListWrapperPanel(), FILE_LIST);
		}
		return rightCardPanel;
	}

	private JPanel getRightFileListWrapperPanel() {

		if (rightFileListWrapperPanel == null) {
			rightFileListWrapperPanel = new JPanel();
			rightFileListWrapperPanel.setLayout(new BorderLayout());
			rightFileListWrapperPanel.add(getFileListActionPanelRight(),
					BorderLayout.NORTH);
			rightFileListWrapperPanel.add(getFileListPanelRight().getPanel(),
					BorderLayout.CENTER);
		}
		return rightFileListWrapperPanel;

	}

	private JPanel getRightPanel() {

		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
					.decode("default:grow"), }, new RowSpec[] {
					RowSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, }));
			rightPanel.add(getRightCardPanel(), "1, 1, fill, fill");
			rightPanel.add(getComboBox(), "1, 3, right, default");
		}
		return rightPanel;
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
			splitPane.setRightComponent(getRightPanel());
			splitPane.setLeftComponent(getRootPanel());
			// splitPane.setDividerLocation(340 + splitPane.getInsets().left);
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

	public void setContextMenu(FileListPanelContextMenu menu) {

		myLogger.warn("Setting of context-menu for FileListWithPreviewPanel not supported for right panel...");
		getFileListPanel().setContextMenu(menu);

	}

	public void setCurrentUrl(String url) {

		getFileListPanel().setCurrentUrl(url);
	}

	public void setExtensionsToDisplay(String[] extensions) {
		fileListPanel.setExtensionsToDisplay(extensions);
		fileListPanelRight.setExtensionsToDisplay(extensions);

	}

	private void setRightPanel(String panel) {

		if (PREVIEW.equals(panel) || FILE_LIST.equals(panel)) {
			final CardLayout cl = (CardLayout) (getRightCardPanel().getLayout());
			cl.show(getRightCardPanel(), panel);
		}

	}

	public void setRootUrl(String url) {

		getFileListPanel().setRootUrl(url);

	}
}
