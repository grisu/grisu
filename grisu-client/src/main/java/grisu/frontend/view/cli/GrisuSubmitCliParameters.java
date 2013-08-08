package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;
import grisu.frontend.control.GJob;
import grisu.frontend.model.job.GrisuJob;

import java.io.File;
import java.util.List;

public class GrisuSubmitCliParameters extends GrisuCliCommand {

    @Parameter
    private List<String> pathToJobs = Lists.newArrayList();

//    @Parameter(names = {"--jobname"}, description = "overwrites the name of the job")
//    private String jobname = null;
//
//    public String getJobname() {
//        return jobname;
//    }

    public List<String> getPaths() {
        if ( pathToJobs.isEmpty() ) {
            return Lists.newArrayList(System.getProperty("user.dir"));
        }
        return pathToJobs;
    }

    public void execute() throws Exception {

        List<String> paths = getPaths();

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
            GrisuJob grisuJob = gJob.createJobDescription(si);
            grisuJobs.add(grisuJob);
        }

        for ( GrisuJob grisuJob : grisuJobs ) {
            System.out.println("Submitting job: "+grisuJob.getJobname());
            grisuJob.submitJob();

            System.out.println("Job submitted, final jobname: " + grisuJob.getJobname());
        }

    }
}
