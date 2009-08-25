package org.vpac.grisu.model.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="actionStatus")
@Entity
public class DtoActionStatus {
	
	public static final String STARTED = "started";
	public static final String SUCCESS = "success";
	public static final String FAILED = "failed";
	
//	public enum Type {
//		filetransfer,
//		multijobsubmission,
//		multijobcancellation,
//		jobsubmission
//	}
//	
//	public enum Status {
//		started,
//		running,
//		success,
//		failed
//	}
	
	private Long id;
	
	@Id
	@GeneratedValue
	private Long getId() {
		return id;
	}

	private void setId(final Long id) {
		this.id = id;
	}
	
	private String handle;
	private Date startTime;
	private Date endTime;
	
	private String actionType;
	
	private String status;
	
	public DtoActionStatus(String handle, String type) {
		this.handle = handle;
		this.actionType = type;
		this.startTime = new Date();
		this.status = STARTED;
	}
	
	public DtoActionStatus() {
	}

	@XmlElement(name="handle")
	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	@XmlElement(name="starttime")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@XmlElement(name="endtime")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@XmlElement(name="type")
	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
