package grisu.frontend.control.fileTransfers;

import grisu.control.ServiceInterface;
import grisu.frontend.model.events.FileTransactionFailedEvent;
import grisu.frontend.model.job.JobObject;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;
import grisu.model.files.GlazedFile;
import grisu.settings.ClientPropertiesManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FileTransactionManager implements PropertyChangeListener {

	static final Logger myLogger = Logger
			.getLogger(FileTransactionManager.class.getName());

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

	public FileTransaction addFileTransfer(final FileTransaction ft) {

		fileTransfers.add(ft);
		ft.addPropertyChangeListener(this);
		final Future<FileTransaction.Status> future = executor1.submit(ft
				.getTransferThread());
		ft.setFuture(future);

		// someone has to watch the transfer thread
		new Thread() {
			@Override
			public void run() {
				try {
					ft.join();
				} catch (InterruptedException e) {
					myLogger.error(e);
					EventBus.publish(new FileTransactionFailedEvent(ft));
				} catch (ExecutionException e) {
					myLogger.error(e);
					EventBus.publish(new FileTransactionFailedEvent(ft));
				}
			}
		}.start();

		return ft;
	}

	public FileTransaction addJobInputFileTransfer(Set<String> sources,
			JobObject job, String targetPath) {
		final FileTransaction ft = new FileTransaction(fm, sources, job,
				targetPath);
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

	public FileTransaction deleteFiles(Set files) {

		final Set<String> temp = new HashSet<String>();
		for (final Object file : files) {
			if (file instanceof GlazedFile) {
				temp.add(((GlazedFile) file).getUrl());
			} else if (file instanceof GridFile) {
				temp.add(((GridFile) file).getUrl());
			} else if (file instanceof String) {
				temp.add((String) file);
			}
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
		EventBus.publish(new FileTransferEvent(ft, evt.getPropertyName(), evt
				.getNewValue()));

	}

}