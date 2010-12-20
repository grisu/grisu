package org.vpac.grisu.frontend.model.job;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;

public class BatchJobFileUploadThread extends Thread {

	private final BatchJobObject batchJob;
	private final String inputFile;
	private final int noOfUpload;
	private final ServiceInterface si;
	private final ExecutorService executor;
	private final List<Exception> exceptions;

	private final FileManager fm;

	public BatchJobFileUploadThread(ServiceInterface si,
			BatchJobObject batchJob, String inputFile, int noOfUpload,
			ExecutorService executor, List<Exception> exceptions) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.batchJob = batchJob;
		this.inputFile = inputFile;
		this.noOfUpload = noOfUpload;
		this.executor = executor;
		this.exceptions = exceptions;
	}

	@Override
	public void run() {
		try {

			final int all = batchJob.getInputFiles().keySet().size();
			EventBus.publish(batchJob.getJobname(), new BatchJobEvent(batchJob,
					"Uploading/copying common input file (" + noOfUpload
							+ " of " + all + "): " + inputFile
							+ " for multipartjob " + batchJob.getJobname()));
			if (FileManager.isLocal(inputFile)) {

				// final DataHandler dh =
				// FileManager.createDataHandler(inputFile);
				// final StatusObject status = new StatusObject(si, batchJob
				// .getInputFiles().get(inputFile));
				fm.uploadJobInput(batchJob.getJobname(), inputFile, batchJob
						.getInputFiles().get(inputFile));

				// si.uploadInputFile(batchJob.getJobname(), dh, batchJob
				// .getInputFiles().get(inputFile));
				if (Thread.currentThread().isInterrupted()) {
					shutdownExecutor();
					return;
				}

				// try {
				// status.waitForActionToFinish(2, true, false);
				// } catch (final Exception e) {
				// e.printStackTrace();
				// shutdownExecutor();
				// return;
				// }

				// if (status.getStatus().isFailed()) {
				// throw new FileTransactionException(inputFile, batchJob
				// .getInputFiles().get(inputFile),
				// "Error when trying to upload input file.", null);
				// }

			} else {
				si.copyBatchJobInputFile(batchJob.getJobname(), inputFile,
						batchJob.getInputFiles().get(inputFile));
				if (Thread.currentThread().isInterrupted()) {
					shutdownExecutor();
					return;
				}
			}
		} catch (final Exception e) {
			// e.printStackTrace();
			exceptions.add(e);
			shutdownExecutor();
		}
	}

	private void shutdownExecutor() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

}
