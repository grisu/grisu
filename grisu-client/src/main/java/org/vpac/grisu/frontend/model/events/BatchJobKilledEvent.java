package org.vpac.grisu.frontend.model.events;

public class BatchJobKilledEvent {

	private final String jobname;
	private final String application;

	public BatchJobKilledEvent(String jobname, String application) {
		this.jobname = jobname;
		this.application = application;
	}

	public String getApplication() {
		return application;
	}

	public String getJobname() {
		return jobname;
	}

	@Override
	public String toString() {
		return getJobname() + " killed";
	}

}
