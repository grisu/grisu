package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.swing.LogPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 12/09/13
 * Time: 7:24 PM
 */
public class SingleJobTabbedFrame extends JFrame {

    public static void main(String[] args) throws Exception {

        ServiceInterface si = LoginManager.login("bestgrid");
        String application = Constants.ALLJOBS_KEY;
        SingleJobTabbedDialog dialog = new SingleJobTabbedDialog(si, Constants.ALLJOBS_KEY);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);

    }

    private SingleJobTabbedPane pane;
    private SingleJobTabbedPane singleJobTabbedPane1;
    private final ServiceInterface si;
    private final String application;
    private LogPanel logPanel;


    public SingleJobTabbedFrame(ServiceInterface si, String application) {

        this.si = si;
        this.application = application;

        logPanel = new LogPanel();
        singleJobTabbedPane1 = new SingleJobTabbedPane(si, application);
        singleJobTabbedPane1.addTab("Monitor log", logPanel, 0);
        setTitle("Monitoring: " + application);

        getContentPane().add(singleJobTabbedPane1, BorderLayout.CENTER);
    }

    public void addMessage(String msg) {
        logPanel.addMessage(msg);
    }

    public void addError(String msg) {
        logPanel.addWarningMessage(msg);
    }

}
