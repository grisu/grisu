package grisu.frontend.model.job;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.model.dto.DtoJob;

import java.util.Map;

import org.w3c.dom.Document;

public class Job extends JobObject {

	public Job(ServiceInterface si) {
		super(si);
	}

	public Job(ServiceInterface si, Document jsdl) {
		super(si, jsdl);
	}

	public Job(ServiceInterface si, DtoJob job) throws NoSuchJobException {
		super(si, job);
	}

	public Job(ServiceInterface si, DtoJob job,
			boolean refreshJobStatusOnBackend) throws NoSuchJobException {
		super(si, job, refreshJobStatusOnBackend);
	}

	public Job(ServiceInterface si, Map<String, String> jobProperties) {
		super(si, jobProperties);
	}

	public Job(ServiceInterface si, String jobname) throws NoSuchJobException {
		super(si, jobname);
	}

	public Job(ServiceInterface si, String jobname,
			boolean refreshJobStatusOnBackend) throws NoSuchJobException {
		super(si, jobname, refreshJobStatusOnBackend);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
