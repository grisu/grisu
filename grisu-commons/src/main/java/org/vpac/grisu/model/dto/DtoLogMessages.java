package org.vpac.grisu.model.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "logMessages")
public class DtoLogMessages {

	public static DtoLogMessages createLogMessages(Map<Long, String> messages) {

		final DtoLogMessages result = new DtoLogMessages();
		for (final Long time : messages.keySet()) {
			final Date date = new Date(time);
			result.addMessage(date, messages.get(time));
		}
		return result;
	}

	List<DtoLogMessage> messages = new LinkedList<DtoLogMessage>();

	public void addMessage(Date date, String message) {
		addMessage(new DtoLogMessage(date, message));
	}

	public void addMessage(DtoLogMessage message) {
		this.messages.add(message);
	}

	public Map<Date, String> asMap() {

		final Map<Date, String> temp = new TreeMap<Date, String>();

		for (final DtoLogMessage msg : getMessages()) {
			temp.put(msg.getDate(), msg.getMessage());
		}
		return temp;
	}

	@XmlElement(name = "message")
	public List<DtoLogMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<DtoLogMessage> messages) {
		this.messages = messages;
	}

}
