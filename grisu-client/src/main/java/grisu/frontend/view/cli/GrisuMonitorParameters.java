package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import grisu.control.ServiceInterface;
import grisu.frontend.control.MonitoringConfig;
import grisu.frontend.control.MonitoringConfigParser;
import grisu.frontend.control.MonitoringDaemon;
import grisu.frontend.control.MonitoringDaemonCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public class GrisuMonitorParameters extends GrisuCliCommand {

    private static final Logger myLogger = LoggerFactory.getLogger(GrisuMonitorParameters.class);

    private MonitoringDaemon md;


    @Parameter(names = {"-w", "--waittime"}, description = "seconds to wait inbetween status checks, default: 60 (don't use very small numbers because of the load this would put on the backend")
    private Integer waittime = 60;


    public Integer getWaittime() {
        return waittime;
    }

    public void setWaittime(Integer waittime) {
        this.waittime = waittime;
    }

    @Parameter(names = {"--gui"}, description = "display graphical job monitoring window (if available)")
    private boolean visual = false;

    public void setVisual(boolean visual) {
        this.visual = visual;
    }

    public boolean isVisual() {
        return visual;
    }

    @Parameter(description =  "path to configurations")
    private List<String> configs;

    public List<String> getConfigs() {
        return configs;
    }

    @Override
    public void execute() throws Exception {

        MonitoringDaemon monitoringDaemon = null;
        try {
            Class<?> monitor = Class.forName("grisu.frontend.control.MonitoringDaemonSwing");
            Constructor monitoringDaemonConstructor = monitor.getConstructor(ServiceInterface.class);
            monitoringDaemon = (MonitoringDaemon) monitoringDaemonConstructor.newInstance(si);
        } catch (Exception e) {
            myLogger.debug("grisu-client-swing not in path, using non-gui monitoring daemon: {}", e.getLocalizedMessage(), e);
            monitoringDaemon = new MonitoringDaemonCli(si);
        }



        Collection<MonitoringConfig> configs = Lists.newArrayList();
        for ( String config : getConfigs() ) {
            configs.addAll(MonitoringConfigParser.parseConfig(config));
        }

//        monitoringDaemon.setVerbose(true);

        monitoringDaemon.monitor(configs);

    }

    @Override
    public boolean exitAfterExecution() {
        return false;
    }
}
