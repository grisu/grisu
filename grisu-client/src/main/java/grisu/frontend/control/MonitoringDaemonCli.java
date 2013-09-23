package grisu.frontend.control;

import grisu.control.ServiceInterface;

import java.util.Date;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 23/09/13
 * Time: 3:55 PM
 */
public class MonitoringDaemonCli extends MonitoringDaemon {

    public MonitoringDaemonCli(ServiceInterface si) {
        super(si);
    }

    protected synchronized void addLog(String msg, boolean error) {

        Long now = new Date().getTime();
        while (log.get(now) != null) {
            now = now + 1;
        }

        log.put(now, msg);

        if ( error ) {
            System.err.println(msg);
        } else {
            System.out.println(msg);
        }

    }

}
