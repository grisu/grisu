package grisu.frontend.utils;

import com.beust.jcommander.internal.Maps;
import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.WalltimeUtils;
import grisu.model.GrisuRegistry;
import grisu.model.GrisuRegistryManager;
import grisu.settings.ClientPropertiesManager;
import grith.jgrith.cred.Cred;

import java.util.Date;
import java.util.Map;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 30/05/13
 * Time: 2:46 PM
 */
public class ClientPropertiesHelper {

    public static Map<String, String> gatherClientProperties(ServiceInterface si) {

        Map<String, String> temp = Maps.newLinkedHashMap();
        temp.put("Client name", LoginManager.getClientName());
        temp.put("Version", LoginManager.getClientVersion());
        temp.put("Grisu frontend version",
                grisu.jcommons.utils.Version.get("grisu-client-lib"));

        if (si == null) {

            temp.put("Documentation", "https://github.com/grisu/grisu");
            temp.put("also:", "https://wiki.auckland.ac.nz/display/CERES/Getting+started");

            temp.put("Contact", "support@nesi.org.nz");

            return temp;
        }

        GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
        Cred c = registry.getCredential();

        try {

            int remainingLifetime = c.getRemainingLifetime();
            String[] remainingString = WalltimeUtils
                    .convertSecondsInHumanReadableString(remainingLifetime);
            temp.put("Remaining session lifetime", remainingString[0] + " "
                    + remainingString[1] + " (" + remainingLifetime
                    + " seconds)");
            // env.printMessage("Remaining session lifetime: "
            // + remainingString[0] + " " + remainingString[1]);
        } catch (Exception e) {
            temp.put("Remaining session lifetime",
                    "can't determine (" + e.getLocalizedMessage() + ")");
            // env.printMessage("Remaining session lifetime: can't determine ("
            // + e.getLocalizedMessage() + ")");
        }

        if (c.isRenewable()) {
            temp.put("Session autorenews", "Yes");
        } else {
            temp.put("Session autorenews", "No");
        }

        temp.put("User ID", si.getDN());
        // env.printMessage("User ID: " + si.getDN());


        temp.put("Grisu backend version", si.getInterfaceInfo("VERSION"));
        temp.put("Grisu backend",
                (String) registry.get(Constants.BACKEND));
        temp.put("Grisu backend host", si
                .getInterfaceInfo("HOSTNAME"));
        temp.put("Grisu backend type", si
                .getInterfaceInfo("TYPE"));
        temp.put("Grisu API version", si.getInterfaceInfo("API_VERSION"));
        if (ClientPropertiesManager.isAdmin()) {
            String time = "n/a";
            try {
                time = new Date(Long.parseLong(si.getInterfaceInfo(ServiceInterface.LAST_CONFIG_UPDATE_KEY))).toString();
            } catch (Exception e) {
            }
            temp.put("Last config update: ", time);
            try {
                time = new Date(Long.parseLong(si.getInterfaceInfo(ServiceInterface.LAST_INFO_UPDATE_KEY))).toString();
            } catch (Exception e) {
            }
            temp.put("Last info update: ", time);
            try {
                time = new Date(Long.parseLong(si.getInterfaceInfo(ServiceInterface.LAST_TEMPLATES_UPDATE_KEY))).toString();
            } catch (Exception e) {
            }
            temp.put("Last templates update: ", time);
        }

        temp.put("Documentation", "https://github.com/grisu/gricli/wiki");
        temp.put("also:", "https://wiki.auckland.ac.nz/display/CERES/Getting+started");

        temp.put("Contact", "eresearch-admin@list.auckland.ac.nz");

        return temp;
    }

}
