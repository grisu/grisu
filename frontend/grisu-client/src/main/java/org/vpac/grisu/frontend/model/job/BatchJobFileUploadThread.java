package org.vpac.grisu.frontend.model.job;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.activation.DataHandler;

import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.model.StatusObject;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.model.FileManager;

public class BatchJobFileUploadThread extends Thread {
	
	private final BatchJobObject batchJob;
	private final String inputFile;
	private final int noOfUpload;
	private final ServiceInterface si;
	private final ExecutorService executor;
	private List<Exception> exceptions;
	
	public BatchJobFileUploadThread(ServiceInterface si, BatchJobObject batchJob, String inputFile, int noOfUpload, ExecutorService executor) {
		this.si = si;
		this.batchJob = batchJob;
		this.inputFile = inputFile;
		this.noOfUpload = noOfUpload;
		this.executor = executor;
	}

	private void shutdownExecutor() {
		if ( executor != null ) {
			executor.shutdownNow();
		}
	}
	
	public void run() {
		try {
			
			final int all = batchJob.getInputFiles().keySet().size();
			EventBus.publish(batchJob.getBatchJobname(), new BatchJobEvent(
					batchJob, "Uploading/copying common input file ("+noOfUpload+" of "+all+"): "
							+ inputFile + " for multipartjob "
							+ batchJob.getBatchJobname()));
			if (FileManager.isLocal(inputFile)) {

				DataHandler dh = FileManager
						.createDataHandler(inputFile);
				StatusObject status = new StatusObject(
						si, batchJob.getInputFiles().get(inputFile));
				si.uploadInputFile(batchJob.getBatchJobname(),
						dh, batchJob.getInputFiles().get(inputFile));
				
				if ( Thread.currentThread().isInterrupted() ) {
					shutdownExecutor();
					return;
				}

				try {
					status.waitForActionToFinish(2, true, false);
				} catch (Exception e) {
					shutdownExecutor();
					return;
				}

				if (status.getStatus().isFailed()) {
					throw new FileTransferException(
							inputFile,
							batchJob.getInputFiles().get(inputFile),
							"Error when trying to upload input file.",
							null);
				}

			} else {
				si.copyBatchJobInputFile(
						batchJob.getBatchJobname(), inputFile, batchJob.getInputFiles()
								.get(inputFile));
				if ( Thread.currentThread().isInterrupted() ) {
					shutdownExecutor();
					return;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			exceptions.add(e);
			shutdownExecutor();
		}
	}
	

}
