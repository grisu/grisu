package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import java.util.List;

public class GrisuSubmitCliParameters {

    @Parameter
    private List<String> pathToJobs = Lists.newArrayList();

    public List<String> getPaths() {
        if ( pathToJobs.isEmpty() ) {
            return Lists.newArrayList(System.getProperty("user.dir"));
        }
        return pathToJobs;
    }



}
