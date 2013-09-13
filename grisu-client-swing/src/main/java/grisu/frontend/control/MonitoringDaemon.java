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
import grisu.jcommons.processes.ExternalCommand;
import grisu.settings.ClientPropertiesManager;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 12/09/13
 * Time: 12:11 PM
 */
public class MonitoringDaemon {

    public static void main(String[] args) throws Exception {

        ServiceInterface si = LoginManager.login("nesi");

        MonitoringDaemon md = new MonitoringDaemon(si, true);
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
        md.monitor("Java", "processed", files, new File("/home/markus/Desktop/joboutput"), true, command);

    }

    private final ServiceInterface si;
    private final RunningJobManager rjm;

    private final boolean displayDialog;
    private final SingleJobTabbedFrame dialog;

    private final Map<Long, String> log = Collections.synchronizedMap(new TreeMap<Long, String>());
    private final Set<GrisuJob> ignoreList = Collections.synchronizedSet(new HashSet<GrisuJob>());

    private final ExecutorService executor;

    boolean verbose = true;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public MonitoringDaemon(ServiceInterface si, boolean displayDialog) {
        executor = Executors.newFixedThreadPool(10);
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

    public void monitor(final String application, final String processed_tag, final Collection<String> filesToDownload, final File targetDir, final boolean deleteJobIfDownloadSuccessful, final List<String> postProcessCommand) throws IOException, RemoteFileSystemException {

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

        while (true) {
            for (final GrisuJob j : list) {
                if (verbose) {
                    addLog("Job '" + j.getJobname() + "': " + j.getStatusString(false));
                }
                //addLog("Job: " + j.getJobname() + ":\t" + j.getStatusString(false));
                //System.out.println("Checking: " + j.getJobname());
                if (ignoreList.contains(j)) {
                    //System.out.println("Ignoring: " + j.getJobname());
                    if (verbose) {
                        addLog("Job '" + j.getJobname() + "': ignored");
                    }
                    continue;
                }

                boolean finished = j.isSuccessful(false);

                if (finished) {
                    try {
                        String value = j.getJobProperty(processed_tag, true);
                        if (Boolean.parseBoolean(value)) {
                            ignoreList.add(j);
                            addLog("Ignoring because already processed: " + j.getJobname());
                            continue;
                        }
                    } catch (Exception e) {
                        addLog("Error getting job properties from '" + j.getJobname() + "': " + e.getLocalizedMessage());
                        continue;
                    }

                    // adding this to the ignore list so it doesn't get processed twice
                    ignoreList.add(j);

                    Thread t = new Thread() {
                        public void run() {
                            postprocess(j, processed_tag, filesToDownload, targetDir, deleteJobIfDownloadSuccessful, postProcessCommand);
                        }
                    };

                    executor.execute(t);

                } else {
                    if (verbose) {
                        addLog("Job '" + j.getJobname() + "': not finished, ignoring for now...");
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

    private void postprocess(GrisuJob j, String processed_tag, Collection<String> filesToDownload, File targetDir, boolean deleteJobIfDownloadSuccessful, List<String> postProcessCommand) {

        try {
            addLog("Job '" + j.getJobname() + "' finished. Start postprocessing...");
            File downloadDir = postprocessDownload(j, filesToDownload, targetDir);

            ExternalCommand ec = new ExternalCommand(postProcessCommand);
            ec.setWorkingDirectory(downloadDir.getAbsolutePath());

            ec.execute();

            File stdout = new File(downloadDir, "postprocess_stdout.txt");
            File stderr = new File(downloadDir, "postprocess_stderr.txt");

            FileUtils.writeLines(stdout, ec.getStdout());
            FileUtils.writeLines(stderr, ec.getStderr());

            if (deleteJobIfDownloadSuccessful) {
                try {
                    addLog("Cleaning job '" + j.getJobname());
                    j.kill(true);
                } catch (Exception e) {
                    addLog("Cleaning of job '" + j.getJobname() + "' failed: " + e.getLocalizedMessage(), true);
                }
            } else {

                // once everything is finished we mark the job as processed
                try {
                    addLog("Job '" + j.getJobname() + "': postprocessing finished.");
                    si.addJobProperty(j.getJobname(), processed_tag, "true");
                } catch (NoSuchJobException e) {
                    addLog("Could not find job: " + j.getJobname(), true);
                    return;
                }
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

    public File postprocessDownload(GrisuJob job, Collection<String> paths, File targetDir) throws RemoteFileSystemException, IOException {

        File targetJobDir = new File(targetDir, job.getJobname());
        targetJobDir.mkdirs();

        if (!targetJobDir.exists()) {
            throw new IOException("Can't create directory: " + targetJobDir.getAbsolutePath());
        }

        addLog("Job: " + job.getJobname() + ": start downloading files");

        for (String path : paths) {

            long origSize = job.getFileSize(path);

            addLog("Job '" + job.getJobname() + "': downloading file '" + path + "'");
            File file = job.downloadOutput(path, ClientPropertiesManager.getFileDownloadRetries(), ClientPropertiesManager.getFileDownloadRetryInterval());

            long newSize = file.length();

            if (origSize != newSize) {
                throw new RemoteFileSystemException("Downloaded filesize (" + newSize + "b) differs from original size (" + origSize + "b).");
            }

            File targetFile = new File(targetJobDir, file.getName());
            if (targetFile.exists()) {
                File backup = new File(targetJobDir, file.getName() + ".bak");
                if (backup.exists()) {
                    FileUtils.deleteQuietly(backup);
                }
                addLog("Target file exists, moving it to: " + backup.getAbsolutePath());
                FileUtils.moveFile(targetFile, backup);
            }

            FileUtils.moveFile(file, targetFile);
            addLog("Job '" + job.getJobname() + "': file '" + path + "' downloaded to: " + targetFile.getAbsolutePath());
        }

        return targetJobDir;

    }
}
