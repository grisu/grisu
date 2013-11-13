package grisu.frontend.view.swing.files.open;

import com.google.common.collect.Maps;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.GridFileSelectionDialog;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileDialogManager {

	public class OpenFileAction extends AbstractAction {

		private final FileDialogManager fdm;
		private final GridFileHolder gfh;
		private final String dialogAlias;

	    public OpenFileAction(FileDialogManager fdm, GridFileHolder h, String dialogAlias) {
	        super();
	        this.fdm = fdm;
	        this.gfh = h;
	        this.dialogAlias = dialogAlias;
	        putValue(SMALL_ICON, new ImageIcon(FileDialogManager.class.getResource("/sun/print/resources/oneside.png")));
	        putValue(NAME, "Open");
	        putValue(SHORT_DESCRIPTION, "Open file");
	    }
	    public void actionPerformed(ActionEvent e) {
	        GridFile file = this.fdm.popupFileDialogAndAskForFile(dialogAlias);
	        gfh.setFile(file);
	    }
	}

	public static Component defaultRootComponent = null;

	public static final String FILE_DIALOG_KEY = "file_dialog";


	static final Logger myLogger = LoggerFactory
			.getLogger(FileDialogManager.class.getName());

	private static Map<ServiceInterface, FileDialogManager> cachedRegistries = new HashMap<ServiceInterface, FileDialogManager>();

	public static FileDialogManager getDefault(final ServiceInterface si) {
		return getDefault(si, null);
	}

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

	public static FileDialogManager getDefault(final ServiceInterface si, Component component) {

		if (si == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default filedialog registry...");
		}

		synchronized (si) {
			if (cachedRegistries.get(si) == null) {
				final FileDialogManager m = new FileDialogManager(si, component);
				cachedRegistries.put(si, m);
			}
		}

		return cachedRegistries.get(si);
	}

	private final ServiceInterface si;
	private static Map<String, GridFileSelectionDialog> dialogs = Maps
			.newConcurrentMap();

	private final Window rootComponent;

	public FileDialogManager(ServiceInterface si, Component comp) {
		this.si = si;
		if ( comp == null ) {
			comp = defaultRootComponent;
		}
		this.rootComponent = SwingUtilities.getWindowAncestor(comp);
	}

	public GridFileSelectionDialog getFileDialog(String dialogName) {

		if (dialogs.get(dialogName) == null) {
			if (si == null) {
				throw new IllegalStateException(
						"File dialog not initialized yet.");
			}
			createSingletonFileDialog(rootComponent,
					si, dialogName);
		}
		return dialogs.get(dialogName);
	}

	public GridFile popupFileDialogAndAskForFile(String alias) {

		getFileDialog(alias).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        getFileDialog(alias).setLocationRelativeTo(rootComponent);
		getFileDialog(alias).setVisible(true);

		final GridFile file = getFileDialog(alias).getSelectedFile();

		return file;
	}

	public OpenFileAction createOpenAction(GridFileHolder gfh, String dialogAlias) {
		OpenFileAction oa = new OpenFileAction(this, gfh, dialogAlias);
		return oa;
	}

}
