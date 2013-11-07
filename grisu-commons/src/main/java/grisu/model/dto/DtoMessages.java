package grisu.model.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A wrapper that contains a list of {@link grisu.model.dto.DtoDataLocation} objects.
 *
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name = "messages")
public class DtoMessages {


    public static DtoMessages createDtoMessages(Collection<DtoMessage> messages) {

        DtoMessages result = new DtoMessages();
        if ( messages == null ) {
            return result;
        }
        for ( DtoMessage m : messages ) {
            result.addMessage(m);
        }
        return result;
    }


	/**
	 * The list of datalocations.
	 */
	private List<DtoMessage> messages = new LinkedList<DtoMessage>();

	@XmlElement(name = "messages")
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
