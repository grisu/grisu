package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.view.swing.files.GrisuFileDialog;
import org.vpac.grisu.frontend.view.swing.files.virtual.GridFileTreeDialog;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.historyRepeater.HistoryManager;

public abstract class AbstractWidget extends JPanel {

	static final Logger myLogger = Logger.getLogger(SingleInputFile.class
			.getName());

	public static GrisuFileDialog createFileDialog(ServiceInterface si,
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
				myLogger.debug(e);
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			}
		}
		final GrisuFileDialog fileDialog = new GrisuFileDialog(owner, si,
				startUrl);
		fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		fileDialog.setExtensionsToDisplay(extensions);
		fileDialog.displayHiddenFiles(displayHiddenFiles);

		fileDialog.centerOnOwner();

		return fileDialog;
	}

	public static GridFileTreeDialog createGridFileDialog(
			ServiceInterface si, String historyKey, String[] extensions,
			boolean displayHiddenFiles, Window owner) {

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
				myLogger.debug(e);
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			}
		}

		final GridFileTreeDialog fileDialog = new GridFileTreeDialog(
				owner, si, displayHiddenFiles, extensions, true, startUrl);
		fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		fileDialog.centerOnOwner();

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

	public AbstractWidget() {
		super();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
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

	public void lockIUI(final boolean lock) {

		final Component[] comps = getComponents();
		for (final Component comp : comps) {
			comp.setEnabled(!lock);
		}

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(l);
	}

	public void saveItemToHistory() {
		if ((hm != null) && StringUtils.isNotBlank(historyKey)) {
			final String temp = getValue();
			if (StringUtils.isNotBlank(temp)) {
				System.out.println("Adding: " + this.getClass().toString());
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
