package org.vpac.grisu.model.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="logMessages")
public class DtoLogMessages {
	
	List<DtoLogMessage> messages = new LinkedList<DtoLogMessage>();
	
	public static DtoLogMessages createLogMessages(Map<Long, String> messages) {
		
		DtoLogMessages result = new DtoLogMessages();
		for ( Long time : messages.keySet() ) {
			Date date = new Date(time);
			result.addMessage(date, messages.get(time));
		}
		return result;
	}

	@XmlElement(name="message")
	public List<DtoLogMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<DtoLogMessage> messages) {
		this.messages = messages;
	}
	
	public void addMessage(DtoLogMessage message) {
		this.messages.add(message);
	}
	
	public void addMessage(Date date, String message) {
		addMessage(new DtoLogMessage(date, message));
	}
	

}
