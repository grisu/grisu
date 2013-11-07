package grisu.model.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 * A wrapper that contains a list of {@link grisu.model.dto.DtoDataLocation} objects.
 *
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name = "datalocations")
public class DtoMessages {


	/**
	 * The list of datalocations.
	 */
	private List<DtoMessage> messages = new LinkedList<DtoMessage>();

	@XmlElement(name = "datalocation")
	public List<DtoMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<DtoMessage> messages) {
		this.messages = messages;
	}

    public void addMessage(DtoMessage msg) {
        messages.add(msg);
    }

}
