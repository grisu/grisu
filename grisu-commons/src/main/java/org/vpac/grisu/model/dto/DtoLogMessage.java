package org.vpac.grisu.model.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "logMessage")
public class DtoLogMessage {

	private Date date;
	private String message;

	public DtoLogMessage() {
	}

	public DtoLogMessage(Date date, String message) {
		this.date = date;
		this.message = message;
	}

	@XmlAttribute
	public Date getDate() {
		return date;
	}

	@XmlValue
	public String getMessage() {
		return message;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
