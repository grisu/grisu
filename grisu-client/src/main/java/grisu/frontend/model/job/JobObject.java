package grisu.frontend.model.job;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.model.dto.DtoJob;

import java.util.Map;

import org.w3c.dom.Document;

/**
 * @author markus
 * @deprecated use {@link GrisuJob} instead
 */
public class JobObject extends GrisuJob {

	public JobObject(ServiceInterface si) {
		super(si);
	}

	public JobObject(ServiceInterface si, Document jsdl) {
		super(si, jsdl);
	}

	public JobObject(ServiceInterface si, DtoJob job) throws NoSuchJobException {
		super(si, job);
	}

	public JobObject(ServiceInterface si, DtoJob job,
			boolean refreshJobStatusOnBackend) throws NoSuchJobException {
		super(si, job, refreshJobStatusOnBackend);
	}

	public JobObject(ServiceInterface si, Map<String, String> jobProperties) {
		super(si, jobProperties);
	}

	public JobObject(ServiceInterface si, String jobname)
			throws NoSuchJobException {
		super(si, jobname);
	}

	public JobObject(ServiceInterface si, String jobname,
			boolean refreshJobStatusOnBackend) throws NoSuchJobException {
		super(si, jobname, refreshJobStatusOnBackend);
	}


}
