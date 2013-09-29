package grisu.frontend.view.cli;

import grisu.model.info.dto.DtoProperties;
import grisu.model.info.dto.DtoStringList;

import java.util.Map;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 27/09/13
 * Time: 12:57 PM
 */
abstract class AbstractAdminCliCommand extends GrisuCliCommand {

    protected DtoStringList execute(String command, Map<String, String> config) {
		DtoProperties props = null;
		if ( config != null ) {
			props = DtoProperties.createProperties(config);
		}

		DtoStringList result = si.admin(command, props);

		if ( result == null ) {
			return null;
		} else {
			return result;
		}
	}

	protected DtoStringList execute(String command) {
		return execute(command, null);
	}

}
