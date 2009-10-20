package org.vpac.grisu.model.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="actionStatus")
public class DtoActionStatus {
	
	public static final String ACTION_STARTED_STRING = "Started";
	public static final String ACTION_FINISHED_STRING = "Finished";
	public static final String ACTION_FAILED_STRING = "Failed";
	
	private Map<Date, String> log =  Collections.synchronizedSortedMap(new TreeMap<Date, String>());
	
	private int totalElements;
	
	private boolean finished = false;
	private boolean failed = false;
	
	private Date lastUpdate = new Date();
	
	@XmlAttribute
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@XmlAttribute
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
		addLogMessage(ACTION_FINISHED_STRING);
		lastUpdate = new Date();
	}

	@XmlAttribute
	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
		addLogMessage(ACTION_FAILED_STRING);
		lastUpdate = new Date();
	}

	@XmlAttribute(name="totalElements")
	public int getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	@XmlElement(name="currentElements")
	public int getCurrentElements() {
		return currentElements;
	}

	public void setCurrentElements(int currentElements) {
		this.currentElements = currentElements;
	}
	
	public synchronized void addElement(String logMessage) {
		this.currentElements = this.currentElements + 1;
		Date now = new Date();
		String temp = this.log.get(now);
		while ( temp != null ) {
//			System.err.println("Already taken: "+now);
			now = new Date(now.getTime()+1);
			temp = this.log.get(now);
		}
		this.log.put(now, logMessage);
		lastUpdate = now;
//		System.out.println("Now "+currentElements+" elements");
	}
	
	public synchronized void addLogMessage(String logMessage) {
		Date now = new Date();
		String temp = this.log.get(now);
		while ( temp != null ) {
//			System.err.println("Already taken: "+now);
			now = new Date(now.getTime()+1);
			temp = this.log.get(now);
		}
		this.log.put(now, logMessage);
		lastUpdate = now;
	}

	private int currentElements = 0;
	
	private String handle;

	@XmlElement(name="log")
	public List<DtoLogItem> getLog() {

		return DtoLogItem.generateLogItemList(this.log);
		
	}

	public void setLog(List<DtoLogItem> log) {
		this.log = DtoLogItem.generateLogMap(log);
	}

	@XmlAttribute(name="handle")
	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public DtoActionStatus(){
	}
	
	public DtoActionStatus(String handle, int totalElements) {
		this.handle = handle;
		this.totalElements = totalElements;
		addLogMessage(ACTION_STARTED_STRING);
	}
	
	public int percentFinished() {
		return (currentElements*100)/totalElements;
	}
	
	
}
