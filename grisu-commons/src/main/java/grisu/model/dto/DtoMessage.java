package grisu.model.dto;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "message")
@Entity
public class DtoMessage implements Comparable<DtoMessage> {


    private Date date = new Date();
    private String title = null;
    private boolean read = false;
    private String message = "";
    private List<String> attachments = Lists.newArrayList();
    private Long id;

    private DtoMessage() {
    }

    public DtoMessage(String title) {
        this.title = title;
    }

    public DtoMessage(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    @Id
    @GeneratedValue
    private Long getId() {
        return this.id;
    }

    // hibernate
	private void setId(final Long id) {
		this.id = id;
	}

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    @XmlAttribute(name = "title")
    @Column(nullable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute(name = "read")
    @Column(nullable = true)
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @XmlAttribute
    @Column(nullable = true)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement(name = "attachments")
    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> getAttachments() {

        return attachments;

    }

    public void setAttachments(List<String> attachemnts) {
        this.attachments = attachemnts;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DtoMessage)) {
            return false;
        }
        DtoMessage o = (DtoMessage) other;
        if (getDate().equals(((DtoMessage) other).getDate()) && getTitle().equalsIgnoreCase(o.getTitle())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDate(), getTitle());
    }

    @Override
    public int compareTo(DtoMessage o) {
        int i = getDate().compareTo(o.getDate());
        if (i == 0) {
            return o.getTitle().compareToIgnoreCase(o.getTitle());
        } else {
            return i;
        }
    }

    @Override
    public String toString() {
        return getTitle();
    }

}
