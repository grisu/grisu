package grisu.frontend.control.jobMonitoring;

import grisu.control.ServiceInterface;
import grisu.settings.ClientPropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 20/09/13
 * Time: 7:38 AM
 */
public class RunningJobManagerManager {

    private static final Logger myLogger = LoggerFactory.getLogger(RunningJobManagerManager.class);

    private static Map<ServiceInterface, RunningJobManager> cachedRegistries = new HashMap<ServiceInterface, RunningJobManager>();


    public static RunningJobManager getDefault(final ServiceInterface si) {

        if (si == null) {
            throw new RuntimeException(
                    "ServiceInterface not initialized yet. Can't get default registry...");
        }

        synchronized (si) {
            if (cachedRegistries.get(si) == null) {
                String rjm = ClientPropertiesManager.getProperty("runningJobManager");
                RunningJobManager m;
                if ( "new".equalsIgnoreCase(rjm)) {
                    m = new RunningJobManagerImpl(si);
                } else {
                    m = new RunningJobManagerOld(si);
                }

                cachedRegistries.put(si, m);
            }
        }

        return cachedRegistries.get(si);
    }
}
