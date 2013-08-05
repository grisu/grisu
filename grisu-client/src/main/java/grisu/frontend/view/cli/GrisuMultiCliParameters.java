package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;

public class GrisuMultiCliParameters extends GrisuCliParameters {

    @Parameter(names = {"-p", "--path"}, description = "path to gjob description")
    private String pathToJob = System.getProperty("user.dir");


    public String getPath() {
        return pathToJob;
    }



}
