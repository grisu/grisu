package org.vpac.grisu.client.gridTests;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.vpac.grisu.client.model.JobException;
import org.vpac.grisu.client.model.JobObject;
import org.vpac.grisu.client.model.JobStatusChangeListener;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.JobSubmissionException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.MdsInformationException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.js.model.JobPropertiesException;

abstract class GridTestElement implements JobStatusChangeListener {

	protected final ServiceInterface serviceInterface;
	protected final String application;
	protected final String version;
	protected final String submissionLocation;

	protected final String id;

	private final List<GridTestStage> testStages = new LinkedList<GridTestStage>();

	protected final JobObject jobObject;

	private GridTestStage currentStage;

	protected GridTestElement(ServiceInterface si, String version,
			String submissionLocation) throws MdsInformationException {
		beginNewStage("Initializing test element...");
		this.serviceInterface = si;
		this.version = version;
		this.submissionLocation = submissionLocation;
		this.application = getApplicationSupported();
		addMessage("Creating JobObject...");
		this.jobObject = createJobObject();
		this.id = UUID.randomUUID().toString();
		this.jobObject.setJobname(this.id);
		addMessage("JobObject created.");
		this.jobObject.addJobStatusChangeListener(this);
		currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);
	}
	
	public static GridTestElement createGridTestElement(String application, ServiceInterface serviceInterface, String version, String subLoc) throws MdsInformationException {
		
		GridTestElement gte = null;
		if ( "java".equals(application) ) {
			gte = new JavaGridTestElement(serviceInterface, version, subLoc);
		} else if ( "UnixCommands".equals(application) ) {
			gte = new UnixCommandsGridTestElement(serviceInterface, version, subLoc);
		} else {
			gte = new GenericGridTestElement(serviceInterface, version, subLoc);
		}
		
		return gte;
	}

	public String getId() {
		return this.id;
	}
	
	public String getSubmissionLocation() {
		return submissionLocation;
	}

	public int getJobStatus(boolean forceRefresh) {
		return this.jobObject.getStatus(forceRefresh);
	}

	protected void addMessage(String message) {
		currentStage.addMessage(message);
	}

	private void beginNewStage(String stageName) {

		currentStage = new GridTestStage(stageName);
		testStages.add(currentStage);
		currentStage.setStatus(GridTestStageStatus.RUNNING);
	}

	public List<GridTestStage> getTestStages() {
		return testStages;
	}

	public void createJob(String fqan) {

		beginNewStage("Creating job on backend...");

		try {
			jobObject.createJob(fqan);
			currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);
		} catch (JobPropertiesException e) {
			currentStage.setPossibleException(e);
			currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
		}

	}

	public void submitJob() {

		beginNewStage("Submitting job to backend...");

		try {
			jobObject.submitJob();
			currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);
		} catch (JobSubmissionException e) {
			currentStage.setPossibleException(e);
			currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
		}
	}

	public void waitForJobToFinish() {

		beginNewStage("Waiting for job to finish...");

		while (this.jobObject.getStatus(true) < JobConstants.FINISHED_EITHER_WAY) {
			if (this.jobObject.getStatus(false) == JobConstants.NO_SUCH_JOB) {
				addMessage("Could not find job anymore. Probably a problem with the container...");
				currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
				return;
			}

			try {
				addMessage("Waiting 2 seconds before new check. Current Status: "
						+ JobConstants.translateStatus(this.jobObject
								.getStatus(false)));
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				currentStage.setPossibleException(e);
				currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
				return;
			}
		}

		addMessage("Job finished one way or another.");
		currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);

	}

	public void checkWhetherJobDidWhatItWasSupposedToDo() {

		beginNewStage("Checking job status and output...");

		boolean success = checkJobSuccess();

		if (success) {
			currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);
		} else {
			currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
		}

	}

	public void killAndClean() {

		beginNewStage("Killing and cleaning job...");

		try {
			jobObject.kill(true);
		} catch (NoSuchJobException e) {
			currentStage.setPossibleException(e);
			currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
		} catch (JobException e) {
			currentStage.setPossibleException(e);
			currentStage.setStatus(GridTestStageStatus.FINISHED_ERROR);
		}

		currentStage.setStatus(GridTestStageStatus.FINISHED_SUCCESS);

	}

	public void jobStatusChanged(JobObject job, int oldStatus, int newStatus) {

		addMessage("New job status: " + JobConstants.translateStatus(newStatus));

	}

	public void printTestResults() {
		for (GridTestStage stage : getTestStages()) {
			System.out.println("Stage: " + stage.getName());
			System.out.println("Started: " + stage.getBeginDate());
			stage.printMessages();
			System.out.println("Ended: " + stage.getEndDate());
			System.out.println("Status: " + stage.getStatus());
			if (stage.getStatus().equals(GridTestStageStatus.FINISHED_ERROR)) {
				System.out.println("Error: "
						+ stage.getPossibleException().getLocalizedMessage());
			}
			System.out.println();
		}
	}

	abstract protected JobObject createJobObject() throws MdsInformationException;

	abstract protected String getApplicationSupported();

	abstract protected boolean checkJobSuccess();

}
