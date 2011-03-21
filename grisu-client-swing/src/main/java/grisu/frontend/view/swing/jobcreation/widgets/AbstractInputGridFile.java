package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.X;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.virtual.GridFileTreeDialog;
import grisu.model.GrisuRegistry;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

abstract public class AbstractInputGridFile extends AbstractWidget {

	public static final String FOLDER_SELECTABLE = "folder_selectable";
	public static final String EXTENSIONS_TO_DISPLAY = "extensions_to_display";
	public static final String DISPLAY_HIDDEN_FILES = "display_hidden_files";

	private GridFileTreeDialog fileDialog = null;

	// public final String selString = "Please select a file";
	private boolean displayHiddenFiles = false;
	private String[] extensions = null;

	private List<GridFile> roots = null;

	private String currentUrl = null;

	private boolean lockCombo = false;

	private JComboBox comboBox;
	protected final DefaultComboBoxModel fileModel = new DefaultComboBoxModel();
	private int selectionMode = ListSelectionModel.SINGLE_SELECTION;
	private boolean foldersSelectable = true;
	private final boolean displayLocalFilesystems = true;

	private static final Map<ServiceInterface, GridFileComboboxRenderer> RENDERERS = new HashMap<ServiceInterface, GridFileComboboxRenderer>();
	/**
	 * The key of a List of {@link GridFile}s that can be retrieved via:
	 * {@link GrisuRegistry#get(String)}.
	 */
	public static final String ROOTS = "roots";

	public static GridFileComboboxRenderer getRenderer(ServiceInterface si,
			ListCellRenderer lr) {

		if (RENDERERS.get(si) == null) {
			GridFileComboboxRenderer r = new GridFileComboboxRenderer(si, lr);
			RENDERERS.put(si, r);
		}
		return RENDERERS.get(si);
	}

	public AbstractInputGridFile() {
		super();
	}

	public void askForFile() {
		GridFile f = popupFileDialogAndAskForFile();

		if (f != null) {
			setInputFile(f);

			if (StringUtils.isNotBlank(getHistoryKey())) {
				getHistoryManager().addHistoryEntry(
						getHistoryKey() + "_last_file", f.getUrl());
			}

		}
	}

	protected GridFileTreeDialog getFileDialog() {

		if (fileDialog == null) {

			fileDialog = createGridFileDialog(getServiceInterface(), roots,
					getHistoryKey() + "_last_dir", extensions,
					displayHiddenFiles, foldersSelectable,
					displayLocalFilesystems,
					SwingUtilities.getWindowAncestor(this));
		}

		return fileDialog;

	}

	public GridFile getInputFile() {
		final GridFile temp = (GridFile) getInputFileComboBox()
		.getSelectedItem();
		return temp;
	}

