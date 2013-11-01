package grisu.frontend.control;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 23/09/13
 * Time: 3:24 PM
 */
public class MonitoringConfig {

    private List<String> applications = null;
    private String processedTag = UUID.randomUUID().toString();

    private Collection<String> filesToDownload = null;
    private String targetDir = null;
    private boolean deleteJob = false;
    private List<String> postProcessCommand = null;

    private MonitoringConfig() {

    }

//    private MonitoringConfig(List<String> applications, String processedTag, Collection<String> filesToDownload, File targetDir, boolean deleteJob, List<String> postProcessCommand) {
//        this.applications = applications;
//        this.processedTag = processedTag;
//        this.filesToDownload = filesToDownload;
//        this.targetDir = targetDir;
//        this.deleteJob = deleteJob;
//        this.postProcessCommand = postProcessCommand;
//    }

    public String getTargetDir() {
        if ( targetDir == null ) {
            return System.getProperty("user.dir");
        } else {
            return targetDir;
        }
    }

    public List<String> getApplications() {
        return applications;
    }

    public String getProcessedTag() {
        return processedTag;
    }

    public Collection<String> getFilesToDownload() {
        return filesToDownload;
    }

    public boolean isDeleteJob() {
        return deleteJob;
    }

    public List<String> getPostProcessCommand() {
        return postProcessCommand;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public void setProcessedTag(String processedTag) {
        this.processedTag = processedTag;
    }

    public void setFilesToDownload(Collection<String> filesToDownload) {
        this.filesToDownload = filesToDownload;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public void setDeleteJob(boolean deleteJob) {
        this.deleteJob = deleteJob;
    }

    public void setPostProcessCommand(List<String> postProcessCommand) {
        this.postProcessCommand = postProcessCommand;
    }
}
