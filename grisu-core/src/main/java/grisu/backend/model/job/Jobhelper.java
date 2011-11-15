package grisu.backend.model.job;

import grisu.backend.model.User;
import grisu.control.exceptions.JobPropertiesException;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jobhelper {

	private static Logger myLogger = LoggerFactory.getLogger(Jobhelper.class
			.getName());

	public static String calculateJobname(User user, String jobname,
			String jobnameCreationMethod) throws JobPropertiesException {

		synchronized (user) {

			if ((jobnameCreationMethod == null)
					|| Constants.FORCE_NAME_METHOD.equals(jobnameCreationMethod)) {

				if (jobname == null) {
					throw new JobPropertiesException(
							JobSubmissionProperty.JOBNAME.toString()
							+ ": "
							+ "Jobname not specified and job creation method is force-name.");
				}

				final String[] allJobnames = user.getJobManager()
						.getAllJobnames(null).asArray();
				Arrays.sort(allJobnames);
				if (Arrays.binarySearch(allJobnames, jobname) >= 0) {
					throw new JobPropertiesException(
							JobSubmissionProperty.JOBNAME.toString()
							+ ": "
							+ "Jobname "
							+ jobname
							+ " already exists and job creation method is force-name.");
				}
			} else if (Constants.UUID_NAME_METHOD.equals(jobnameCreationMethod)) {
				if (jobname != null) {
					jobname = jobname + "_" + UUID.randomUUID().toString();
				} else {
					jobname = UUID.randomUUID().toString();
				}
			} else if (Constants.TIMESTAMP_METHOD.equals(jobnameCreationMethod)) {

				final String[] allJobnames = user.getJobManager()
						.getAllJobnames(null).asArray();
				Arrays.sort(allJobnames);

				String temp;
				do {
					final String timestamp = new Long(new Date().getTime())
					.toString();
					try {
						Thread.sleep(1);
					} catch (final InterruptedException e) {
						myLogger.debug(e.getLocalizedMessage(), e);
					}

					temp = jobname;
					if (temp == null) {
						temp = timestamp;
					} else {
						temp = temp + "_" + timestamp;
					}
				} while (Arrays.binarySearch(allJobnames, temp) >= 0);

				jobname = temp;

			} else if (Constants.UNIQUE_NUMBER_METHOD.equals(jobnameCreationMethod)) {

				final SortedSet<String> jobNames = user.getJobManager()
						.getAllJobnames(
								Constants.ALLJOBS_INCL_BATCH_KEY).asSortedSet();
				jobNames.addAll(user.getBatchJobManager()
						.getAllBatchJobnames(null).asSortedSet());

				int max = -1;

				for (final String jn : jobNames) {
					if (jn.equals(jobname)) {
						if (max < 0) {
							max = 1;
						}
					} else {
						if (jn.startsWith(jobname)) {
							final int index = jn.lastIndexOf("_");
							try {
								final String integerString = jn
										.substring(index + 1);
								final int value = Integer.parseInt(integerString);
								if (value > max) {
									max = value;
								}
							} catch (final Exception e) {
							}
						}
					}
				}

				if (max != -1) {
					jobname = jobname + "_" + (max + 1);
				}

			} else {
				throw new JobPropertiesException(
						JobSubmissionProperty.JOBNAME.toString() + ": "
								+ "Jobname creation method "
								+ jobnameCreationMethod + " not supported.");
			}

			if (jobname == null) {
				throw new RuntimeException(
						"Jobname is null. This should never happen. Please report to markus.binsteiner@arcs.org.au");
			}

			return jobname;
		}
	}
}
