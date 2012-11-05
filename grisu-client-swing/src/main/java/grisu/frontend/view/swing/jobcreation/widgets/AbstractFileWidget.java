package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.frontend.view.swing.files.GridFileSelectionDialog;
import grisu.model.dto.GridFile;

import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

abstract class AbstractFileWidget extends AbstractWidget {
	
	public static final String FOLDER_SELECTABLE = "folder_selectable";
	public static final String EXTENSIONS_TO_DISPLAY = "extensions_to_display";
	public static final String DISPLAY_HIDDEN_FILES = "display_hidden_files";
	
	private GridFileSelectionDialog fileDialog = null;

	private boolean displayFiles = true;
	private boolean displayHiddenFiles = false;
	private String[] extensions = null;
	
	private List<GridFile> roots = null;
	
	private int selectionMode = ListSelectionModel.SINGLE_SELECTION;
	private boolean foldersSelectable = true;
	
	public AbstractFileWidget() {
		
	}
	
	protected Set<GridFile> popupFileDialogAndAskForFiles() {

		if (getServiceInterface() == null) {
			getMylogger().error(
					"ServiceInterface not set. Can't open dialog...");
			return null;
		}

		getFileDialog().setVisible(true);

		final Set<GridFile> files = getFileDialog().getSelectedFiles();

		return files;
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
	
	public GridFileSelectionDialog getFileDialog() {

		if (fileDialog == null) {

			fileDialog = createGridFileDialog(getServiceInterface(), roots,
					getHistoryKey() + "_last_dir", extensions, displayFiles,
					displayHiddenFiles, foldersSelectable, selectionMode,
					SwingUtilities.getWindowAncestor(this));
		}

		return fileDialog;

	}
	
	public void setFileDialog(GridFileSelectionDialog d) {
		this.fileDialog = d;
	}
	
	public void setDisplayFiles(boolean display) {
		this.displayFiles = display;
		if (fileDialog != null) {
			fileDialog.setDisplayFiles(display);
		}
	}

	public void setDisplayHiddenFiles(boolean display) {
		this.displayHiddenFiles = display;
		if (fileDialog != null) {
			fileDialog.setDisplayHiddenFiles(display);
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



}
