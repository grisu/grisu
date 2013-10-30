package grisu.frontend.view.cli;

import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

public class GrisuReloadTemplatesCliParameters extends AbstractAdminCliCommand {

    @Override
    public void execute() throws Exception {

        DtoStringList result = execute(Constants.REFRESH_TEMPLATES);
		for (String line : result.asArray()) {
			System.out.println(line);
		}
    }




}
