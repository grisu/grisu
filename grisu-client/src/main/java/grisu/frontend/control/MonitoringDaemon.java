package grisu.frontend.control;

import ca.odell.glazedlists.EventList;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Maps;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.frontend.control.jobMonitoring.RunningJobManagerManager;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.job.GrisuJob;
import grisu.jcommons.processes.ExternalCommand;
import grisu.settings.ClientPropertiesManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
abstract public class MonitoringDaemon {

    private static final Logger myLogger = LoggerFactory.getLogger(MonitoringDaemon.class);

    public static void main(String[] args) throws Exception {

        ServiceInterface si = LoginManager.login("nesi");

        MonitoringDaemon md = new MonitoringDaemonCli(si);
        md.setVerbose(true);
//        List<String> files = Lists.newArrayList();
//        files.add("stdout.txt");
//        files.add("stderr.txt");
//        files.add(".job.ll");
//        files.add("output.zip");

        //md.monitor(Constants.ALLJOBS_KEY, "testtag", files, new File("/home/markus/Desktop/joboutput"));
//        List<String> command = Lists.newArrayList();
//        command.add("sh");
//        command.add("/home/markus/local/bin/test_script.sh");
//        md.monitor("Java", "processed", files, new File("/home/markus/Desktop/joboutput"), true, command);

        Collection<MonitoringConfig> configs = MonitoringConfigParser.parseConfig("/home/markus/src/grisu/grisu-client/monitor.groovy");

        md.monitor(configs);

    }

    private final ServiceInterface si;
    private final RunningJobManager rjm;

    protected final Map<Long, String> log = Collections.synchronizedMap(new TreeMap<Long, String>());
    private final Set<GrisuJob> ignoreList = Collections.synchronizedSet(new HashSet<GrisuJob>());

    private final ExecutorService executor;

    protected boolean visual = false;

    boolean verbose = false;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public MonitoringDaemon(ServiceInterface si) {
        executor = Executors.newFixedThreadPool(10);
        this.si = si;
        this.rjm = RunningJobManagerManager.getDefault(si);

    }

    protected void addLog(String msg) {
        addLog(msg, false);
    }

    abstract protected void addLog(String msg, boolean error);

    public void monitor(MonitoringConfig config) throws IOException, RemoteFileSystemException {
        Set<MonitoringConfig> temp = Sets.newHashSet();
        temp.add(config);
        monitor(temp);
    }


    public void monitor(final Collection<MonitoringConfig> configs) throws IOException, RemoteFileSystemException {
        Map<String, MonitoringConfig> map = Maps.newTreeMap();

        for ( MonitoringConfig config : configs ) {
            for ( String application : config.getApplications() ) {
                if ( map.keySet().contains(application) ) {
                    throw new RuntimeException("Application: "+application+" monitored multiple times.");
                }
                map.put(application, config);
            }
        }
        monitor(map);
    }

    private void monitor(final Map<String, MonitoringConfig> configs) throws IOException, RemoteFileSystemException {


        for (final String application : configs.keySet()) {

            final File targetDir = new File(configs.get(application).getTargetDir());

            addLog("Starting monitoring daemon for application: " + application);

            if (configs.get(application).getFilesToDownload() != null && configs.get(application).getFilesToDownload().size() > 0) {
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

            Thread t = new Thread() {
                public void run() {

                    final EventList<GrisuJob > list = rjm.getJobs(application);
                    final MonitoringConfig config = configs.get(application);

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
                                    String value = j.getJobProperty(config.getProcessedTag(), true);
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

                                        postprocess(j, config.getProcessedTag(), config.getFilesToDownload(), targetDir, config.isDeleteJob(), config.getPostProcessCommand());
                                    }
                                };

                                executor.execute(t);

                            } else {
                                if (verbose) {
                                    addLog("Job '" + j.getJobname() + "': not finished, ignoring for now...");
                                }
                            }

                        }
                        int sec = (ClientPropertiesManager.getDefaultJobStatusRecheckInterval() / 2);
                        myLogger.debug("Sleeping for: "+sec+" seconds.");
                        try {
                            Thread.sleep(sec * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            };

            t.start();

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
