package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import java.util.List;

public class GrisuStatusCliParameters {

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
}
