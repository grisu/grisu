package grisu.frontend.control.fileTransfers;

import grisu.control.events.FileDeletedEvent;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.model.job.GrisuJob;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;

public class FileTransaction implements Comparable<FileTransaction> {

	public enum Status {
		CREATED,
		TRANSACTION_RUNNING,
		FINISHED,
		FAILED
	}

	public static final String DELETE_STRING = "delete";

	private final FileManager fm;
	private final Set<String> sourceUrls;
	private final String targetDirUrl;
	private final GrisuJob job;
	private final boolean overwrite;

	private FileTransactionException possibleException;
	private String failedSourceFile;

	private String currentSourceFile;

	private Date started;
	private Date finished;

	private int transferredSourceFiles = 0;

	private Callable<FileTransaction.Status> transferThread;

	private Future<Status> endStatus = null;

	private Status status = Status.CREATED;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final String id = UUID.randomUUID().toString();


	public FileTransaction(FileManager fm, Set<GridFile> sources,
			GridFile target, boolean overwrite) {

		this(fm, GridFile.extractUrls(sources), target.getUrl(), overwrite);
	}

	public FileTransaction(FileManager fm, Set<String> sources, GrisuJob job,
			String targetPath) {

		this.fm = fm;
		this.sourceUrls = sources;
		if (StringUtils.isBlank(targetPath)) {
			this.targetDirUrl = "";
		} else {
			this.targetDirUrl = targetPath;
		}
		this.job = job;
		this.overwrite = true;

		// if (StringUtils.isBlank(this.targetDirUrl)) {
		// throw new RuntimeException("Targeturl not set.");
		// }
	}

	public FileTransaction(FileManager fm, Set<String> sourceUrl,
			String targetDirUrl) {

		this(fm, sourceUrl, targetDirUrl, false);

	}

	public FileTransaction(FileManager fm, Set<String> sourceUrl,
			String targetDirUrl, boolean overwrite) {
		this.fm = fm;
		this.sourceUrls = sourceUrl;
		this.targetDirUrl = targetDirUrl;
		this.job = null;
		this.overwrite = overwrite;

		if (StringUtils.isBlank(this.targetDirUrl)) {
			throw new RuntimeException("Targeturl not set.");
		}

	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public int compareTo(FileTransaction o) {

		return getStarted().compareTo(o.getStarted());

	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof FileTransaction) {
			final FileTransaction ft = (FileTransaction) o;
			if (getSourceUrl().equals(ft.getSourceUrl())
					&& getTargetDirUrl().equals(ft.getTargetDirUrl())
					&& getStatus().equals(ft.getStatus())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getCurrentSourceFile() {
		return currentSourceFile;
	}

	public FileTransactionException getException() {
		return possibleException;
	}

	public String getFailedSourceFile() {
		return this.failedSourceFile;
	}

	public Date getFinished() {
		return finished;
	}

	public String getId() {
		return id;
	}

	public Set<String> getSourceUrl() {
		return sourceUrls;
	}

	public Date getStarted() {
		return started;
	}

	public Status getStatus() {
		return status;
	}

	public String getTargetDirUrl() {
		if ((targetDirUrl == null) && (job != null)) {
			return job.getJobDirectoryUrl();
		} else {
			return targetDirUrl;
		}
	}

	public int getTotalSourceFiles() {
		return sourceUrls.size();
	}

	public int getTransferredSourceFiles() {
		return transferredSourceFiles;
	}

	public synchronized Callable<FileTransaction.Status> getTransferThread() {

		if (transferThread == null) {

			transferThread = new Callable<FileTransaction.Status>() {

				public FileTransaction.Status call()
						throws InterruptedException {

					started = new Date();
					status = Status.TRANSACTION_RUNNING;
					pcs.firePropertyChange("status", Status.CREATED,
							Status.TRANSACTION_RUNNING);

					for (final String sourceUrl : sourceUrls) {

						if (Thread.currentThread().isInterrupted()) {

							status = Status.FAILED;
							finished = new Date();
							pcs.firePropertyChange("status",
									Status.TRANSACTION_RUNNING, Status.FAILED);
							failedSourceFile = sourceUrl;
							throw new InterruptedException(
									"Job staging interrupted.");
						}

						final String oldFile = currentSourceFile;
						currentSourceFile = sourceUrl;
						pcs.firePropertyChange("currentSourceFile", oldFile,
								currentSourceFile);
						try {
							if (job != null) {

								if (StringUtils.isBlank(targetDirUrl)) {
									fm.uploadJobInput(job.getJobname(),
											sourceUrl,
											FileManager.getFilename(sourceUrl));
								} else {
									fm.uploadJobInput(job.getJobname(),
											sourceUrl, targetDirUrl);
								}
							} else {
								if (DELETE_STRING.equals(targetDirUrl)) {
									try {
										// X.p("XXX: " + sourceUrl);

										fm.deleteFile(sourceUrl);
										EventBus.publish(new FileDeletedEvent(
												sourceUrl));
									} catch (final RemoteFileSystemException e1) {
										throw new FileTransactionException(
												sourceUrl, targetDirUrl,
												e1.getLocalizedMessage(), e1);
									}
								} else {
									try {
										fm.cp(sourceUrl, targetDirUrl,
												overwrite);
									} catch (final Exception e3) {
										throw new FileTransactionException(
												sourceUrl, targetDirUrl,
												e3.getLocalizedMessage(), e3);
									}
								}
							}
						} catch (final FileTransactionException e) {
							possibleException = e;
							failedSourceFile = sourceUrl;
							finished = new Date();
							status = Status.FAILED;
							pcs.firePropertyChange("status",
									Status.TRANSACTION_RUNNING, Status.FAILED);
							return status;
						}
						transferredSourceFiles = transferredSourceFiles + 1;
						pcs.firePropertyChange("transferredSourceFiles",
								transferredSourceFiles - 1,
								transferredSourceFiles);
					}
					finished = new Date();
					status = Status.FINISHED;
					pcs.firePropertyChange("status",
							Status.TRANSACTION_RUNNING, Status.FINISHED);

					return status;

				}

			};

		}

		return transferThread;
	}

	@Override
	public int hashCode() {
		return (21 * getSourceUrl().hashCode()) + (3
				* getTargetDirUrl().hashCode()) + (8 * getStatus().hashCode());
	}

	public boolean isFinished() {

		if (Status.FAILED.equals(this.getStatus())
				|| Status.FINISHED.equals(this.getStatus())) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public Status join() throws InterruptedException, ExecutionException {

		if ((transferThread == null) || (endStatus == null)) {
			throw new IllegalStateException("Transfer thread not created yet.");
		}

		try {
			return endStatus.get();
		} catch (final InterruptedException e) {
			endStatus.cancel(true);
			failedSourceFile = currentSourceFile;
			possibleException = new FileTransactionException(currentSourceFile,
					targetDirUrl, "File transfer interrupted", e);
			finished = new Date();
			status = Status.FAILED;
			pcs.firePropertyChange("status", Status.TRANSACTION_RUNNING,
					Status.FAILED);
			throw e;
		} catch (final Exception e) {
			endStatus.cancel(true);
			failedSourceFile = currentSourceFile;
			possibleException = new FileTransactionException(currentSourceFile,
					targetDirUrl, "File transfer error", e);
			finished = new Date();
			status = Status.FAILED;
			pcs.firePropertyChange("status", Status.TRANSACTION_RUNNING,
					Status.FAILED);

			throw new ExecutionException(e);
		}

	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void setFuture(Future<Status> future) {
		this.endStatus = future;
	}
}
