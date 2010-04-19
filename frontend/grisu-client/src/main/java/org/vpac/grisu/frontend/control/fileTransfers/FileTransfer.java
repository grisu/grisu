package org.vpac.grisu.frontend.control.fileTransfers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.files.GlazedFile;

public class FileTransfer implements Comparable<FileTransfer> {

	public enum Status {
		CREATED,
		TRANSFERRING,
		FINISHED,
		FAILED
	}

	private final FileManager fm;
	private final Set<String> sourceUrls;
	private final String targetDirUrl;
	private final JobObject job;
	private final boolean overwrite;

	private FileTransferException possibleException;
	private String failedSourceFile;

	private String currentSourceFile;

	private Date started;
	private Date finished;

	private int transferredSourceFiles = 0;

	private Callable<FileTransfer.Status> transferThread;

	private Future<Status> endStatus = null;

	private Status status = Status.CREATED;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public FileTransfer(FileManager fm, Set<GlazedFile> sources, GlazedFile target, boolean overwrite) {

		this(fm, GlazedFile.extractUrls(sources), target.getUrl(), overwrite);
	}

	public FileTransfer(FileManager fm, Set<String> sources, JobObject job) {

		this.fm = fm;
		this.sourceUrls = sources;
		this.targetDirUrl = job.getJobDirectoryUrl();
		this.job = job;
		this.overwrite = true;

	}

	public FileTransfer(FileManager fm, Set<String> sourceUrl, String targetDirUrl, boolean overwrite) {
		this.fm = fm;
		this.sourceUrls = sourceUrl;
		this.targetDirUrl = targetDirUrl;
		this.job = null;
		this.overwrite = overwrite;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public int compareTo(FileTransfer o) {

		return getStarted().compareTo(o.getStarted());

	}

	@Override
	public boolean equals(Object o) {

		if ( o instanceof FileTransfer ) {
			FileTransfer ft = (FileTransfer)o;
			if ( getSourceUrl().equals(ft.getSourceUrl()) && getTargetDirUrl().equals(ft.getTargetDirUrl())
					&& getStatus().equals(ft.getStatus()) ) {
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

	public FileTransferException getException() {
		return possibleException;
	}

	public String getFailedSourceFile() {
		return this.failedSourceFile;
	}

	public Date getFinished() {
		return finished;
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
		if ( (targetDirUrl == null) && (job != null) ) {
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

	public synchronized Callable<FileTransfer.Status> getTransferThread() {

		if ( transferThread == null ) {

			transferThread = new Callable<FileTransfer.Status>() {

				public FileTransfer.Status call() throws InterruptedException {

					started = new Date();
					status = Status.TRANSFERRING;
					pcs.firePropertyChange("status", Status.CREATED, Status.TRANSFERRING);

					for ( String sourceUrl : sourceUrls ) {
						String oldFile = currentSourceFile;
						currentSourceFile = sourceUrl;
						pcs.firePropertyChange("currentSourceFile", oldFile, currentSourceFile);
						try {
							if ( job != null ) {
								fm.uploadInputFile(sourceUrl, job.getJobname());
							} else {
								fm.cp(sourceUrl, targetDirUrl, overwrite);
							}
						} catch (FileTransferException e) {
							possibleException = e;
							failedSourceFile = sourceUrl;
							finished = new Date();
							status = Status.FAILED;
							pcs.firePropertyChange("status", Status.TRANSFERRING, Status.FAILED);
							return status;
						}
						transferredSourceFiles = transferredSourceFiles+1;
						pcs.firePropertyChange("transferredSourceFiles", transferredSourceFiles-1, transferredSourceFiles);
					}
					finished = new Date();
					status = Status.FINISHED;
					pcs.firePropertyChange("status", Status.TRANSFERRING, Status.FINISHED);

					return status;
				}
			};

		}

		return transferThread;
	}

	@Override
	public int hashCode() {
		return 21 * getSourceUrl().hashCode() + 3 * getTargetDirUrl().hashCode() + 8 * getStatus().hashCode();
	}

	public boolean isFinished() {

		if ( Status.FAILED.equals(this.getStatus()) || Status.FINISHED.equals(this.getStatus()) ) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public Status join() throws InterruptedException, ExecutionException {

		if ( (transferThread == null) || (endStatus == null) ) {
			throw new IllegalStateException("Transfer thread not created yet.");
		}

		return endStatus.get();


	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void setFuture(Future<Status> future) {
		this.endStatus = future;
	}
}
