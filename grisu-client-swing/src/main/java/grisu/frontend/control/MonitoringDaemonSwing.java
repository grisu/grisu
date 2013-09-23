package grisu.frontend.control;

import com.beust.jcommander.internal.Lists;
import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.swing.jobmonitoring.single.SingleJobTabbedFrame;
import grisu.jcommons.constants.Constants;

import javax.swing.*;
import java.util.Date;
import java.util.List;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 12/09/13
 * Time: 12:11 PM
 */
public class MonitoringDaemonSwing extends MonitoringDaemon {

    public static void main(String[] args) throws Exception {

        ServiceInterface si = LoginManager.login("nesi");

        MonitoringDaemonSwing md = new MonitoringDaemonSwing(si);
        md.setVerbose(true);
        List<String> files = Lists.newArrayList();
//        files.add("stdout.txt");
//        files.add("stderr.txt");
//        files.add(".job.ll");
        files.add("output.zip");

        //md.monitor(Constants.ALLJOBS_KEY, "testtag", files, new File("/home/markus/Desktop/joboutput"));
        List<String> command = Lists.newArrayList();
        command.add("sh");
        command.add("/home/markus/local/bin/test_script.sh");
//        md.monitor("Java", "processed", files, new File("/home/markus/Desktop/joboutput"), true, command);

    }


    private final boolean displayDialog;
    private final SingleJobTabbedFrame dialog;


    public MonitoringDaemonSwing(ServiceInterface si) {
        super(si);
        this.displayDialog = true;

        if (displayDialog) {

            dialog = new SingleJobTabbedFrame(si, Constants.ALLJOBS_KEY);
            dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dialog.pack();

            new Thread() {
                public void run() {
                    dialog.setVisible(true);
                }
            }.start();

        } else {
            dialog = null;
        }
    }

    @Override
    protected synchronized void addLog(String msg, boolean error) {

        Long now = new Date().getTime();
        while (log.get(now) != null) {
            now = now + 1;
        }

        log.put(now, msg);

        if (displayDialog) {
            if (error) {
                dialog.addError(msg);
            } else {
                dialog.addMessage(msg);
            }
        } else {
            if ( error ) {
                System.err.println(msg);
            } else {
                System.out.println(msg);
            }
        }

    }

}
