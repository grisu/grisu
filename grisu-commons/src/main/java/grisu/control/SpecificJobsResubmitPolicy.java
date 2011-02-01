package grisu.control;

import grisu.jcommons.constants.Constants;
import grisu.model.dto.DtoProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


public class SpecificJobsResubmitPolicy implements ResubmitPolicy {

	private final Set<String> jobnames;
	private final Set<String> submissionLocations;

	public SpecificJobsResubmitPolicy(Set<String> jobnames,
			Set<String> submissionLocations) {
		this.jobnames = jobnames;
		this.submissionLocations = submissionLocations;
	}

	public String getName() {
		return Constants.SUBMIT_POLICY_RESTART_SPECIFIC_JOBS;
	}

	public DtoProperties getProperties() {

		final Map<String, String> result = new HashMap<String, String>();
		result.put(Constants.JOBNAMES_TO_RESTART,
				StringUtils.join(jobnames, ","));
		result.put(Constants.SUBMISSIONLOCATIONS_TO_RESTART,
				StringUtils.join(submissionLocations, ","));

		return DtoProperties.createProperties(result);
	}

}
