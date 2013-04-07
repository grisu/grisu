package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.view.swing.files.open.FileDialogManager;
import grisu.frontend.view.swing.files.open.GridFileHolder;
import grisu.frontend.view.swing.utils.DisabledGlassPane;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.textarea.StandaloneTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridFileTextEditPanel extends JPanel implements GridFileHolder {

	public static final Logger myLogger = LoggerFactory
			.getLogger(GridFileTextEditPanel.class);
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_FILE_DIALOG_ALIAS = "default";

	private GrisuTextArea textArea;
	private boolean documentUnsaved = false;
	private ServiceInterface si;
	private FileManager fm;
	private FileDialogManager fdm;

	private String fileDialogAlias = null;

	private GridFile currentFile;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private DisabledGlassPane glassPane = new DisabledGlassPane();

	/**
	 * Create the panel.
	 */
	public GridFileTextEditPanel(String fileDialogAlias, JFrame frame) {
		setLayout(new BorderLayout(0, 0));

		frame.setGlassPane(glassPane);

		add(getStandaloneTextArea(), BorderLayout.CENTER);

		this.fileDialogAlias = fileDialogAlias;
	}

	public void activateGlassPane(String msg) {
		glassPane.activate(msg);
	}

	public void deactivateGlassPane() {
		glassPane.deactivate();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void askUserForFile() {

		GridFile file = fdm.popupFileDialogAndAskForFile(fileDialogAlias);

		setFile(file);

	}

	public GridFile getFile() {
		return currentFile;
	}

	public GridFileSelectionDialog getFileDialog() {

		return fdm.getFileDialog(fileDialogAlias);
	}

	public String getFileDialogAlias() {
		return fileDialogAlias;
	}

	private StandaloneTextArea getStandaloneTextArea() {
		if (textArea == null) {
			textArea = new GrisuTextArea();

			textArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (documentUnsaved == false) {
						documentUnsaved = true;
						pcs.firePropertyChange("documentUnsaved", false, true);
					}
				}

			});
		}
		return textArea;
	}

	public String getText() {
		return getStandaloneTextArea().getText();
	}

	public boolean isDocumentUnsaved() {
		return documentUnsaved;
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void setFile(final GridFile file) {

		if (file == null) {
			return;
		}

		if (si == null) {
			myLogger.debug("TextEditPanel not initialized yet, not loading file: "
					+ file.getUrl());
		}

		activateGlassPane("Loading file: " + file.getName());

		Thread t = new Thread() {
			public void run() {

				try {

					File localfile = null;
					try {
						localfile = fm.downloadFile(file.getUrl());
					} catch (final FileTransactionException e1) {
						myLogger.error("Can't download file: " + file.getUrl(),
								e1);
						return;
					}

					String text;
					try {
						text = FileUtils.readFileToString(localfile);
					} catch (final IOException e) {
						myLogger.error("Can't read file to string: "
								+ localfile.getAbsolutePath());
						return;
					}

					setTextAreaText(text);

					if (documentUnsaved == true) {
						documentUnsaved = false;
						pcs.firePropertyChange("documentUnsaved", true, false);
					}

					GridFile old = currentFile;
					currentFile = file;
					pcs.firePropertyChange("currentFile", currentFile, old);

				} finally {
					deactivateGlassPane();
				}
			}
		};
		t.setName("OpenFileDownloadThread");
		t.start();

	}

	public void setFile(String path_or_url) throws RemoteFileSystemException {

		if (si == null) {
			myLogger.debug("TextEditPanel not initialized yet, not loading file: "
					+ path_or_url);
		}

		GridFile file = fm.createGridFile(path_or_url);
		setFile(file);
	}

	public void setFileDialogAlias(String alias) {
		this.fileDialogAlias = alias;
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.fdm = FileDialogManager.getDefault(si, this);

	}

	private void setTextAreaText(String text) {
		if (text == null) {
			text = "";
		}
		getStandaloneTextArea().setText(text);

	}

	public void setTextMode(String modeName) {
		final Mode mode = new Mode(modeName);
		mode.setProperty("file", mode + ".xml");
		ModeProvider.instance.addMode(mode);
		getStandaloneTextArea().getBuffer().setMode(mode);
	}
}