	protected JComboBox getInputFileComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(fileModel);
			fileModel.addElement("");
			comboBox.setEditable(false);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.DESELECTED) {
						return;
					}


					Object sel = fileModel.getSelectedItem();

					GridFile file = null;
					if (sel instanceof String) {
						String s = (String)sel;
						if (StringUtils.isBlank(s)) {
							currentUrl = null;
							return;
						}

						try {
							file = getFileManager().createGridFile(s);
							if (!getFileManager().fileExists(file)) {
								throw new Exception("File " + file.getUrl()
										+ " does not exist.");
							}
						} catch (Exception ex) {
							X.p("Display error message: "
									+ ex.getLocalizedMessage());
							return;
						}

					}
					else if ((sel instanceof GridFile)) {
						file = (GridFile) sel;
					}

					if (file == null) {
						X.p("Display error message2");
						return;
					}


					if (!file.getUrl().equals(currentUrl)) {

						setInputFile(file);
						// getPropertyChangeSupport().firePropertyChange(
						// "inputFileUrl", currentUrl, getValue());
						currentUrl = getValue();

					}

				}

			});
		}

		return comboBox;
	}

	public String getInputFileUrl() {
		try {
			final GridFile temp = (GridFile) getInputFileComboBox()
			.getSelectedItem();

			return temp.getUrl();
		} catch (Exception e) {
			myLogger.error(e);
			return null;
		}
	}

	@Override
	public String getValue() {
		return getInputFileUrl();
	}

	@Override
	public void historyKeySet() {
		getHistoryManager().setMaxNumberOfEntries(getHistoryKey(), 8);
		for (final String entry : getHistoryManager().getEntries(
				getHistoryKey())) {

			GridFile temp = null;
			try {
				temp = getFileManager().createGridFile(entry);
				if (temp.isFolder()) {
					temp.setIsInaccessible(true);
				}
			} catch (Exception e) {
				temp = new GridFile(entry, true, e);
			}

			if (fileModel.getIndexOf(temp) < 0) {
				fileModel.addElement(temp);
			}

		}
	}

	protected GridFile popupFileDialogAndAskForFile() {

		if (getServiceInterface() == null) {
			getMylogger().error(
			"ServiceInterface not set. Can't open dialog...");
			return null;
		}

		getFileDialog().setVisible(true);

		final GridFile file = getFileDialog().getSelectedFile();

		return file;
	}

	public void setConfiguration(Map<String, String> config) {

		if (config == null) {
			return;
		}

		for (String key : config.keySet()) {

			if (FOLDER_SELECTABLE.equals(key)) {
				try {
					boolean s = Boolean.parseBoolean(config.get(key));
					setFoldersSelectable(s);
				} catch (Exception e) {
					myLogger.warn("Can't parse value of " + key + ": "
							+ config.get(key));
					continue;
				}
			} else if (EXTENSIONS_TO_DISPLAY.equals(key)) {
				setExtensionsToDisplay(config.get(key).split(","));
			} else if (DISPLAY_HIDDEN_FILES.equals(key)) {
				try {
					boolean display = Boolean.parseBoolean(config.get(key));
					setDisplayHiddenFiles(display);
				} catch (Exception e) {
					myLogger.warn("Can't parse value of " + key + ": "
							+ config.get(key));
					continue;
				}
			} else if (ROOTS.equals(key)) {
				try {
					List<GridFile> roots = (List<GridFile>) (GrisuRegistryManager
							.getDefault(getServiceInterface()).get(config.get(key)));
					if (roots != null) {
						setRoots(roots);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void setDisplayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
		if (fileDialog != null) {
			fileDialog.displayHiddenFiles(display);
		}
	}

	public void setExtensionsToDisplay(String[] extensions) {
		this.extensions = extensions;
		if (fileDialog != null) {
			fileDialog.setExtensionsToDisplay(extensions);
		}
	}

	public void setFoldersSelectable(boolean foldersSelectable) {
		this.foldersSelectable = foldersSelectable;
		if (fileDialog != null) {
			fileDialog.setFoldersSelectable(foldersSelectable);
		}
	}

	public void setInputFile(GridFile file) {

		lockCombo = true;
		if (file == null) {
			// fileModel.setSelectedItem(selString);
			fileModel.setSelectedItem("");
			return;
		}

		final int index = fileModel.getIndexOf(file);
		if (index < 0) {
			fileModel.addElement(file);
		}
		fileModel.setSelectedItem(file);
		getInputFileComboBox().setToolTipText(file.getUrl());
		lockCombo = false;

		getPropertyChangeSupport().firePropertyChange("inputFile", null, file);

		if ( StringUtils.isNotBlank(getHistoryKey()) ) {

			getHistoryManager().addHistoryEntry(getHistoryKey(), file.getUrl());

		}

	}

	public void setInputFileUrl(String fileUrl)
	throws RemoteFileSystemException {

		setInputFile(getFileManager().createGridFile(fileUrl));

	}

	@Override
	protected boolean setLastValue() {
		return false;
	}

	public void setRoots(List<GridFile> roots) {
		this.roots = roots;
		this.fileDialog = null;
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
		if (fileDialog != null) {
			fileDialog.setSelectionMode(selectionMode);
		}
	}

	@Override
	public void setServiceInterface(ServiceInterface si) {
		super.setServiceInterface(si);
		getInputFileComboBox().setRenderer(
				getRenderer(getServiceInterface(), getInputFileComboBox()
						.getRenderer()));
	}

	@Override
	public void setValue(String value) {

		GridFile f;
		try {
			f = getFileManager().createGridFile(value);
		} catch (RemoteFileSystemException e) {
			throw new RuntimeException(e);
		}
		setInputFile(f);

	}

}
