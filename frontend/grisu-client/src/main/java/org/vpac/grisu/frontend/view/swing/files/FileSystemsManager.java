package org.vpac.grisu.frontend.view.swing.files;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FileSystemsManager {

	private static Map<ServiceInterface, FileSystemsManager> cachedRegistries = new HashMap<ServiceInterface, FileSystemsManager>();

	public static FileSystemsManager getDefault(ServiceInterface si) {
		if (si == null) {
			throw new RuntimeException(
			"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (si) {
			if (cachedRegistries.get(si) == null) {
				FileSystemsManager m = new FileSystemsManager(si);
				cachedRegistries.put(si, m);
			}
		}

		return cachedRegistries.get(si);
	}

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final EventList<FileSystemItem> allFileSystems = new BasicEventList<FileSystemItem>();

	public FileSystemsManager(ServiceInterface si) {
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
		.getUserEnvironmentManager();

		allFileSystems.addAll(em.getFileSystems());

		// Add seperators
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.SELECT, "Click here to select filesystem..."));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.LOCAL,
		" -- Local -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.BOOKMARK,
		" -- Bookmarks -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.REMOTE,
		" -- Grid -- "));

	}

	public void addBookmark(String alias, GlazedFile file) {
		FileSystemItem temp = this.em.setBookmark(alias, file.getUrl());
		allFileSystems.add(temp);
	}

	public EventList<FileSystemItem> getAllFileSystems() {
		return allFileSystems;
	}

	public void removeBookmark(String alias) {
		FileSystemItem temp = this.em.setBookmark(alias, null);
		allFileSystems.remove(temp);
	}

}
