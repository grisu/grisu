package grisu.model.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "actionStatus")
public class DtoActionStatus {

	public static final String ACTION_STARTED_STRING = "Started";
	public static final String ACTION_FINISHED_STRING = "Finished";
	public static final String ACTION_FAILED_STRING = "Failed";

	public static String getLastMessage(DtoActionStatus status) {

		final DtoLogItem li = status.getLog().get(status.getLog().size() - 1);
		return li.getLogMessage();

	}

	public static String getLogMessagesAsString(DtoActionStatus status) {

		final StringBuffer temp = new StringBuffer();
		for (final DtoLogItem li : status.getLog()) {
			temp.append(li.getLogMessage() + ", ");
		}
		return temp.toString();
	}

	private Map<Date, String> log = Collections
			.synchronizedSortedMap(new TreeMap<Date, String>());

	private int totalElements;
	private boolean finished = false;

	private boolean failed = false;

	private Date lastUpdate = new Date();

	private int currentElements = 0;

	private String handle;

	private String errorCause = null;

	public DtoActionStatus() {
	}

	public DtoActionStatus(String handle, int totalElements) {
		this.handle = handle;
		this.totalElements = totalElements;
		addLogMessage(ACTION_STARTED_STRING);
	}

	public synchronized void addElement(String logMessage) {
		this.currentElements = this.currentElements + 1;
		Date now = new Date();
		String temp = this.log.get(now);
		while (temp != null) {
			// System.err.println("Already taken: "+now);
			now = new Date(now.getTime() + 1);
			temp = this.log.get(now);
		}
		this.log.put(now, logMessage);
		lastUpdate = now;
		// System.out.println("Now "+currentElements+" elements");
	}

	public synchronized void addLogMessage(String logMessage) {
		Date now = new Date();
		String temp = this.log.get(now);
		while (temp != null) {
			// System.err.println("Already taken: "+now);
			now = new Date(now.getTime() + 1);
			temp = this.log.get(now);
		}
		this.log.put(now, logMessage);
		lastUpdate = now;
	}

	@XmlElement(name = "currentElements")
	public int getCurrentElements() {
		return currentElements;
	}

	@XmlElement(name = "errorCause")
	public String getErrorCause() {
		return this.errorCause;
	}

	@XmlAttribute(name = "handle")
	public String getHandle() {
		return handle;
	}

	@XmlAttribute
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@XmlElement(name = "log")
	public List<DtoLogItem> getLog() {

		return DtoLogItem.generateLogItemList(this.log);

	}

	@XmlAttribute(name = "totalElements")
	public int getTotalElements() {
		return totalElements;
	}

	@XmlAttribute
	public boolean isFailed() {
		return failed;
	}

	@XmlAttribute
	public boolean isFinished() {
		return finished;
	}

	public double percentFinished() {
		if (totalElements <= 0) {
			return 0;
		}
		final double result = (currentElements * 100) / totalElements;
		if (result > 100.0) {
			return 100.0;
		} else {
			return result;
		}
	}

	public void setCurrentElements(int currentElements) {
		this.currentElements = currentElements;
	}

	public void setErrorCause(String cause) {
		this.errorCause = cause;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
		addLogMessage(ACTION_FAILED_STRING);
		lastUpdate = new Date();
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
		addLogMessage(ACTION_FINISHED_STRING);
		lastUpdate = new Date();
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setLog(List<DtoLogItem> log) {
		this.log = DtoLogItem.generateLogMap(log);
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

}
