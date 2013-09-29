package grisu.frontend.view.cli;

import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

public class GrisuReloadInfoCliParameters extends AbstractAdminCliCommand {

    @Override
    public void execute() throws Exception {

        DtoStringList result = execute(Constants.REFRESH_GRID_INFO);
		for (String line : result.asArray()) {
			System.out.println(line);
		}
    }




}
