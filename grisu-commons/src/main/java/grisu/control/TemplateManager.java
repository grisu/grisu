package grisu.control;

import grisu.control.exceptions.NoSuchTemplateException;
import grisu.control.exceptions.TemplateException;
import grisu.settings.ClientPropertiesManager;
import grisu.settings.Environment;
import grisu.utils.GrisuTemplateFilenameFilter;

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
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class TemplateManager {

	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	// private final Map<String, List<String>> remoteTemplates = new
	// TreeMap<String, List<String>>();
	private String[] remoteTemplateNames = null;
	// private Map<String, List<String>> localTemplates = null;

	private final ServiceInterface si;

	public TemplateManager(ServiceInterface si) {
		this.si = si;
	}

	public String addLocalTemplate(File template) {

		final File file = new File(Environment.getTemplateDirectory(),
				template.getName());

		if (!file.equals(template)) {
			try {
				FileUtils.copyFile(template, file);
			} catch (final IOException e1) {
				throw new RuntimeException(e1);
			}
		}

		List<String> temp;
		try {
			temp = FileUtils.readLines(file);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		final String filename = FilenameUtils.getBaseName(file.toString());
		// localTemplates.put(filename, temp);

		pcs.firePropertyChange("localTemplateNames", null,
				getLocalTemplateNames());

		return filename;
	}

	public void addRemoteTemplate(String name) throws NoSuchTemplateException {

		final List<String> temp = getRemoteTemplate(name);

		ClientPropertiesManager.addServerTemplate(name);

		pcs.firePropertyChange("remoteTemplateNames", null,
				getRemoteTemplateNames());

	}

	public void addTemplateManagerListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public String copyTemplateToLocalTemplateStore(String name)
			throws NoSuchTemplateException {

		final List<String> temp = getRemoteTemplate(name);

		File file = new File(Environment.getTemplateDirectory(), name
				+ ".template");
		int i = 1;
		String tempName = name;
		while (file.exists()) {
			tempName = name + "_" + i;
			file = new File(Environment.getTemplateDirectory(), tempName
					+ ".template");
			i = i + 1;
		}

		try {
			FileUtils.writeLines(file, temp);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		pcs.firePropertyChange("localTemplateNames", null,
				getLocalTemplateNames());

		return tempName;

	}

	public SortedSet<String> getAllTemplateNames() {

		final SortedSet<String> allNames = new TreeSet<String>();

		allNames.addAll(getMyRemoteTemplateNames());
		allNames.addAll(getLocalTemplates().keySet());

		return allNames;
	}

	public List<String> getLocalTemplate(File template)
			throws TemplateException {

		if (!template.exists()) {
			throw new TemplateException("Template " + template.toString()
					+ " does not exit.");
		}

		try {
			return FileUtils.readLines(template);
		} catch (final IOException e) {
			throw new TemplateException("Can't read template: "
					+ e.getLocalizedMessage(), e);
		}

	}

	public List<String> getLocalTemplateNames() {

		return new LinkedList<String>(getLocalTemplates().keySet());

	}

	public Map<String, List<String>> getLocalTemplates() {
		// if (localTemplates == null) {

		final Map<String, List<String>> localTemplates = new HashMap<String, List<String>>();

		final File tempDir = new File(Environment.getTemplateDirectory());

		if (!tempDir.exists()) {
			if (!tempDir.mkdirs()) {
				throw new RuntimeException(
						"Could not create directory "
								+ tempDir.toString()
								+ ". Please create it manually and make it writable by the current user.");
			}
		}

		final File[] templates = tempDir
				.listFiles(new GrisuTemplateFilenameFilter());

		for (final File file : templates) {

			List<String> temp;
			try {
				temp = FileUtils.readLines(file);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			final String filename = FilenameUtils.getBaseName(file.toString());
			localTemplates.put(filename, temp);

		}

		// }
		return localTemplates;
	}

	public List<String> getMyRemoteTemplateNames() {

		String[] myTemps = ClientPropertiesManager.getServerTemplates();
		
		if ( myTemps == null || myTemps.length == 0 ) {
			myTemps = new String[]{"generic"};
		}

		return Arrays.asList(myTemps);
	}

	public List<String> getRemoteTemplate(String name)
			throws NoSuchTemplateException {

		String temp = null;
		try {
			temp = si.getTemplate(name);
		} catch (final Exception e) {
			// doesn't matter -- will be dealt with below...
		}
		if (StringUtils.isBlank(temp)) {
			throw new NoSuchTemplateException("Template " + name + " is empty.");
		}
		final List<String> lines = Arrays.asList(StringUtils.split(temp, '\n'));
		return lines;
	}

	public String[] getRemoteTemplateNames() {

		if (remoteTemplateNames == null) {
			remoteTemplateNames = si.listHostedApplicationTemplates();
		}
		return remoteTemplateNames;
	}

	public List<String> getTemplate(String name) throws NoSuchTemplateException {

		final List<String> result = getLocalTemplates().get(name);

		if (result == null || result.size() == 0) {
			return getRemoteTemplate(name);
		} else {
			return result;
		}

	}

	public void removeLocalApplication(String name) {

		// localTemplates.remove(name);

		final File temp = new File(Environment.getTemplateDirectory(), name
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
