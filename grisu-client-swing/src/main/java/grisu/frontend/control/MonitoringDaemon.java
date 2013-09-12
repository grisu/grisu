package grisu.frontend.control;

import ca.odell.glazedlists.EventList;
import com.beust.jcommander.internal.Lists;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.frontend.view.swing.jobmonitoring.single.SingleJobTabbedFrame;
import grisu.jcommons.constants.Constants;
import grisu.settings.ClientPropertiesManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 12/09/13
 * Time: 12:11 PM
 */
public class MonitoringDaemon {

    public static void main(String[] args) throws Exception {

        ServiceInterface si = LoginManager.login("bestgrid");

        MonitoringDaemon md = new MonitoringDaemon(si, true);
        List<String> files = Lists.newArrayList();
//        files.add("stdout.txt");
//        files.add("stderr.txt");
//        files.add(".job.ll");
        files.add("output.zip");

        md.monitor(Constants.ALLJOBS_KEY, "testtag", files, new File("/home/markus/Desktop/joboutput"));

    }

    private final ServiceInterface si;
    private final RunningJobManager rjm;

    private final boolean displayDialog;
    private final SingleJobTabbedFrame dialog;

    private final Map<Long, String> log = Collections.synchronizedMap(new TreeMap<Long, String>());

    public MonitoringDaemon(ServiceInterface si, boolean displayDialog) {
        this.si = si;
        this.rjm = RunningJobManager.getDefault(si);
        this.displayDialog = displayDialog;

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

    private void addLog(String msg) {
        addLog(msg, false);
    }

    private synchronized void addLog(String msg, boolean error) {

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
            System.out.println(msg);
        }

        System.out.println(error + ": " + msg);
    }

    public void monitor(String application, String processed_tag, Collection<String> filesToDownload, File targetDir) throws IOException, RemoteFileSystemException {

        addLog("Starting monitoring daemon for application: " + application);

        if (filesToDownload != null && filesToDownload.size() > 0) {
            if (!targetDir.exists()) {
                targetDir.mkdirs();
                if (!targetDir.exists()) {
                    String msg = "Can't create target dir: " + targetDir.getAbsolutePath();
                    addLog(msg, true);
                    throw new IOException(msg);
                }
            }

            if (!targetDir.isDirectory()) {
                String msg = "Target dir already exists and is not directory: " + targetDir.getAbsolutePath();
                addLog(msg, true);
                throw new IOException(msg);
            }
        }

        EventList<GrisuJob> list = rjm.getJobs(application);

        Set<GrisuJob> ignoreList = Collections.synchronizedSet(new HashSet<GrisuJob>());

        while (true) {
            for (GrisuJob j : list) {
                //addLog("Job: " + j.getJobname() + ":\t" + j.getStatusString(false));
                //System.out.println("Checking: " + j.getJobname());
                if (ignoreList.contains(j)) {
                    //System.out.println("Ignoring: " + j.getJobname());
                    continue;
                }

                boolean finished = j.isSuccessful(false);

                if (finished) {
                    String value = j.getJobProperty(processed_tag, true);
                    if (Boolean.parseBoolean(value)) {
                        ignoreList.add(j);
                        addLog("Ignoring because already processed: " + j.getJobname());
                        continue;
                    }

                    // adding this to the ignore list so it doesn't get processed twice
                    ignoreList.add(j);

                    try {
                        addLog("Job '" + j.getJobname() + "' finished. Start postprocessing...");
                        postprocessDownload(j, filesToDownload, targetDir);

                        // once everything is finished we mark the job as processed
                        try {
                            addLog("Job '"+j.getJobname()+"': postprocessing finished.");
                            si.addJobProperty(j.getJobname(), processed_tag, "true");
                        } catch (NoSuchJobException e) {
                            addLog("Could not find job: " + j.getJobname(), true);
                            continue;
                        }

                    } catch (Exception e) {
                        addLog("Error postprocessing job '" + j.getJobname() + "': " + e.getLocalizedMessage() + ". Queuing it for postprocessing again...", true);
                        try {
                            ignoreList.remove(j);
                            si.addJobProperty(j.getJobname(), processed_tag, "false");
                        } catch (NoSuchJobException e1) {
                            // ignoring this
                            addLog("Job '" + j.getJobname() + "': error re-queuing because job was not found anymore.", true);
                        }
                    }
                }

            }
            System.out.println("------------------------------");
            try {
                Thread.sleep((ClientPropertiesManager.getDefaultJobStatusRecheckInterval() / 2) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    public void postprocessDownload(GrisuJob job, Collection<String> paths, File targetDir) throws RemoteFileSystemException, IOException {

        File targetJobDir = new File(targetDir, job.getJobname());
        targetJobDir.mkdirs();

        if ( !targetJobDir.exists() ) {
            throw new IOException("Can't create directory: "+targetJobDir.getAbsolutePath());
        }

        addLog("Job: " + job.getJobname() + ": start downloading files");

        for (String path : paths) {

            addLog("Job '" + job.getJobname() + "': downloading file '" + path + "'");
            File file = job.downloadOutput(path, ClientPropertiesManager.getFileDownloadRetries(), ClientPropertiesManager.getFileDownloadRetryInterval());

            File targetFile = new File(targetJobDir, file.getName());
            if ( targetFile.exists() ) {
                File backup = new File(targetJobDir, file.getName()+".bak");
                if ( backup.exists() ) {
                    FileUtils.deleteQuietly(backup);
                }
                addLog("Target file exists, moving it to: "+backup.getAbsolutePath());
                FileUtils.moveFile(targetFile, backup);
            }

            FileUtils.moveFile(file, targetFile);
            addLog("Job '" + job.getJobname() + "': file '" + path + "' downloaded to: " + targetFile.getAbsolutePath());
        }

    }
}
