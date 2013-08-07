import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import grisu.control.JobConstants;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.GJob;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.frontend.view.cli.*;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.jcommons.utils.OutputHelpers;
import grisu.jcommons.utils.WalltimeUtils;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoJob;
import grith.jgrith.cred.Cred;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 5/08/13
 * Time: 3:32 PM
 */
public class Grisu extends GrisuCliClient<GrisuMultiCliParameters> {

    public static ServiceInterface serviceInterface = null;
    public static Cred credential = null;
    //    public static List<String> cli_parameters;
    public final String[] args;
    final JCommander jc;
    final GrisuMultiCliParameters mainParams;
    final GrisuSubmitCliParameters submitParams;
    final GrisuListCliParameters listParams;
    final GrisuWaitParameters waitParams;
    final GrisuStatusCliParameters statusParams;
    final GrisuViewCliParameters viewParams;

    final String command;

    public Grisu(GrisuMultiCliParameters params, GrisuSubmitCliParameters submitParams, GrisuListCliParameters listParams, GrisuWaitParameters waitParams, GrisuStatusCliParameters statusParams, GrisuViewCliParameters viewParams, String[] args) throws Exception {
        super(params, args);

        this.args = args;
        this.mainParams = params;
        this.submitParams = submitParams;
        this.listParams = listParams;
        this.waitParams = waitParams;
        this.statusParams = statusParams;
        this.viewParams = viewParams;

        jc = new JCommander(params);
        jc.setProgramName("grisu");
        jc.addCommand("submit", submitParams);
        jc.addCommand("list", listParams);
        jc.addCommand("wait", waitParams);
        jc.addCommand("status", statusParams);

        jc.parse(args);

        command = jc.getParsedCommand();

    }

    public static void main(String[] args) {

        LoginManager.initGrisuClient("grisu");

        GrisuMultiCliParameters params = new GrisuMultiCliParameters();
        GrisuSubmitCliParameters submitParams = new GrisuSubmitCliParameters();
        GrisuListCliParameters listParams = new GrisuListCliParameters();
        GrisuWaitParameters waitParams = new GrisuWaitParameters();
        GrisuStatusCliParameters statusParams = new GrisuStatusCliParameters();
        GrisuViewCliParameters viewParams = new GrisuViewCliParameters();

        Grisu s = null;
        try {
            s = new Grisu(params, submitParams, listParams, waitParams, statusParams, viewParams, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not execute command: "
                    + e.getLocalizedMessage());
            System.exit(1);
        }
        s.run();

    }

    private void waitForJobToFinish() {

        List<String> jobnames = waitParams.getJobnames();

        for (String jobname : jobnames) {

            try {
                final GrisuJob job = new GrisuJob(serviceInterface, jobname);
                job.waitForJobToFinish(waitParams.getWaittime());
                System.out.println(job.getJobname()+":\t\t"+job.getStatusString(false));
            } catch (final NoSuchJobException e) {
                System.err.println("No such job: " + jobname);
                continue;
            }
        }

    }

    private void status() {

        if (statusParams.isSummary() || statusParams.getJobnames().size() == 0) {

            UserEnvironmentManager uem = GrisuRegistryManager.getDefault(serviceInterface).getUserEnvironmentManager();

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
            for (String jobname : statusParams.getJobnames()) {


                final DtoJob job;
                try {
                    job = serviceInterface.getJob(jobname);
                } catch (NoSuchJobException e) {
                    System.err.println("No such job: "+jobname);
                    continue;
                }

                if (!statusParams.isDetails()) {
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

    private void list() {
        UserEnvironmentManager uem = GrisuRegistryManager.getDefault(serviceInterface).getUserEnvironmentManager();

        SortedSet<DtoJob> currentJobs = uem.getCurrentJobs(true);

        List<List<String>> table = Lists.newArrayList();
        for ( DtoJob j : currentJobs ) {
            List<String> temp = Lists.newArrayList();
            temp.add(j.jobname());
            temp.add(j.statusAsString());
            table.add(temp);
        }

        String tableString = OutputHelpers.getTable(table);

        System.out.println(tableString);

    }

    private void submit(GrisuSubmitCliParameters submitParams) throws Exception {

        List<String> paths = submitParams.getPaths();

        List<GJob> jobs = Lists.newArrayList();

        for (String path : paths) {


            File job = new File(path);
            if (!job.exists()) {
                throw new Exception("Job does not exist: " + path);
            }

            File jobDir = null;

            if (job.isDirectory()) {
                jobDir = job;
                job = new File(jobDir, GJob.SUBMIT_PROPERTIES_FILE_NAME);
                if (!job.exists()) {
                    job = new File(jobDir, GJob.JOB_PROPERTIES_FILE_NAME);
                    if (!job.exists()) {
                        throw new Exception("Job does not exist: " + path);
                    }
                }
            } else {
                jobDir = job.getParentFile();
            }

            System.out.println("Reading job: " + job.getAbsolutePath());
            GJob gJob = new GJob(job.getAbsolutePath());

            jobs.add(gJob);
        }

        List<GrisuJob> grisuJobs = Lists.newArrayList();
        for ( GJob gJob : jobs ) {
            System.out.println("Creating job: "+gJob.getJobname());
            GrisuJob grisuJob = gJob.createJobDescription(serviceInterface);
            grisuJobs.add(grisuJob);
        }

        for ( GrisuJob grisuJob : grisuJobs ) {
            System.out.println("Submitting job: "+grisuJob.getJobname());
            grisuJob.submitJob();

            System.out.println("Job submitted, final jobname: " + grisuJob.getJobname());
        }
    }

    @Override
    protected void run() {

        try {

            if (getLoginParameters().isNologin()) {

                System.out.println("Doing nothing...");
                System.exit(0);
            } else {

                credential = getCredential();
                serviceInterface = getServiceInterface();

                if ("submit".equals(command)) {
                    submit(submitParams);
                } else if ("list".equals(command)) {
                    list();
                } else if ( "wait".equals(command)) {
                    waitForJobToFinish();
                } else if ( "status".equals(command)) {
                    status();
                } else {
                    System.out.println("No command: " + command);
                    jc.usage();
                    System.exit(1);
                }

                System.exit(0);

            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getLocalizedMessage());
//            e.printStackTrace();
            System.exit(2);
        }

        System.exit(0);
    }

}
