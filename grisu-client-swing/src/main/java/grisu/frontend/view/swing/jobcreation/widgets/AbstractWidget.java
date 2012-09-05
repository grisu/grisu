package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.swing.files.GridFileSelectionDialog;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vpac.historyRepeater.HistoryManager;

public abstract class AbstractWidget extends JPanel {

	static final Logger myLogger = LoggerFactory
			.getLogger(SingleInputFile.class.getName());

	public static GridFileSelectionDialog createFileDialog(ServiceInterface si,
			String historyKey, String[] extensions, boolean displayHiddenFiles,
			Window owner) {

		if (si == null) {
			return null;
		}

		String startUrl = GrisuRegistryManager.getDefault(si)
				.getHistoryManager().getLastEntry(historyKey);

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
				myLogger.debug(e.getLocalizedMessage(), e);
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			}
		}
		final GridFileSelectionDialog fileDialog = new GridFileSelectionDialog(
				owner, si);
		fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		fileDialog.setExtensionsToDisplay(extensions);
		fileDialog.setDisplayHiddenFiles(displayHiddenFiles);

		fileDialog.centerOnOwner();

		return fileDialog;
	}

	public static GridFileSelectionDialog createGridFileDialog(
			ServiceInterface si,
			List<GridFile> roots, String historyKey,
			String[] extensions, boolean displayFiles,
			boolean displayHiddenFiles, boolean foldersSelectable,
			int selectionMode, Window owner) {

		if (si == null) {
			return null;
		}

		String startUrl = GrisuRegistryManager.getDefault(si)
				.getHistoryManager().getLastEntry(historyKey);

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
				myLogger.debug(e.getLocalizedMessage(), e);
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			}
		}

		final GridFileSelectionDialog fileDialog = new GridFileSelectionDialog(
				owner, si);
		fileDialog.setDisplayHiddenFiles(displayHiddenFiles);
		fileDialog.setDisplayFiles(displayFiles);
		fileDialog.setExtensionsToDisplay(extensions);
		fileDialog.setFoldersSelectable(foldersSelectable);
		if (selectionMode >= 0) {
			fileDialog.setSelectionMode(selectionMode);
		}

		fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		return fileDialog;
	}

	public static Logger getMylogger() {
		return myLogger;
	}

	private PropertyChangeSupport pcs = null;

	private ServiceInterface si;
	private FileManager fm;

	private HistoryManager hm;

	private String historyKey = null;

	private String title = null;

	public AbstractWidget() {
		super();
	}

	public void addWidgetListener(PropertyChangeListener l) {
		getPropertyChangeSupport().addPropertyChangeListener(l);
	}

	public FileManager getFileManager() {
		return this.fm;
	}

	public String getHistoryKey() {
		return historyKey;
	}

	public HistoryManager getHistoryManager() {
		return hm;
	}

	protected PropertyChangeSupport getPropertyChangeSupport() {
		if (pcs == null) {
			pcs = new PropertyChangeSupport(this);
		}
		return pcs;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	public String getTitle() {
		return this.title;
	}

	abstract public String getValue();

	/**
	 * Can be overridden if necessary.
	 */
	public void historyKeySet() {

	}

	private void initHistory() {

		final String lastValue = hm.getLastEntry(this.historyKey);
		if (StringUtils.isBlank(lastValue)) {
			return;
		}

		historyKeySet();

		if (setLastValue()) {
			try {
				setValue(lastValue);
			} catch (final Exception e) {
				return;
			}
		}

	}

	public void lockUI(final boolean lock) {

		final Component[] comps = getComponents();
		for (final Component comp : comps) {
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					comp.setEnabled(!lock);
				}
			});
		}

	}

	public void removeWidgetListener(PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(l);
	}

	public void saveItemToHistory() {
		if ((hm != null) && StringUtils.isNotBlank(historyKey)) {
			final String temp = getValue();
			if (StringUtils.isNotBlank(temp)) {
				// System.out.println("Adding: " + this.getClass().toString());
				hm.addHistoryEntry(this.historyKey, temp);
			}
		}
	}

	public void setHistoryKey(String key) {

		this.historyKey = key;

		if (si != null) {
			initHistory();
		}

	}

	/**
	 * Can be overwritten.
	 * 
	 * @param set
	 *            whether to set the last used value or not
	 */
	protected boolean setLastValue() {
		return true;
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.hm = GrisuRegistryManager.getDefault(si).getHistoryManager();
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

		if (historyKey != null) {
			initHistory();
		}
	}
	public void setTitle(String title) {
		this.title = title;
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
	}

	/**
	 * Can be overwritten.
	 * 
	 * @param group
	 */
	public void setValidationGroup(ValidationGroup group) {
	}

	abstract public void setValue(String value);

}
