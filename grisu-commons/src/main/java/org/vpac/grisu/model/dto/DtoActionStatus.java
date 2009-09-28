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
	
	private Map<Date, String> log =  Collections.synchronizedSortedMap(new TreeMap<Date, String>());
	
	private int totalElements;
	
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
		this.log.put(new Date(), logMessage);
	}
	
	public synchronized void addLogMessage(String logMessage) {
		this.log.put(new Date(), logMessage);
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
		log.put(new Date(), ACTION_STARTED_STRING);
	}
	
	public int percentFinished() {
		return (currentElements*100)/totalElements;
	}
	
	public boolean finished() {
		return currentElements == totalElements;
	}
	
}
