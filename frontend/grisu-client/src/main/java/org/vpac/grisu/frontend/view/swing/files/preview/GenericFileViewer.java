package org.vpac.grisu.frontend.view.swing.files.preview;

import java.awt.CardLayout;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.view.swing.files.FileListListener;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;

public class GenericFileViewer extends JPanel implements FileViewer,
FileListListener {

	private static Set<String> viewers = null;

	private static FileViewer createViewerPanel(File currentLocalCacheFile) {

		Magic parser = new Magic();
		MagicMatch match = null;
		try {
			match = Magic.getMagicMatch(currentLocalCacheFile, true);
			System.out.println(match.getMimeType());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Set<String> viewers = findViewers();
		for (String f : viewers) {

			try {
				FileViewer viewerClass = (FileViewer) (Class.forName(f)	.newInstance());

				for (String t : viewerClass.getSupportedMimeTypes()) {
					if (match.getMimeType().contains(t)) {
						return viewerClass;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		//		// if null, try known ones
		//		FileViewer viewer = new PlainTextFileViewer();
		//		for (String t : viewer.getSupportedMimeTypes()) {
		//			if (match.getMimeType().contains(t)) {
		//				return viewer;
		//			}
		//		}

		//		viewer = new ImageFileViewer();
		//		for (String t : viewer.getSupportedMimeTypes()) {
		//			if (match.getMimeType().contains(t)) {
		//				return viewer;
		//			}
		//		}

		return null;
	}

	public static Set<String> findViewers() {

		if (viewers == null) {
			viewers = new HashSet<String>();

			String pckgname = "org.vpac.grisu.frontend.view.swing.files.preview.fileViewers";
			String name = new String(pckgname);
			if (!name.startsWith("/")) {
				name = "/" + name;
			}
			name = name.replace('.', '/');

			// Get a File object for the package
			URL url = GenericFileViewer.class.getResource(name);
			File directory = new File(url.getFile());
			// New code
			// ======
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (String file : files) {

					// we are only interested in .class files
					if (file.endsWith(".class")) {
						// removes the .class extension
						String classname = file.substring(0, file.length() - 6);
						try {
							// Try to create an instance of the object
							Object o = Class
							.forName(pckgname + "." + classname)
							.newInstance();
							if (o instanceof FileViewer) {
								viewers.add(pckgname + "." + classname);
							}
						} catch (ClassNotFoundException cnfex) {
							System.err.println(cnfex);
						} catch (InstantiationException iex) {
							// We try to instantiate an interface
							// or an object that does not have a
							// default constructor
						} catch (IllegalAccessException iaex) {
							// The class is not public
						}
					}
				}
			}
		}

		if ( (viewers == null) || (viewers.size() == 0) ) {
			viewers = new HashSet<String>();
			viewers.add("org.vpac.grisu.frontend.view.swing.files.preview.fileViewers.PlainTextFileViewer");
			viewers.add("org.vpac.grisu.frontend.view.swing.files.preview.fileViewers.ImageFileViewer");
		}
		return viewers;
	}

	private final ServiceInterface si;
	private final FileManager fm;

	private File currentLocalCacheFile = null;

	private GlazedFile currentGlazedFile = null;

	private final JPanel emptyPanel = new JPanel();
	private final String EMPTY_PANEL = "__empty__";

	/**
	 * Create the panel.
	 */
	public GenericFileViewer(ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

		setLayout(new CardLayout());

		add(emptyPanel, EMPTY_PANEL);
	}

	public void fileDoubleClicked(GlazedFile file) {

		setFile(file, null);
	}

	public void filesSelected(Set<GlazedFile> files) {

	}

	public JPanel getPanel() {
		return this;
	}

	public String[] getSupportedMimeTypes() {
		return new String[] { "*" };
	}

	public void isLoading(boolean loading) {

	}

	private void setEmptyPanel() {
		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				CardLayout cl = (CardLayout) (getLayout());
				cl.show(GenericFileViewer.this, EMPTY_PANEL);

				revalidate();
			}

		});
	}

	public void setFile(GlazedFile file, File localCacheFile) {

		setEmptyPanel();
		currentGlazedFile = file;
		if ((localCacheFile != null) && localCacheFile.exists()) {
			currentLocalCacheFile = localCacheFile;
		} else {
			try {
				if (fm.upToDateLocalCacheFileExists(file.getUrl())) {
					currentLocalCacheFile = fm.getLocalCacheFile(file.getUrl());
				} else {
					if (fm.isBiggerThanTreshold(file.getUrl())) {

						System.out.println("Bigger than treshold...");
						return;
					}

					currentLocalCacheFile = fm.downloadFile(file.getUrl());
				}

			} catch (RemoteFileSystemException e) {
				e.printStackTrace();
			} catch (FileTransferException e) {
				e.printStackTrace();
			}
		}

		if (!currentLocalCacheFile.exists()
				|| (currentLocalCacheFile.length() == 0L)) {
			System.out.println("File empty...");
			return;
		}

		final FileViewer viewer = createViewerPanel(currentLocalCacheFile);

		if (viewer != null) {
			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {
					viewer.setFile(currentGlazedFile, currentLocalCacheFile);
					add(viewer.getPanel(), currentLocalCacheFile.toString());

					CardLayout cl = (CardLayout) (getLayout());
					cl.show(GenericFileViewer.this, currentLocalCacheFile
							.toString());

					revalidate();
				}

			});

		} else {

			setEmptyPanel();
			System.out.println("No viewer panel found.");
		}

		System.out.println("File set: " + file.getName());
		System.out.println("Local file: " + currentLocalCacheFile.getPath());
		System.out.println("Local filesize: " + currentLocalCacheFile.length());

	}
}
