

package org.vpac.grisu.fs.model;

/**
 * Not used yet.
 * 
 * @author Markus Binsteiner
 *
 */
public class FileTransfer {
	
	public static final int SUCCESS = 100;
	public static final int FAILED = -1;
	public static final int PENDING = 0;
	
	private Long id = null;
	
	private User user = null;
	
	private String sourceURL = null;
	private String targetURL = null;
	
	private int status = -1;
	
	public FileTransfer() {
	}
	
	public FileTransfer(User user, String source, String target){
		this.user = user;
		this.sourceURL = source;
		this.targetURL = target;
	}

	private Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	private void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public String getTargetURL() {
		return targetURL;
	}

	private void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}

	public User getUser() {
		return user;
	}

	private void setUser(User user) {
		this.user = user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
