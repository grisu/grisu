package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.files.FileSystemItem;
import grisu.model.files.GlazedFile;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FileSystemsManager {

	static final Logger myLogger = Logger.getLogger(FileSystemsManager.class
			.getName());

	private static Map<ServiceInterface, FileSystemsManager> cachedRegistries = new HashMap<ServiceInterface, FileSystemsManager>();

	public static FileSystemsManager getDefault(ServiceInterface si) {
		if (si == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default registry...");
		}

		synchronized (si) {
			if (cachedRegistries.get(si) == null) {
				final FileSystemsManager m = new FileSystemsManager(si);
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
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.SELECT,
				"Click here to select..."));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.LOCAL,
				" -- Local -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.BOOKMARK,
				" -- Bookmarks -- "));
		allFileSystems.add(new FileSystemItem(FileSystemItem.Type.REMOTE,
				" -- Grid -- "));

	}

	public void addBookmark(String alias, GlazedFile file) {
		final FileSystemItem temp = this.em.setBookmark(alias, file.getUrl());
		allFileSystems.add(temp);
	}

	public EventList<FileSystemItem> getAllFileSystems() {
		return allFileSystems;
	}

	// public FileSystemItem getFileSystemForUrl(String url) {
	//
	// return user
	//
	// // for (FileSystemItem fsi : getAllFileSystems()) {
	// //
	// // if (FileSystemItem.Type.SELECT.equals(fsi.getType())) {
	// // continue;
	// // }
	// //
	// // if (FileSystemItem.Type.BOOKMARK.equals(fsi.getType())) {
	// // continue;
	// // }
	// //
	// // myLogger.debug("Checking filesystem: " + fsi.getAlias());
	// //
	// // if (url.startsWith(fsi.getRootFile().getUrl())) {
	// // return fsi;
	// // }
	// // }
	// //
	// // for (FileSystemItem fsi : getAllFileSystems()) {
	// //
	// // if (!FileSystemItem.Type.BOOKMARK.equals(fsi.getType())) {
	// // continue;
	// // }
	// // myLogger.debug("Checking bookmark: " + fsi.getAlias());
	// // if (url.startsWith(fsi.getRootFile().getUrl())) {
	// // return fsi;
	// // }
	// // }
	// //
	// // return null;
	// }

	public void removeBookmark(String alias) {
		final FileSystemItem temp = this.em.setBookmark(alias, null);
		allFileSystems.remove(temp);
	}

}
