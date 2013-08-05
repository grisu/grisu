import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Lists;
import grisu.control.ServiceInterface;
import grisu.frontend.control.GJob;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.frontend.view.cli.GrisuListCliParameters;
import grisu.frontend.view.cli.GrisuSubmitCliParameters;
import grisu.frontend.view.cli.GrisuCliClient;
import grisu.frontend.view.cli.GrisuMultiCliParameters;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoJob;
import grith.jgrith.cred.Cred;

import java.io.File;
import java.util.List;
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

    final String command;

    public Grisu(GrisuMultiCliParameters params, GrisuSubmitCliParameters submitParams, GrisuListCliParameters listParams, String[] args) throws Exception {
        super(params, args);
        this.args = args;
        this.mainParams = params;
        this.submitParams = submitParams;
        this.listParams = listParams;
        jc = new JCommander(params);
        jc.setProgramName("grisu");
        jc.addCommand("submit", submitParams);
        jc.addCommand("list", listParams);

        jc.parse(args);

        command = jc.getParsedCommand();

    }

    public static void main(String[] args) {

        LoginManager.initGrisuClient("grisu");

        GrisuMultiCliParameters params = new GrisuMultiCliParameters();
        GrisuSubmitCliParameters submitParams = new GrisuSubmitCliParameters();
        GrisuListCliParameters listParams = new GrisuListCliParameters();
        Grisu s = null;
        try {
            s = new Grisu(params, submitParams, listParams, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not execute command: "
                    + e.getLocalizedMessage());
            System.exit(1);
        }
        s.run();

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
