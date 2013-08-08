package grisu.frontend.view.cli;

import com.beust.jcommander.internal.Lists;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoJob;

import java.util.List;
import java.util.SortedSet;

public class GrisuListCliParameters extends GrisuCliCommand {

    @Override
    public void execute() throws Exception {

        UserEnvironmentManager uem = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();

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


}
