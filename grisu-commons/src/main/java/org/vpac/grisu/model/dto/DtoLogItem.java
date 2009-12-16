package org.vpac.grisu.model.dto;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class DtoLogItem {

	public static List<DtoLogItem> generateLogItemList(Map<Date, String> map) {

		List<DtoLogItem> result = new LinkedList<DtoLogItem>();

		for (Date key : map.keySet()) {
			result.add(new DtoLogItem(key, map.get(key)));
		}

		return result;
	}

	public static Map<Date, String> generateLogMap(List<DtoLogItem> list) {

		Map<Date, String> result = Collections
				.synchronizedMap(new TreeMap<Date, String>());

		for (DtoLogItem item : list) {
			result.put(item.getTime(), item.getLogMessage());
		}

		return result;

	}

	private Date time;
	private String logMessage;

	public DtoLogItem() {
	}

	public DtoLogItem(Date time, String logMessage) {
		this.time = time;
		this.logMessage = logMessage;
	}

	@XmlValue
	public String getLogMessage() {
		return logMessage;
	}

	@XmlAttribute
	public Date getTime() {
		return time;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
