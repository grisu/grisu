package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import java.util.List;

public class GrisuViewCliParameters {

    @Parameter
    private List<String> paths = Lists.newArrayList();

    public List<String> getPaths() {
        if ( paths.isEmpty() ) {
            return Lists.newArrayList();
        }
        return paths;
    }



}
