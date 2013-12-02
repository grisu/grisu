package grisu.frontend.view.cli;

import grisu.frontend.utils.ClientPropertiesHelper;
import grisu.jcommons.utils.OutputHelpers;

import java.util.Map;

public class GrisuAboutParameters extends AbstractAdminCliCommand {

    @Override
    public void execute() throws Exception {

        Map<String, String> temp = ClientPropertiesHelper.gatherClientProperties(si);

        String output = OutputHelpers.getTable(temp);

        System.out.println(output);
    }




}
