package org.vpac.grisu.frontend.control.fileTransfers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Set;

import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
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
	private final boolean overwrite;

	private FileTransferException possibleException;

	private String currentSourceFile;

	private Date started;
	private Date finished;

	private int transferredSourceFiles = 0;

	private Thread transferThread;

	private Status status = Status.CREATED;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public FileTransfer(FileManager fm, Set<GlazedFile> sources, GlazedFile target, boolean overwrite) {

		this(fm, GlazedFile.extractUrls(sources), target.getUrl(), overwrite);

	}

	public FileTransfer(FileManager fm, Set<String> sourceUrl, String targetDirUrl, boolean overwrite) {
		this.fm = fm;
		this.sourceUrls = sourceUrl;
		this.targetDirUrl = targetDirUrl;
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
		return targetDirUrl;
	}

	public int getTotalSourceFiles() {
		return sourceUrls.size();
	}

	public int getTransferredSourceFiles() {
		return transferredSourceFiles;
	}

	public Thread getTransferThread() {

		if ( transferThread == null ) {

			transferThread = new Thread() {
				@Override
				public void run() {
					started = new Date();
					status = Status.TRANSFERRING;
					pcs.firePropertyChange("status", Status.CREATED, Status.TRANSFERRING);
					try {
						for ( String sourceUrl : sourceUrls ) {
							String oldFile = currentSourceFile;
							currentSourceFile = sourceUrl;
							pcs.firePropertyChange("currentSourceFile", oldFile, currentSourceFile);
							fm.cp(sourceUrl, targetDirUrl, overwrite);
							transferredSourceFiles = transferredSourceFiles+1;
							pcs.firePropertyChange("transferredSourceFiles", transferredSourceFiles-1, transferredSourceFiles);
						}
						finished = new Date();
						status = Status.FINISHED;
						pcs.firePropertyChange("status", Status.TRANSFERRING, Status.FINISHED);
					} catch (FileTransferException e) {
						possibleException = e;
						finished = new Date();
						status = Status.FAILED;
						pcs.firePropertyChange("status", Status.TRANSFERRING, Status.FAILED);
						return;
					}
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

	public void join() {

		if ( transferThread == null ) {
			throw new IllegalStateException("Transfer thread not created yet.");
		}

		if ( transferThread.isAlive() ) {
			try {
				transferThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}
}
