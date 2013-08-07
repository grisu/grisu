package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import java.util.List;

public class GrisuWaitParameters {

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

    public Integer getWaittime() {
        return waittime;
    }



}
