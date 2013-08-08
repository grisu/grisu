package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import grisu.control.JobConstants;
import grisu.control.exceptions.NoSuchJobException;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.jcommons.utils.OutputHelpers;
import grisu.jcommons.utils.WalltimeUtils;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoJob;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class GrisuStatusCliParameters extends GrisuCliCommand {

    @Parameter
    private List<String> jobnames = Lists.newArrayList();

    public List<String> getJobnames() {
        if ( jobnames.isEmpty() ) {
            return Lists.newArrayList();
        }
        return jobnames;
    }

    @Parameter(names = {"--summary"}, description = "displays a summary of all jobs. if this is set all other parameters will be ignored")
    private boolean summary = false;

    @Parameter(names = {"--details"}, description = "displays all job properties, not only status")
    private boolean details = false;

    public boolean isSummary() {
        return summary;
    }

    public boolean isDetails() {
        return details;
    }

    @Override
    public void execute() throws Exception {

        if (isSummary() || getJobnames().size() == 0) {

            UserEnvironmentManager uem = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();

            SortedSet<DtoJob> currentJobs = uem.getCurrentJobs(true);

            Integer active = 0;
            Integer finished = 0;
            Integer failed = 0;
            Integer success = 0;
            Integer unknown = 0;

            for (final DtoJob j : currentJobs) {
                if (j.getStatus() >= JobConstants.FINISHED_EITHER_WAY) {
                    finished = finished + 1;
                    if (j.getStatus() != JobConstants.DONE) {
                        failed = failed + 1;
                    } else {
                        success = success + 1;
                    }
                } else {
                    if ((j.getStatus() < JobConstants.PENDING)
                            || (j.getStatus() == JobConstants.NO_SUCH_JOB)) {
                        unknown = unknown + 1;
                    } else {
                        active = active + 1;
                    }
                }
            }

            System.out.println("Jobs:\n");
            final Map<String, String> table = Maps.newLinkedHashMap();
            table.put("Active", active.toString());
            table.put("Finished", finished.toString());
            // if (failed > 0) {
            table.put("   Successful:", success.toString());
            table.put("   Failed:", failed.toString());
            // }
            table.put("Broken/Not found:", unknown.toString());

            final String msg = OutputHelpers.getTable(table);

            System.out.println(msg);
            return;
        } else {
            for (String jobname : getJobnames()) {


                final DtoJob job;
                try {
                    job = si.getJob(jobname);
                } catch (NoSuchJobException e) {
                    System.err.println("No such job: "+jobname);
                    continue;
                }

                if (!isDetails()) {
                    System.out.println(jobname+"\t\t"+job.statusAsString());
                } else {

                    System.out.println("\nJob: " + jobname + "\n");

                    final Map<String, String> props = job.propertiesAsMap();
                    final Map<String, String> result = Maps.newTreeMap();

                    result.put(Constants.STATUS_STRING,
                            JobConstants.translateStatus(job.getStatus()));

                    for (final String key : props.keySet()) {

                        String valName = JobSubmissionProperty.getPrettyName(key);

                        if (StringUtils.isBlank(valName)) {
                            if (Constants.FQAN_KEY.equals(key)) {
                                valName = "group";
                            } else {
                                valName = key;
                            }
                        }

                        result.put(valName, formatAttribute(key, props.get(key)));

                    }

                    for (final String key : result.keySet()) {
                        System.out.println(key + " : " + result.get(key));
                    }
                    System.out.println("\n");
                }

            }
        }
    }

    private String formatAttribute(String aName, String aVal) {

        if (Constants.SUBMISSION_TIME_KEY.equals(aName)) {
            final Date d = new Date(Long.parseLong(aVal));
            return DateFormat.getInstance().format(d);
        } else if (Constants.MEMORY_IN_B_KEY.equals(aName)) {
            double memory = Long.parseLong(aVal);
            memory = memory / 1024.0 / 1024.0 / 1024.0;
            return String.format("%.2f GB", memory);

        } else if (Constants.WALLTIME_IN_MINUTES_KEY.equals(aName)) {
            final String[] strings = WalltimeUtils
                    .convertSecondsInHumanReadableString(Integer.parseInt(aVal) * 60);
            return StringUtils.join(strings, " ");
        } else {
            return aVal;
        }
    }
}
