package org.vpac.grisu.frontend.control.fileTransfers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;
import org.vpac.grisu.settings.ClientPropertiesManager;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FileTransactionManager implements PropertyChangeListener {

	private static Map<ServiceInterface, FileTransactionManager> cachedFileTransferManagers = new HashMap<ServiceInterface, FileTransactionManager>();

	public static FileTransactionManager getDefault(final ServiceInterface si) {

		if (si == null) {
			throw new RuntimeException(
					"ServiceInterface not initialized yet. Can't get default filetransfermanager...");
		}

		synchronized (si) {
			if (cachedFileTransferManagers.get(si) == null) {
				final FileTransactionManager m = new FileTransactionManager(si);
				cachedFileTransferManagers.put(si, m);
			}
		}

		return cachedFileTransferManagers.get(si);
	}

	private final ServiceInterface si;
	private final FileManager fm;

	final ExecutorService executor1 = Executors
			.newFixedThreadPool(ClientPropertiesManager
					.getConcurrentUploadThreads());

	final EventList<FileTransaction> fileTransfers = new BasicEventList<FileTransaction>();

	final Thread shutdownHook = new Thread() {
		@Override
		public void run() {
			executor1.shutdownNow();
		}
	};

	public FileTransactionManager(ServiceInterface si) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();

		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}

	public FileTransaction addFileTransfer(FileTransaction ft) {

		fileTransfers.add(ft);
		ft.addPropertyChangeListener(this);
		final Future<FileTransaction.Status> future = executor1.submit(ft
				.getTransferThread());
		ft.setFuture(future);
		return ft;
	}

	public FileTransaction addJobInputFileTransfer(Set<String> sources,
			JobObject job) {
		final FileTransaction ft = new FileTransaction(fm, sources, job);
		return addFileTransfer(ft);
	}

	//
	// public FileTransfer addFileTransfer(Set<GlazedFile> sources, GlazedFile
	// target, boolean overwrite) {
	//
	// Set<String> temp = new HashSet<String>();
	// for ( GlazedFile f : sources ) {
	// temp.add(f.getUrl());
	// }
	//
	// return addFileTransfer(temp, target.getUrl(), overwrite);
	// }
	//
	// public FileTransfer addFileTransfer(Set<String> sourceUrls, String
	// targetUrl, boolean overwrite) {
	//
	// FileTransfer ft = new FileTransfer(fm, sourceUrls, targetUrl, overwrite);
	// return addFileTransfer(ft);
	//
	// }
	//
	// public FileTransfer addFileTransfer(String sourceUrl, String targetUrl,
	// boolean overwrite) {
	//
	// ImmutableSet<String> temp = ImmutableSet.of(sourceUrl);
	// FileTransfer ft = new FileTransfer(fm, temp, targetUrl, overwrite);
	// return addFileTransfer(ft);
	// }

	public FileTransaction deleteFiles(Set<GlazedFile> files) {

		final Set<String> temp = new HashSet<String>();
		for (final GlazedFile file : files) {
			temp.add(file.getUrl());
		}

		return deleteUrls(temp);

	}

	public FileTransaction deleteUrls(Set<String> urls) {

		final FileTransaction trans = new FileTransaction(fm, urls,
				FileTransaction.DELETE_STRING);

		return addFileTransfer(trans);
	}

	public void propertyChange(PropertyChangeEvent evt) {

		final FileTransaction ft = (FileTransaction) evt.getSource();
		EventBus.publish(new FileTransferEvent(ft));

	}

}
