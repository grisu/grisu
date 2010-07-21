package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.Component;
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
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.historyRepeater.HistoryManager;

public abstract class AbstractWidget extends JPanel {

	static final Logger myLogger = Logger.getLogger(SingleInputFile.class
			.getName());

	public static Logger getMylogger() {
		return myLogger;
	}

	public static GrisuFileDialog createFileDialog(ServiceInterface si,
			String historyKey, String[] extensions, boolean displayHiddenFiles) {

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
			} catch (RemoteFileSystemException e) {
				myLogger.debug(e);
				startUrl = new File(System.getProperty("user.home")).toURI()
						.toString();
			}
		}
		GrisuFileDialog fileDialog = new GrisuFileDialog(si, startUrl);
		fileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		fileDialog.setExtensionsToDisplay(extensions);
		fileDialog.displayHiddenFiles(displayHiddenFiles);

		return fileDialog;
	}

	private PropertyChangeSupport pcs = null;

	private ServiceInterface si;

	private HistoryManager hm;

	private String historyKey = null;

	public AbstractWidget() {
		super();
	}

	public String getHistoryKey() {
		return historyKey;
	}

	public HistoryManager getHistoryManager() {
		return hm;
	}

	public ServiceInterface getServiceInterface() {
		return si;
	}

	/**
	 * Can be overridden if necessary.
	 */
	public void historyKeySet() {

	}

	protected PropertyChangeSupport getPropertyChangeSupport() {
		if (pcs == null) {
			pcs = new PropertyChangeSupport(this);
		}
		return pcs;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		getPropertyChangeSupport().removePropertyChangeListener(l);
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

	private void initHistory() {

		String lastValue = hm.getLastEntry(this.historyKey);
		if (StringUtils.isBlank(lastValue)) {
			return;
		}

		historyKeySet();

		if (setLastValue()) {
			try {
				setValue(lastValue);
			} catch (Exception e) {
				return;
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
	 * @param group
	 */
	public void setValidationGroup(ValidationGroup group) {
	}

	public void lockIUI(final boolean lock) {

		Component[] comps = getComponents();
		for (Component comp : comps) {
			comp.setEnabled(!lock);
		}

	}

	public void setTitle(String title) {
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
	}

	public void saveItemToHistory() {
		if (hm != null && StringUtils.isNotBlank(historyKey)) {
			String temp = getValue();
			if (StringUtils.isNotBlank(temp)) {
				System.out.println("Adding: " + this.getClass().toString());
				hm.addHistoryEntry(this.historyKey, temp);
			}
		}
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.hm = GrisuRegistryManager.getDefault(si).getHistoryManager();

		if (historyKey != null) {
			initHistory();
		}
	}

	abstract public void setValue(String value);

	abstract public String getValue();

}
