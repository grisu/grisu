package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.textarea.StandaloneTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class GridFileTextEditPanel extends JPanel {
	
	public static final Logger myLogger = LoggerFactory.getLogger(GridFileTextEditPanel.class);
	private static final long serialVersionUID = 1L;
	
	public static final String FILE_DIALOG_KEY = "file_dialog";
	public static final String DEFAULT_FILE_DIALOG_ALIAS = "default";
	
	private static Map<String, GridFileSelectionDialog> dialogs = Maps
			.newConcurrentMap();
	
	private synchronized static void createSingletonFileDialog(Window owner,
			ServiceInterface si, String dialogAlias) {

		if (dialogs.get(dialogAlias) == null) {
			String startUrl = GrisuRegistryManager
					.getDefault(si)
					.getHistoryManager()
					.getLastEntry(
							dialogAlias + "_" + FILE_DIALOG_KEY);

			if (StringUtils.isBlank(startUrl)) {
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			} else if (!FileManager.isLocal(startUrl)) {
				try {
					if (!si.isFolder(startUrl)) {
						startUrl = new File(System.getProperty("user.home"))
						.toURI().toString();
					}
				} catch (final RemoteFileSystemException e) {
					myLogger.debug("Can't load file: "+startUrl, e);
					startUrl = new File(System.getProperty("user.home"))
					.toURI().toString();
				}
			}
			final GridFileSelectionDialog dialog = new GridFileSelectionDialog(
					owner, si);

			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialogs.put(dialogAlias, dialog);
		}
	}
	
	private StandaloneTextArea textArea;
	private boolean documentUnsaved = false;
	private ServiceInterface si;
	private FileManager fm;
	
	private String fileDialogAlias = null;
	
	private GridFile currentFile;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public GridFileSelectionDialog getFileDialog(String dialogName) {

		if (dialogs.get(dialogName) == null) {
			if (si == null) {
				throw new IllegalStateException(
						"File dialog not initialized yet.");
			}
			createSingletonFileDialog(SwingUtilities.getWindowAncestor(this),
					si, dialogName);
		}
		return dialogs.get(dialogName);
	}
	
	public GridFileTextEditPanel() {
		this(DEFAULT_FILE_DIALOG_ALIAS);
	}

	/**
	 * Create the panel.
	 */
	public GridFileTextEditPanel(String fileDialogAlias) {
		setLayout(new BorderLayout(0, 0));
		add(getStandaloneTextArea(), BorderLayout.CENTER);
		
		this.fileDialogAlias = fileDialogAlias;
	}
	
	public String getFileDialogAlias() {
		return fileDialogAlias;
	}
	
	public void setFileDialogAlias(String alias) {
		this.fileDialogAlias = alias;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}
	
	public GridFileSelectionDialog getFileDialog() {
		return getFileDialog(fileDialogAlias);
	}
	
	public void askUserForFile() {
		
		GridFile file = popupFileDialogAndAskForFile();
		
		setFile(file);
		
	}
	
	protected GridFile popupFileDialogAndAskForFile() {

		getFileDialog().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getFileDialog().centerOnOwner();
		getFileDialog().setVisible(true);

		final GridFile file = getFileDialog().getSelectedFile();

		// final Set<GridFile> currentDirs =
		// getFileDialog().getCurrentDirectories();
		// if (currentDirs != null) {
		//
		// hm.addHistoryEntry(templateName + "_" +
		// FILE_DIALOG_LAST_DIRECTORY_KEY,
		// currentDir.getUrl());
		// }

		return file;
	}
	
	public void setTextMode(String modeName) {
		final Mode mode = new Mode(modeName);
		mode.setProperty("file", mode + ".xml");
		ModeProvider.instance.addMode(mode);
		getStandaloneTextArea().getBuffer().setMode(mode);
	}

	private StandaloneTextArea getStandaloneTextArea() {
		if (textArea == null) {
			textArea = StandaloneTextArea.createTextArea();
			
			final Mode mode = new Mode("text");
			mode.setProperty("file", "text.xml");
			ModeProvider.instance.addMode(mode);
			textArea.getBuffer().setMode(mode);
			

			//textArea.setRightClickPopup(po);
			JPopupMenu p = new JPopupMenu();
			textArea.setRightClickPopup(p);
			
			textArea.addMenuItem("undo", "Undo");
			textArea.addMenuItem("redo", "Redo");
			p.addSeparator();
			textArea.addMenuItem("cut", "Cut");
			textArea.addMenuItem("copy", "Copy");
			textArea.addMenuItem("paste", "Paste");
			
			textArea.setRightClickPopupEnabled(true);

			textArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if ( documentUnsaved == false ) {
						documentUnsaved = true;
						pcs.firePropertyChange("documentUnsaved", false, true);
					}
				}

			});
		}
		return textArea;
	}
	
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(
				si).getFileManager();

	}
	
	public void setFile(String path_or_url) throws RemoteFileSystemException {
		
		if ( si == null ) {
			myLogger.debug("TextEditPanel not initialized yet, not loading file: "+path_or_url);
		}
		
		GridFile file = fm.createGridFile(path_or_url);
		setFile(file);
	}
	
	private void setTextAreaText(String text) {
		if ( text == null ) {
			text = "";
		}
		getStandaloneTextArea().setText(text);

	}
	
	public String getText() {
		return getStandaloneTextArea().getText();
	}

	
	public void setFile(GridFile file) {
		
		if ( file == null ) {
			return;
		}
		
		if ( si == null ) {
			myLogger.debug("TextEditPanel not initialized yet, not loading file: "+file.getUrl());
		}
		

		File localfile = null;
		try {
			localfile = fm.downloadFile(file.getUrl());
		} catch (final FileTransactionException e1) {
			myLogger.error("Can't download file: "+file.getUrl(), e1);
			return;
		}

		String text;
		try {
			text = FileUtils.readFileToString(localfile);
		} catch (final IOException e) {
			myLogger.error("Can't read file to string: "+localfile.getAbsolutePath());
			return;
		}

		setTextAreaText(text);
		
		if ( documentUnsaved == true ) {
			documentUnsaved = false;
			pcs.firePropertyChange("documentUnsaved", true, false);
		}	
		
		GridFile old = currentFile;
		currentFile = file;
		pcs.firePropertyChange("currentFile", currentFile, old);

	}
	
	public GridFile getFile() {
		return currentFile;
	}
	
	public boolean isDocumentUnsaved() {
		return documentUnsaved;
	}
}
