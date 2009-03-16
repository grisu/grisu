

package org.vpac.grisu.fs.model;

import java.util.Date;

/**
 * Not used yet.
 * 
 * @author Markus Binsteiner
 *
 */
public class FileReservation {

	public final static Integer VALID = 0;
	public final static Integer EXPIRED = 1;
	public final static Integer DISABLED = 2;
	public final static Integer FINISHED = 3;
	
	private Long id = null;
	
	private User user = null;
	
	private String file_url = null;
	private Long file_size = null;
	private Integer status = null;
	private Date valid_until = null;
	
	// for hibernate
	private FileReservation(){
		this(null, new Long(0), null);
	}

	/**
	 * Default constructor for a FileReservation. If you don't want an end date, give null.
	 * @param file_url the filename of the "to-be-created" file
	 * @param file_size the expected filesize of the "to-be-created" file
	 * @param valid_until an enddate on which this FileReservation will expire (or null if you don't want it to expire)
	 */
	public FileReservation(String file_url, Long file_size, Date valid_until){
		this.status = -1;
		this.file_url = file_url;
		this.file_size = file_size;
		this.status = getStatus();
		this.valid_until = valid_until;
	}

	public Long getFile_size() {
		return file_size;
	}

	private void setFile_size(Long file_size) {
		this.file_size = file_size;
	}

	public String getFile_url() {
		return file_url;
	}

	private void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	private Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public Integer getStatus() {
		Date now = new Date();
		if ( valid_until != null && now.after(valid_until) ){
			status = EXPIRED;
		} else if ( status == EXPIRED ) {
			status = VALID;
		}
		return status;
	}

	public void setStatus(Integer status) {

		//TODO check for status changed
		
		Date now = new Date();
		if ( valid_until != null && now.after(valid_until) ) {
			status = EXPIRED;
			return;
		} 
		this.status = status;
	}

	public Date getValid_until() {
		return valid_until;
	}

	public void setValid_until(Date valid_until) {
		this.valid_until = valid_until;
		getStatus();
	}

	
	public User getUser() {
		return user;
	}

	private void setUser(User user) {
		this.user = user;
	}
	
}
