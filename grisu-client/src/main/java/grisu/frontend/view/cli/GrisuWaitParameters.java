package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.GrisuJob;

import java.util.List;

public class GrisuWaitParameters extends GrisuCliCommand {

    @Parameter
    private List<String> jobnames = Lists.newArrayList();
    @Parameter(names = {"-w", "--waittime"}, description = "seconds to wait inbetween status checks, default: 60 (don't use very small numbers because of the load this would put on the backend")
    private Integer waittime = 60;

    public List<String> getJobnames() {
        if ( jobnames.isEmpty() ) {
            return Lists.newArrayList();
        }
        return jobnames;
    }

    public void setJobnames(List<String> jobnames) {
        this.jobnames = jobnames;
    }

    public Integer getWaittime() {
        return waittime;
    }

    public void setWaittime(Integer waittime) {
        this.waittime = waittime;
    }

    @Override
    public void execute() throws Exception {

        List<String> jobnames = getJobnames();

        for (String jobname : jobnames) {

            try {
                final GrisuJob job = new GrisuJob(si, jobname);
                job.waitForJobToFinish(getWaittime());
                System.out.println(job.getJobname()+":\t\t"+job.getStatusString(false));
            } catch (final NoSuchJobException e) {
                System.err.println("No such job: " + jobname);
                continue;
            }
        }
    }
}
