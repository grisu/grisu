package grisu.control.serviceInterfaces;

import grisu.jcommons.constants.Constants;
import grisu.settings.ServerPropertiesManager;
import grith.jgrith.voms.VOManagement.VOManagement;

import java.util.Map;

public class AdminInterface {



	public AdminInterface() {
	}

	public String execute(String command, Map<String, String> config) {
		if (Constants.REFRESH_VOS.equals(command)) {
			return refreshVos();
		} else if (Constants.REFRESH_CONFIG.equals(command)) {
			return refreshConfig();
		}

		return "\"" + command + "\" is not a valid admin command.";
	}

	private String refreshConfig() {
		ServerPropertiesManager.refreshConfig();
		return "Config refreshed.";
	}

	private String refreshVos() {

		refreshConfig();
		VOManagement.refreshAllVOs();

		return "VOs refreshed.";


	}

}
