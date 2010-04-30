package org.vpac.grisu.control;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.GrisuTemplateFilenameFilter;

public class TemplateManager {

	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final Map<String, List<String>> remoteTemplates = new TreeMap<String, List<String>>();
	private String[] remoteTemplateNames = null;
	private Map<String, List<String>> localTemplates = null;

	private final ServiceInterface si;

	public TemplateManager(ServiceInterface si) {
		this.si = si;
	}

	public String addLocalTemplate(File template) {

		File file = new File(Environment.getTemplateDirectory(), template
				.getName());

		if (!file.equals(template)) {
			try {
				FileUtils.copyFile(template, file);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}

		List<String> temp;
		try {
			temp = FileUtils.readLines(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String filename = FilenameUtils.getBaseName(file.toString());
		localTemplates.put(filename, temp);

		pcs.firePropertyChange("localTemplateNames", null,
				getLocalTemplateNames());

		return filename;
	}

	public void addRemoteTemplate(String name) throws NoSuchTemplateException {

		List<String> temp = getRemoteTemplate(name);

		ClientPropertiesManager.addServerTemplate(name);

		pcs.firePropertyChange("remoteTemplateNames", null,
				getRemoteTemplateNames());

	}

	public void addTemplateManagerListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public SortedSet<String> getAllTemplateNames() {

		SortedSet<String> allNames = new TreeSet<String>();

		allNames.addAll(getMyRemoteTemplateNames());
		allNames.addAll(getLocalTemplates().keySet());

		return allNames;
	}

	public List<String> getLocalTemplateNames() {

		return new LinkedList(getLocalTemplates().keySet());

	}

	public Map<String, List<String>> getLocalTemplates() {
		if (localTemplates == null) {

			localTemplates = new HashMap<String, List<String>>();

			File tempDir = new File(Environment.getTemplateDirectory());

			if (!tempDir.exists()) {
				if (!tempDir.mkdirs()) {
					throw new RuntimeException(
							"Could not create directory "
									+ tempDir.toString()
									+ ". Please create it manually and make it writable by the current user.");
				}
			}

			File[] templates = tempDir
					.listFiles(new GrisuTemplateFilenameFilter());

			for (File file : templates) {
				addLocalTemplate(file);
			}

		}
		return localTemplates;
	}

	public List<String> getMyRemoteTemplateNames() {

		String[] myTemps = ClientPropertiesManager.getServerTemplates();

		return Arrays.asList(myTemps);
	}

	public List<String> getRemoteTemplate(String name)
			throws NoSuchTemplateException {

		if (remoteTemplates.get(name) == null) {
			String temp = si.getTemplate(name);
			if (StringUtils.isBlank(temp)) {
				throw new NoSuchTemplateException("Template " + name
						+ " is empty.");
			}
			List<String> lines = Arrays.asList(StringUtils.split(temp, '\n'));
			remoteTemplates.put(name, lines);
		}
		return remoteTemplates.get(name);
	}

	public String[] getRemoteTemplateNames() {

		if (remoteTemplateNames == null) {
			remoteTemplateNames = si.listHostedApplicationTemplates();
		}
		return remoteTemplateNames;
	}

	public List<String> getTemplate(String name) throws NoSuchTemplateException {

		List<String> result = getLocalTemplates().get(name);

		if (result == null || result.size() == 0) {
			return getRemoteTemplate(name);
		} else {
			return result;
		}

	}

	public void removeLocalApplication(String name) {

		localTemplates.remove(name);

		File temp = new File(Environment.getTemplateDirectory(), name
				+ ".template");

		temp.delete();

		pcs.firePropertyChange("localTemplateNames", null,
				getLocalTemplateNames());

	}

	public void removeRemoteApplication(String name) {

		ClientPropertiesManager.removeServerTemplate(name);

		pcs.firePropertyChange("remoteTemplateNames", null,
				getRemoteTemplateNames());

	}

	public void removeTemplateManagerListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

}
