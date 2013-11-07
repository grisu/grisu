package grisu.model.dto;

import grisu.model.info.dto.DtoStringList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement(name = "message")
public class DtoMessage {


	private Date date = new Date();

	private String title = null;

    private boolean read = false;

	private String message = "";

    private DtoStringList attachments = null;

	public DtoMessage() {
	}

	public DtoMessage(String title) {
		this.title = title;
	}


    public DtoMessage(String title, Date date) {
        this.title = title;
        this.date = date;
    }

	@XmlElement(name = "message")
	public String getMessage() {
		return message;
	}

	@XmlAttribute(name = "title")
	public String getTitle() {
		return title;
	}

    @XmlAttribute(name = "read")
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

	@XmlAttribute
	public Date getDate() {
		return date;
	}

	@XmlElement(name = "attachments")
	public DtoStringList getAttachments() {

		return attachments;

	}

	public void setMessage(String msg) {
        this.message = msg;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setAttachments(DtoStringList attachemnts) {
		this.attachments = attachemnts;
	}

}
