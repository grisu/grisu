package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.historyRepeater.HistoryManager;

public abstract class AbstractWidget extends JPanel {

	static final Logger myLogger = Logger.getLogger(SingleInputFile.class
			.getName());

	public static Logger getMylogger() {
		return myLogger;
	}
	
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}
	
	private void initHistory() {

		String lastValue = hm.getLastEntry(this.historyKey);
		if (StringUtils.isBlank(lastValue)) {
			return;
		}

		historyKeySet();

		try {
			setValue(lastValue);
		} catch (Exception e) {
			return;
		}

	}

	public void setHistoryKey(String key) {

		this.historyKey = key;

		if (si != null) {
			initHistory();
		}

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
